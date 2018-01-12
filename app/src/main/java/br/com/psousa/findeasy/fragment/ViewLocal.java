package br.com.psousa.findeasy.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.psousa.findeasy.FindEasyApplication;
import br.com.psousa.findeasy.R;
import br.com.psousa.findeasy.domain.modelo.Local;
import br.com.psousa.findeasy.service.LocalJson;
import br.com.psousa.findeasy.util.AndroidUtils;

public class ViewLocal extends Fragment {

    private ProgressDialog load;

    private TextView mTxtNome;
    private TextView mTxtEndereco;
    private TextView mTxtTelefone;
    private TextView mTxtCategoria;

    public ViewLocal() {
        // Required empty public constructor
    }


    public static ViewLocal newInstance() {
        ViewLocal fragment = new ViewLocal();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    public void setCodigo(Long codigo){
        new GetCarregarLocalJson(getContext(),
                                 codigo,
                                 FindEasyApplication.getInstance().getToken()).execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.view_local, container, false);

        mTxtNome = (TextView) view.findViewById(R.id.edt_view_local_nome);
        mTxtEndereco = (TextView) view.findViewById(R.id.edt_view_local_endereco);
        mTxtTelefone = (TextView) view.findViewById(R.id.edt_view_local_telefone);
        mTxtCategoria = (TextView) view.findViewById(R.id.edt_view_local_categoria);

        return view;
    }

    private class GetCarregarLocalJson extends AsyncTask<Void, Void, Local> {
        private String msgException;
        private String token;
        private Long codigo;
        private Context context;

        public GetCarregarLocalJson(Context context, Long codigo, String token) {
            this.token = token;
            this.context = context;
            this.codigo = codigo;
        }

        @Override
        protected void onPreExecute() {
            load = ProgressDialog.show(context, getString(R.string.aguarde), getString(R.string.comunicando_com_servidor));
        }

        @Override
        protected Local doInBackground(Void... params) {
            LocalJson json = new LocalJson(context);
            Local retorno = null;
            try {
                retorno = json.getLocalCodigo(AndroidUtils.URL_LOCAL+"/"+
                        codigo, token);
            } catch (Exception e) {
                msgException = e.getMessage();
            }
            return retorno;
        }

        @Override
        protected void onPostExecute(Local retorno) {
            if (retorno != null) {
                updateInfo(retorno);
                load.dismiss();
            } else if (msgException != null && !msgException.isEmpty()) {
                load.dismiss();
                AndroidUtils.alertDialog(context, getString(R.string.atencao), msgException);
            } else {
                AndroidUtils.alertDialog(context, getString(R.string.atencao), getString(R.string.erro_ao_comunicar_com_servidor));
                load.dismiss();
            }
        }

        private void updateInfo(Local local){
            mTxtNome.setText(local.getDescricao());
            mTxtEndereco.setText(local.getEndereco());
            mTxtTelefone.setText(local.getTelefone());
            mTxtCategoria.setText(local.getCategoria().getDescricao());
        }
    }


}
