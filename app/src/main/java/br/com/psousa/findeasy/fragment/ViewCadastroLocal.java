package br.com.psousa.findeasy.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import br.com.psousa.findeasy.FindEasyApplication;
import br.com.psousa.findeasy.R;
import br.com.psousa.findeasy.activity.PrincipalActivity;
import br.com.psousa.findeasy.domain.modelo.CategoriaLocal;
import br.com.psousa.findeasy.domain.modelo.Local;
import br.com.psousa.findeasy.service.CategoriaLocalJson;
import br.com.psousa.findeasy.service.LocalJson;
import br.com.psousa.findeasy.util.AndroidUtils;
import br.com.psousa.findeasy.util.Mask;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewCadastroLocal extends Fragment {

    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";

    public ViewCadastroLocal() {
        // Required empty public constructor
    }

    public static ViewCadastroLocal newInstance(double longitude, double latitude){
        ViewCadastroLocal fragment = new ViewCadastroLocal();
        Bundle args = new Bundle();
        args.putDouble(LONGITUDE, longitude);
        args.putDouble(LATITUDE, latitude);
        fragment.setArguments(args);
        return fragment;
    }

    private OnViewCadastroLocalListener mListener;
    private EditText mEdtLatitude;
    private EditText mEdtLongitude;
    private EditText mEdtDescricao;
    private EditText mEdtTelefone;
    private EditText mEdtEndereco;
    private AppCompatSpinner mSpnCategoria;
    private TextWatcher telefone1Mask;
    private Boolean maskOriginal = true, convertTel = true;
    private List<CategoriaLocal> mCategorias;
    private ProgressDialog load;
    private CategoriaLocal mCategoria;
    double mLongitude;
    double mLatitude;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle args = getArguments();
            mLongitude = args.getDouble(LONGITUDE);
            mLatitude = args.getDouble(LATITUDE);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view =  inflater.inflate(R.layout.view_cadastro_local, container, false);
        // Inflate the layout for this fragment

        mEdtLatitude = (EditText) view.findViewById(R.id.edt_cadastro_local_latitude);
        mEdtLongitude = (EditText) view.findViewById(R.id.edt_cadastro_local_longitude);
        mEdtDescricao = (EditText) view.findViewById(R.id.edt_cadastro_local_descricao);
        mEdtTelefone = (EditText) view.findViewById(R.id.edt_cadastro_local_telefone);
        mEdtEndereco = (EditText) view.findViewById(R.id.edt_cadastro_local_endereco);
        mSpnCategoria = (AppCompatSpinner) view.findViewById(R.id.edt_cadastro_local_categoria);

        telefone1Mask = Mask.insert("(##) ####-####", mEdtTelefone);
        mEdtTelefone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                alteraMascaraTelefone(s, mEdtTelefone);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mEdtTelefone.addTextChangedListener(telefone1Mask);

        setLocalizacao(mLongitude, mLatitude);

        new GetCarregarCategoriaJson(getContext(), FindEasyApplication.getInstance().getToken()).execute();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_cadastro_usuario, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_salvar) {
            Local local = criaObjetoComDados();
            try {
                 validar(local);
                 new PostLocalJson(local).execute();
            } catch (Exception e){
                AndroidUtils.alertDialog(getContext(), getString(R.string.atencao), e.getMessage());
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private class GetCarregarCategoriaJson extends AsyncTask<Void, Void, List<CategoriaLocal>> {
        private String msgException;
        private String token;
        private Context context;

        public GetCarregarCategoriaJson(Context context, String token) {
            this.token = token;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            load = ProgressDialog.show(context, getString(R.string.aguarde), getString(R.string.comunicando_com_servidor));
        }

        @Override
        protected List<CategoriaLocal> doInBackground(Void... params) {
            CategoriaLocalJson json = new CategoriaLocalJson(context);
            List<CategoriaLocal> retorno = null;
            try {
                retorno = json.getCategorias(AndroidUtils.URL_CATEGORIA_LOCAL, token);
            } catch (Exception e) {
                msgException = e.getMessage();
            }
            return retorno;
        }

        @Override
        protected void onPostExecute(List<CategoriaLocal> retorno) {
            if (retorno != null) {
                if (retorno.size() > 0) {
                    mCategorias = retorno;
                    updateCategorias();
                    load.dismiss();
                } else {
                    AndroidUtils.alertDialog(context, getString(R.string.atencao), getString(R.string.erro_ao_comunicar_com_servidor));
                    load.dismiss();
                }
            } else if (msgException != null && !msgException.isEmpty()) {
                load.dismiss();
                AndroidUtils.alertDialog(context, getString(R.string.atencao), msgException, new AndroidUtils.OnClickAlertDialod() {
                    @Override
                    public void onOkClick() {
                        getActivity().finish();
                    }
                });
            } else {
                AndroidUtils.alertDialog(context, getString(R.string.atencao), getString(R.string.erro_ao_comunicar_com_servidor), new AndroidUtils.OnClickAlertDialod() {
                    @Override
                    public void onOkClick() {
                        getActivity().finish();
                    }
                });
                load.dismiss();
            }
        }
    }

    private class PostLocalJson extends AsyncTask<Void, Void, Local> {
        private String msgException;
        private Local local;

        public PostLocalJson(Local local){
            this.local = local;
        }

        @Override
        protected void onPreExecute() {
            load = ProgressDialog.show(getContext(), getString(R.string.aguarde), getString(R.string.cadastrando_local));
        }

        @Override
        protected Local doInBackground(Void... params) {
            LocalJson json = new LocalJson(getContext());
            Local retorno = null;
            try {
                retorno = json.postLocal(AndroidUtils.URL_LOCAL, local, FindEasyApplication.getInstance().getToken());
            } catch (Exception e) {
                msgException = e.getMessage();
            }
            return retorno;
        }

        public void fecharActivity(Long codigo){
            if (mListener != null) {
                mListener.onClose(codigo);
            }
        }

        @Override
        protected void onPostExecute(Local retorno) {
            if (retorno != null) {
                if (retorno.getCodigo() != null) {
                    load.dismiss();
                    final Long codigo = retorno.getCodigo();
                    AndroidUtils.alertDialog(getContext(), getString(R.string.atencao), getString(R.string.local_cadastrado_sucesso), new AndroidUtils.OnClickAlertDialod() {
                        @Override
                        public void onOkClick() {
                            fecharActivity(codigo);
                        }
                    });
                } else {
                    AndroidUtils.alertDialog(getContext(), getString(R.string.atencao), getString(R.string.erro_ao_cadastrar_local));
                    load.dismiss();
                }
            } else if (msgException != null && !msgException.isEmpty()) {
                load.dismiss();
                AndroidUtils.alertDialog(getContext(), getString(R.string.atencao), msgException);
            } else {
                AndroidUtils.alertDialog(getContext(), getString(R.string.atencao), getString(R.string.erro_ao_cadastrar_local));
                load.dismiss();
            }
        }
    }

    public void setLocalizacao(double longitude, double latitude){
        mEdtLongitude.setText(String.valueOf(longitude));
        mEdtLatitude.setText(String.valueOf(latitude));

        Geocoder geocoder;
        List<Address> addresses;
        try {
            geocoder = new Geocoder(getContext(), Locale.getDefault());
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            mEdtEndereco.setText(addresses.get(0).getAddressLine(0));
        } catch (IOException e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void updateCategorias(){
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, mCategorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnCategoria.setAdapter(adapter);
        mSpnCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                mCategoria = mCategorias.get(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void validar(Local local) throws Exception {
        if (local.getDescricao() == null ||
                local.getDescricao().isEmpty()){
            throw new Exception(getString(R.string.informe_descricao_local));
        }
        if (local.getTelefone() == null ||
                local.getTelefone().isEmpty()){
            throw new Exception(getString(R.string.informe_telefone));
        }
        if (local.getCategoria() == null ||
                local.getCategoria().getDescricao().isEmpty()){
            throw new Exception(getString(R.string.informe_categoria));
        }
        if (local.getEndereco() == null ||
                local.getEndereco().isEmpty()){
            throw new Exception(getString(R.string.informe_endereco));
        }

        if (local.getLatitude() == null ||
                local.getLatitude().isEmpty() ||
                local.getLongitude() == null ||
                local.getLongitude().isEmpty()){
            throw new Exception(getString(R.string.informe_localizacao));
        }
    }

    public Local criaObjetoComDados(){
        Local local = new Local();
        local.setDescricao(mEdtDescricao.getText().toString());
        local.setAtivo(true);
        local.setCategoria(mCategoria);
        local.setEndereco(mEdtEndereco.getText().toString());
        local.setLatitude(mEdtLatitude.getText().toString());
        local.setLongitude(mEdtLongitude.getText().toString());
        local.setNivelBusca(0);
        local.setTelefone(mEdtTelefone.getText().toString());
        local.setUsuario(FindEasyApplication.getInstance().getUsuarioLogado());
        return local;
    }

    private void alteraMascaraTelefone(CharSequence s, EditText edt) {
        if (mEdtTelefone.getText().toString().length() == 15 && convertTel) {
            convertTel = false;
            maskOriginal = false;
            edt.removeTextChangedListener(telefone1Mask);
            telefone1Mask = Mask.insert("(##) #####-####", edt);
            edt.addTextChangedListener(telefone1Mask);
            edt.setText(s);
        } else {
            if (mEdtTelefone.getText().toString().length() == 14 && !maskOriginal) {
                maskOriginal = true;
                convertTel = true;
                edt.removeTextChangedListener(telefone1Mask);
                telefone1Mask = Mask.insert("(##) ####-####", edt);
                edt.addTextChangedListener(telefone1Mask);
                edt.setText(s);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnViewCadastroLocalListener) {
            mListener = (OnViewCadastroLocalListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnViewCadastroLocalListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnViewCadastroLocalListener {
        // TODO: Update argument type and name
        void onClose(Long codigo);
    }

}
