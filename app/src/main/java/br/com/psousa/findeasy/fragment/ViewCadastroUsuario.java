package br.com.psousa.findeasy.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.psousa.findeasy.R;
import br.com.psousa.findeasy.domain.modelo.Usuario;
import br.com.psousa.findeasy.service.UsuarioJson;
import br.com.psousa.findeasy.util.AndroidUtils;
import br.com.psousa.findeasy.util.Mask;
import livroandroid.lib.fragment.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewCadastroUsuario extends BaseFragment {

    private EditText mEdtNome;
    private EditText mEdtEmail;
    private EditText mEdtCelular;
    private EditText mEdtSenha;
    private ProgressDialog load;
    private TextWatcher telefone1Mask;
    private Boolean maskOriginal = true, convertTel = true;


    public ViewCadastroUsuario() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.view_cadastro_usuario, container, false);

        mEdtNome = (EditText) view.findViewById(R.id.edt_cadastro_nome);
        mEdtCelular = (EditText) view.findViewById(R.id.edt_cadsatro_celular);
        mEdtEmail = (EditText) view.findViewById(R.id.edt_cadsatro_email);
        mEdtSenha = (EditText) view.findViewById(R.id.edt_cadsatro_senha);

        telefone1Mask = Mask.insert("(##) ####-####", mEdtCelular);
        mEdtCelular.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                alteraMascaraTelefone(s, mEdtCelular);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mEdtCelular.addTextChangedListener(telefone1Mask);



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
            Usuario usu = criaObjetoComDados();
            try {
                validar(usu);
                new PostUsuarioJson(usu).execute();
            } catch (Exception e){
                AndroidUtils.alertDialog(getContext(), getString(R.string.atencao), e.getMessage());
            }

        }
        return super.onOptionsItemSelected(item);
    }

    public void validar(Usuario usuario) throws Exception {
        if (usuario.getNome() == null ||
                usuario.getNome().isEmpty()){
            throw new Exception(getString(R.string.informe_nome_usuario));
        }
        if (usuario.getCelular() == null ||
                usuario.getCelular().isEmpty()){
            throw new Exception(getString(R.string.informe_celular));
        }
        if (usuario.getEmail() == null ||
                usuario.getEmail().isEmpty()){
            throw new Exception(getString(R.string.informe_email));
        } else {
            if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(usuario.getEmail().trim()).matches() && usuario.getEmail().trim().length() > 0)) {
                  throw new Exception(getString(R.string.email_invalido));
            }
        }
        if (usuario.getSenha() == null ||
                usuario.getSenha().isEmpty()){
            throw new Exception(getString(R.string.informe_senha));
        }
    }

    public Usuario criaObjetoComDados(){
        Usuario usuario = new Usuario();
        usuario.setNome(mEdtNome.getText().toString());
        usuario.setAtivo(true);
        usuario.setCelular(mEdtCelular.getText().toString());
        usuario.setEmail(mEdtEmail.getText().toString());
        usuario.setSenha(AndroidUtils.md5(mEdtSenha.getText().toString()));
        return usuario;
    }

    public void fecharActivity(){
        getActivity().finish();
    }

    private class PostUsuarioJson extends AsyncTask<Void, Void, Usuario> {
        private int tentativa;
        private String msgException;
        private Usuario usuario;

        public PostUsuarioJson(Usuario usuario){
            this.usuario = usuario;
        }

        @Override
        protected void onPreExecute() {
            load = ProgressDialog.show(getContext(), getString(R.string.aguarde), getString(R.string.cadastrando_usuario));
        }

        @Override
        protected Usuario doInBackground(Void... params) {
            UsuarioJson json = new UsuarioJson(getContext());
            Usuario retorno = null;
            try {
                retorno = json.postUsuario(AndroidUtils.URL_USUARIO, usuario);
            } catch (Exception e) {
                msgException = e.getMessage();
            }
            return retorno;
        }

        @Override
        protected void onPostExecute(Usuario retorno) {
            if (retorno != null) {
                if (retorno.getCodigo() != null) {
                    load.dismiss();
                    AndroidUtils.alertDialog(getContext(), getString(R.string.atencao), getString(R.string.usuario_cadastrado_sucesso), new AndroidUtils.OnClickAlertDialod() {
                        @Override
                        public void onOkClick() {
                            fecharActivity();
                        }
                    });
                } else {
                    AndroidUtils.alertDialog(getContext(), getString(R.string.atencao), getString(R.string.erro_ao_cadastrar_usuario));
                    load.dismiss();
                }
            } else if (msgException != null && !msgException.isEmpty()) {
                load.dismiss();
                AndroidUtils.alertDialog(getContext(), getString(R.string.atencao), msgException);
            } else {
                AndroidUtils.alertDialog(getContext(), getString(R.string.atencao), getString(R.string.erro_ao_cadastrar_usuario));
                load.dismiss();
            }
        }
    }

    private void alteraMascaraTelefone(CharSequence s, EditText edt) {
        if (mEdtCelular.getText().toString().length() == 15 && convertTel) {
            convertTel = false;
            maskOriginal = false;
            edt.removeTextChangedListener(telefone1Mask);
            telefone1Mask = Mask.insert("(##) #####-####", edt);
            edt.addTextChangedListener(telefone1Mask);
            edt.setText(s);
        } else {
            if (mEdtCelular.getText().toString().length() == 14 && !maskOriginal) {
                maskOriginal = true;
                convertTel = true;
                edt.removeTextChangedListener(telefone1Mask);
                telefone1Mask = Mask.insert("(##) ####-####", edt);
                edt.addTextChangedListener(telefone1Mask);
                edt.setText(s);
            }
        }
    }

}
