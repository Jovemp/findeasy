package br.com.psousa.findeasy.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import br.com.psousa.findeasy.FindEasyApplication;
import br.com.psousa.findeasy.R;
import br.com.psousa.findeasy.domain.modelo.Usuario;
import br.com.psousa.findeasy.service.RecuperaSenhaJson;
import br.com.psousa.findeasy.service.UsuarioJson;
import br.com.psousa.findeasy.util.AndroidUtils;
import br.com.psousa.findeasy.util.PermissaoAndroid;

public class LoginActivity extends AppCompatActivity {

    private TextView mTxtNaoSouCadastrado;
    private TextView mTxtEsqueciASenha;

    private EditText mEdtEmail;
    private EditText mEdtSenha;

    private Button mBtnLogar;

    private ProgressDialog load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mTxtNaoSouCadastrado = (TextView) findViewById(R.id.lb_login_nao_sou_cadastrado);

        mTxtNaoSouCadastrado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, CadastroUsuarioActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        mTxtEsqueciASenha = (TextView) findViewById(R.id.lb_login_esqueci_a_senha);

        mTxtEsqueciASenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RecuperaSenhaActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        mEdtEmail = (EditText) findViewById(R.id.edt_login_email);
        mEdtSenha = (EditText) findViewById(R.id.edt_login_senha);
        mBtnLogar = (Button) findViewById(R.id.btn_login_entrar);

        mBtnLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logar();
            }
        });

        if (FindEasyApplication.getInstance().getUsuarioLogado() != null ){
            Intent it = new Intent(LoginActivity.this, PrincipalActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(it);
            fechar();
        }

        PermissaoAndroid.validate(this, PermissaoAndroid.REQUEST_PERMISSIONS_CODE, this, PermissaoAndroid.permissoes);

    }

    private void fechar(){
        finish();
    }

    private void logar(){
        if (mEdtEmail == null ||
                mEdtEmail.getText().toString().isEmpty()){
            AndroidUtils.alertDialog(this, getString(R.string.atencao), getString(R.string.informe_email));
        } else {
            if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(mEdtEmail.getText().toString().trim()).matches() && mEdtEmail.getText().toString().trim().length() > 0)) {
                AndroidUtils.alertDialog(this, getString(R.string.atencao), getString(R.string.email_invalido));
            } else {
                if (mEdtSenha == null ||
                        mEdtSenha.getText().toString().isEmpty()){
                    AndroidUtils.alertDialog(this, getString(R.string.atencao), getString(R.string.informe_senha));
                } else {
                    if (!(mEdtSenha.getText().toString().trim().length() > 5)) {
                        AndroidUtils.alertDialog(this, getString(R.string.atencao), getString(R.string.senha_curta));
                    } else {
                        new PostLoginJson( mEdtEmail.getText().toString().trim(), AndroidUtils.md5(mEdtSenha.getText().toString().trim())).execute();
                    }
                }
            }
        }
    }

    private class PostLoginJson extends AsyncTask<Void, Void, String> {
        private String msgException;
        private String email;
        private String senha;

        public PostLoginJson(String email, String senha){
            this.email = email;
            this.senha = senha;
        }

        @Override
        protected void onPreExecute() {
            load = ProgressDialog.show(LoginActivity.this, getString(R.string.aguarde), getString(R.string.comunicando_com_servidor));
        }

        @Override
        protected String doInBackground(Void... params) {
            UsuarioJson json = new UsuarioJson(LoginActivity.this);
            String retorno = null;
            try {
                retorno = json.getLogin(AndroidUtils.URL_TOKEN, email, senha);
            } catch (Exception e) {
                msgException = e.getMessage();
            }
            return retorno;
        }

        @Override
        protected void onPostExecute(String retorno) {
            if (retorno != null) {
                if (!retorno.isEmpty()) {
                    FindEasyApplication.getInstance().setToken(retorno);
                    load.dismiss();
                    Intent it = new Intent(LoginActivity.this, PrincipalActivity.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(it);
                    fechar();
                } else {
                    AndroidUtils.alertDialog(LoginActivity.this, getString(R.string.atencao), getString(R.string.email_ou_senha_invalida));
                    load.dismiss();
                }
            } else if (msgException != null && !msgException.isEmpty()) {
                load.dismiss();
                AndroidUtils.alertDialog(LoginActivity.this, getString(R.string.atencao), msgException);
            } else {
                AndroidUtils.alertDialog(LoginActivity.this, getString(R.string.atencao), getString(R.string.erro_ao_comunicar_com_servidor));
                load.dismiss();
            }
        }
    }
}
