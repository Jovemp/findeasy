package br.com.psousa.findeasy.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import br.com.psousa.findeasy.R;
import br.com.psousa.findeasy.fragment.ViewCadastroLocal;
import br.com.psousa.findeasy.fragment.ViewPesquisaMaps;

public class CadastroLocalActivity extends AppCompatActivity implements ViewPesquisaMaps.OnViewPesquisaMapsListener, ViewCadastroLocal.OnViewCadastroLocalListener {

    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";

    public static final String ARG_CODIGO = "cod_local";
    public static final String RES_RESULT = "result";

    private FragmentManager mFragmentManager;

    double mLatitude;
    double mLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_local);

        setTitle(getString(R.string.sel_localizacao));


        Intent it = getIntent();

        mLatitude = it.getDoubleExtra(LATITUDE, 0);
        mLongitude = it.getDoubleExtra(LONGITUDE, 0);

        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction().replace(R.id.cadastro_local_fragment, ViewPesquisaMaps.newInstance(mLongitude, mLatitude)).commit();


        // Adiciona botão de Voltar na barra de Titulo
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.cadastro_local_fragment);
                if (fragment instanceof  ViewCadastroLocal){
                    setTitle(getString(R.string.sel_localizacao));
                    mFragmentManager = getSupportFragmentManager();
                    mFragmentManager.beginTransaction().replace(R.id.cadastro_local_fragment, ViewPesquisaMaps.newInstance(mLongitude, mLatitude)).commit();
                } else {
                    finish();
                }
                // Fecha a tela ao clicar no botão de voltar
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFragmentManager = getSupportFragmentManager();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.cadastro_local_fragment);
        if (fragment instanceof  ViewCadastroLocal){
            setTitle(getString(R.string.sel_localizacao));
            mFragmentManager = getSupportFragmentManager();
            mFragmentManager.beginTransaction().replace(R.id.cadastro_local_fragment, ViewPesquisaMaps.newInstance(mLongitude, mLatitude)).commit();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onProximo(double longitude, double latitude) {
        mLongitude = longitude;
        mLatitude = latitude;
        setTitle(getString(R.string.cadastro_local));
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction().replace(R.id.cadastro_local_fragment, ViewCadastroLocal.newInstance(longitude, latitude)).commit();
    }

    @Override
    public void onClose(Long codigo){
        Intent returnIntent = new Intent();
        returnIntent.putExtra(RES_RESULT, codigo);
        setResult(RESULT_OK, returnIntent);
        // fechando tela
        finish();
    }
}
