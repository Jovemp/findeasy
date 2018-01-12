package br.com.psousa.findeasy.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.HorizontalStepsViewIndicator;
import com.baoyachi.stepview.bean.StepBean;

import java.util.ArrayList;
import java.util.List;

import br.com.psousa.findeasy.R;
import br.com.psousa.findeasy.domain.modelo.Usuario;
import br.com.psousa.findeasy.fragment.ViewCodigoRecuperaSenha;
import br.com.psousa.findeasy.fragment.ViewEmailRecuperaSenha;
import br.com.psousa.findeasy.fragment.ViewSenhaRecuperaSenha;
import br.com.psousa.findeasy.service.RecuperaSenhaJson;
import br.com.psousa.findeasy.util.AndroidUtils;

public class RecuperaSenhaActivity extends AppCompatActivity {

    private StepBean mStepBean0;
    private StepBean mStepBean1;
    private StepBean mStepBean2;
    private HorizontalStepView mSetpview;
    private static int mProgresso;

    private FragmentManager mFragmentManager;
    private ViewEmailRecuperaSenha viewEmailRecuperaSenha;
    private ViewCodigoRecuperaSenha viewCodigoRecuperaSenha;
    private ViewSenhaRecuperaSenha viewSenhaRecuperaSenha;
    private ProgressDialog load;

    private Button mBtnVoltar, mBtnAvancar;

