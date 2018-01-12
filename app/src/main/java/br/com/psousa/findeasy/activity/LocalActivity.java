package br.com.psousa.findeasy.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import br.com.psousa.findeasy.R;
import br.com.psousa.findeasy.fragment.ViewLocal;

public class LocalActivity extends AppCompatActivity {

    public static final String COD_LOCAL = "cod_local";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);

        setTitle(getString(R.string.local));

        Intent it = getIntent();
        Long codigo = it.getLongExtra(COD_LOCAL, 0);


        ViewLocal cf = (ViewLocal) getSupportFragmentManager().findFragmentById(R.id.local_fragment);
        cf.setCodigo(codigo);

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
    }
}