    private String mEmail = "";
    private String mCodigo = "";
    private Usuario mUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recupera_senha);

        // Adiciona botão de Voltar na barra de Titulo
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fecha a tela ao clicar no botão de voltar
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStepBean0 = new StepBean(getString(R.string.e_mail),0);
        mStepBean1 = new StepBean(getString(R.string.codigo),-1);
        mStepBean2 = new StepBean(getString(R.string.senha),-1);

        mProgresso = 0;

        mSetpview = (HorizontalStepView) findViewById(R.id.step_view);

        preencherProgresso();

        mBtnVoltar = (Button) findViewById(R.id.btn_recupera_senha_voltar);
        mBtnAvancar = (Button) findViewById(R.id.btn_recupera_senha_avancar);

        mBtnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backProgresso();
            }
        });

        mBtnAvancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mProgresso == 0){
                    mEmail = viewEmailRecuperaSenha.getEmail();
                    if (!mEmail.trim().isEmpty()) {
                        new PostRecuperaSenhaJson(mEmail).execute();
                    }
                } else if (mProgresso == 1) {
                    mCodigo = viewCodigoRecuperaSenha.getCodigo();
                    if (!mCodigo.trim().isEmpty()) {
                        new PostRetornaUsuarioJson(mEmail, mCodigo).execute();
                    }
                } else if (mProgresso == 2) {
                    String senha = viewSenhaRecuperaSenha.getSenha();
                    if (!senha.trim().isEmpty()) {
                        mUsuario.setSenha(AndroidUtils.md5(senha));
                        new PostAlteraSenhaJson(mUsuario).execute();
                    }
                }
                //nextProgresso();
            }
        });

        mBtnVoltar.setVisibility(View.INVISIBLE);

        viewEmailRecuperaSenha = new ViewEmailRecuperaSenha();
        viewCodigoRecuperaSenha = new ViewCodigoRecuperaSenha();
        viewSenhaRecuperaSenha = new ViewSenhaRecuperaSenha();

        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction().replace(R.id.container_activity, viewEmailRecuperaSenha).commit();
    }

    private void setProgresso(int index){
        switch (index){
            case 0:
                mStepBean0.setState(0);
                mStepBean1.setState(-1);
                mStepBean2.setState(-1);
                mBtnVoltar.setVisibility(View.INVISIBLE);
                mBtnAvancar.setText(R.string.avan_ar);
                mFragmentManager.beginTransaction().replace(R.id.container_activity, viewEmailRecuperaSenha).commit();
                break;
            case 1:
                mStepBean0.setState(1);
                mStepBean1.setState(0);
                mStepBean2.setState(-1);
                mBtnVoltar.setVisibility(View.VISIBLE);
                mBtnAvancar.setText(R.string.avan_ar);
                mFragmentManager.beginTransaction().replace(R.id.container_activity, viewCodigoRecuperaSenha).commit();
                break;
            case 2:
                mStepBean0.setState(1);
                mStepBean1.setState(1);
                mStepBean2.setState(0);
                mBtnVoltar.setVisibility(View.VISIBLE);
                mBtnAvancar.setText(R.string.concluir);
                mFragmentManager.beginTransaction().replace(R.id.container_activity, viewSenhaRecuperaSenha).commit();
                break;
            default:
                mStepBean0.setState(0);
                mStepBean1.setState(-1);
                mStepBean2.setState(-1);
                mBtnAvancar.setText(R.string.avan_ar);
                mBtnVoltar.setVisibility(View.INVISIBLE);
                break;
        }
        preencherProgresso();
    }

    private void nextProgresso(){
        if (mProgresso < 2){
            mProgresso++;
            setProgresso(mProgresso);
        }
    }

    private void backProgresso(){
        if (mProgresso > 0) {
            mProgresso--;
            setProgresso(mProgresso);
        }
    }

    private void preencherProgresso(){
        List<StepBean> stepsBeanList = new ArrayList<>();

        stepsBeanList.add(mStepBean0);
        stepsBeanList.add(mStepBean1);
        stepsBeanList.add(mStepBean2);

        mSetpview
                .setStepViewTexts(stepsBeanList)//总步骤
                .setTextSize(10)//set textSize
                .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(this, R.color.uncompleted_color))//设置StepsViewIndicator完成线的颜色
                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(this, R.color.uncompleted_text_color))//设置StepsViewIndicator未完成线的颜色
                .setStepViewComplectedTextColor(ContextCompat.getColor(this, R.color.uncompleted_text_color))//设置StepsView text完成线的颜色
                .setStepViewUnComplectedTextColor(ContextCompat.getColor(this, R.color.uncompleted_text_color))//设置StepsView text未完成线的颜色
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(this, R.drawable.complted))//设置StepsViewIndicator CompleteIcon
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(this, R.drawable.default_icon))//设置StepsViewIndicator DefaultIcon
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(this, R.drawable.attention));//设置StepsViewIndicator AttentionIcon
    }

    private class PostRecuperaSenhaJson extends AsyncTask<Void, Void, String> {
        private String msgException;
        private String email;

        public PostRecuperaSenhaJson(String email){
            this.email = email;
        }

        @Override
        protected void onPreExecute() {
            load = ProgressDialog.show(RecuperaSenhaActivity.this, getString(R.string.aguarde), getString(R.string.comunicando_com_servidor));
        }

        @Override
        protected String doInBackground(Void... params) {
            RecuperaSenhaJson json = new RecuperaSenhaJson(RecuperaSenhaActivity.this);
            String retorno = null;
            try {
                retorno = json.postRecuperaSenha(AndroidUtils.URL_USUARIO_RECUPERA_SENHA, email);
            } catch (Exception e) {
                msgException = e.getMessage();
            }
            return retorno;
        }

        @Override
        protected void onPostExecute(String retorno) {
            if (retorno != null) {
                if (retorno.contains("sucesso")) {
                    load.dismiss();
                    nextProgresso();
                } else {
                    AndroidUtils.alertDialog(RecuperaSenhaActivity.this, getString(R.string.atencao), retorno);
                    load.dismiss();
                }
            } else if (msgException != null && !msgException.isEmpty()) {
                load.dismiss();
                AndroidUtils.alertDialog(RecuperaSenhaActivity.this, getString(R.string.atencao), msgException);
            } else {
                AndroidUtils.alertDialog(RecuperaSenhaActivity.this, getString(R.string.atencao), getString(R.string.erro_ao_comunicar_com_servidor));
                load.dismiss();
            }
        }
    }

    private class PostRetornaUsuarioJson extends AsyncTask<Void, Void, Usuario> {
        private String msgException;
        private String email;
        private String codigo;

        public PostRetornaUsuarioJson(String email, String codigo){
            this.email = email;
            this.codigo = codigo;
        }

        @Override
        protected void onPreExecute() {
            load = ProgressDialog.show(RecuperaSenhaActivity.this, getString(R.string.aguarde), getString(R.string.comunicando_com_servidor));
        }

        @Override
        protected Usuario doInBackground(Void... params) {
            RecuperaSenhaJson json = new RecuperaSenhaJson(RecuperaSenhaActivity.this);
            Usuario retorno = null;
            try {
                retorno = json.postRetornaUsuario(AndroidUtils.URL_USUARIO_RECUPERA_SENHA_USUARIO, codigo, email);
            } catch (Exception e) {
                msgException = e.getMessage();
            }
            return retorno;
        }

        @Override
        protected void onPostExecute(Usuario retorno) {
            if (retorno != null) {
                if (retorno.getCodigo() != null) {
                    mUsuario = retorno;
                    load.dismiss();
                    nextProgresso();
                } else {
                    AndroidUtils.alertDialog(RecuperaSenhaActivity.this, getString(R.string.atencao), getString(R.string.codigo_invalido));
                    load.dismiss();
                }
            } else if (msgException != null && !msgException.isEmpty()) {
                load.dismiss();
                AndroidUtils.alertDialog(RecuperaSenhaActivity.this, getString(R.string.atencao), msgException);
            } else {
                AndroidUtils.alertDialog(RecuperaSenhaActivity.this, getString(R.string.atencao), getString(R.string.erro_ao_comunicar_com_servidor));
                load.dismiss();
            }
        }
    }

    private class PostAlteraSenhaJson extends AsyncTask<Void, Void, Usuario> implements AndroidUtils.OnClickAlertDialod{
        private String msgException;
        private Usuario usuario;

        public PostAlteraSenhaJson(Usuario usuario){
            this.usuario = usuario;
        }

        @Override
        protected void onPreExecute() {
            load = ProgressDialog.show(RecuperaSenhaActivity.this, getString(R.string.aguarde), getString(R.string.comunicando_com_servidor));
        }

        @Override
        protected Usuario doInBackground(Void... params) {
            RecuperaSenhaJson json = new RecuperaSenhaJson(RecuperaSenhaActivity.this);
            Usuario retorno = null;
            try {
                retorno = json.postAlteraSenha(AndroidUtils.URL_USUARIO_RECUPERA_SENHA_ALTERA_SENHA, usuario);
            } catch (Exception e) {
                msgException = e.getMessage();
            }
            return retorno;
        }

        @Override
        protected void onPostExecute(Usuario retorno) {
            if (retorno != null) {
                if (retorno.getCodigo() != null) {
                    mUsuario = retorno;
                    load.dismiss();
                    AndroidUtils.alertDialog(RecuperaSenhaActivity.this, getString(R.string.atencao), getString(R.string.senha_alterada_com_sucesso), this);

                } else {
                    AndroidUtils.alertDialog(RecuperaSenhaActivity.this, getString(R.string.atencao), getString(R.string.erro_ao_comunicar_com_servidor));
                    load.dismiss();
                }
            } else if (msgException != null && !msgException.isEmpty()) {
                load.dismiss();
                AndroidUtils.alertDialog(RecuperaSenhaActivity.this, getString(R.string.atencao), msgException);
            } else {
                AndroidUtils.alertDialog(RecuperaSenhaActivity.this, getString(R.string.atencao), getString(R.string.erro_ao_comunicar_com_servidor));
                load.dismiss();
            }
        }

        @Override
        public void onOkClick() {
            finish();
        }
    }

}
