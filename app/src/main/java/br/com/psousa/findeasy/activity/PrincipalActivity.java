package br.com.psousa.findeasy.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.psousa.findeasy.FindEasyApplication;
import br.com.psousa.findeasy.R;
import br.com.psousa.findeasy.domain.modelo.Local;
import br.com.psousa.findeasy.domain.modelo.Usuario;
import br.com.psousa.findeasy.service.LocalJson;
import br.com.psousa.findeasy.service.UsuarioJson;
import br.com.psousa.findeasy.util.AndroidUtils;
import br.com.psousa.findeasy.util.MyLocation;
import br.com.psousa.findeasy.util.PermissaoAndroid;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.ReplaySubject;

public class PrincipalActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private ProgressDialog load;
    private MyLocation mMyLocation;
    private MyLocation.LocationResult mLocationResult;
    private MarkerOptions mMarkerOptions;
    private Marker mMyMarker;
    private List<Marker> mMarkerLocais;
    private LatLng mLocalizacao;

    private TextView mTxtUsuario;
    private TextView mTxtEmail;
    private FloatingActionButton mFabAdicionar;
    private MenuItem mSearchItem;

    private SupportMapFragment mMapFragment;
    private FragmentManager mFragmentManager;

    private static final int COD_CADASTRO_LOCAL = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0);

        mTxtUsuario = (TextView) headerLayout.findViewById(R.id.lb_menu_usuario);
        mTxtEmail = (TextView) headerLayout.findViewById(R.id.lb_menu_email);

        if (FindEasyApplication.getInstance().getUsuarioLogado() == null) {
            new PostCarregarUsuarioJson(FindEasyApplication.getInstance().getToken()).execute();
        } else {
            updateUsuarioTela();
        }

        mMapFragment = new SupportMapFragment();

        mMapFragment.getMapAsync(this);

        mFragmentManager = getSupportFragmentManager();

        mFragmentManager.beginTransaction().replace(R.id.container_activity, mMapFragment).commit();

        PermissaoAndroid.validate(this, PermissaoAndroid.REQUEST_PERMISSIONS_CODE, this, PermissaoAndroid.permissoes);

        mLocationResult = new MyLocation.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                // Add a marker in Sydney and move the camera
                mLocalizacao = new LatLng(location.getLatitude(), location.getLongitude());

                if (mMarkerOptions == null) {
                    mMarkerOptions = new MarkerOptions()
                            .position(mLocalizacao)
                            .title("Esse sou eu!")
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_localizacao));
                } else {
                    mMyMarker.remove();
                    mMarkerOptions.position(mLocalizacao);
                }

                if (mMarkerOptions != null) {
                    if (mMap != null) {
                        mMyMarker = mMap.addMarker(mMarkerOptions);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocalizacao, 17));
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    }
                }
            }
        };
        mMyLocation = new MyLocation();
        mMyLocation.getLocation(this, mLocationResult);

        mFabAdicionar = (FloatingActionButton) findViewById(R.id.fab_principal_adicionar);

        mFabAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adicionarLocal();
            }
        });

        mMarkerLocais = new ArrayList<Marker>();

    }

    private void adicionarLocal(){
        if (mLocalizacao != null) {
            Intent it = new Intent(PrincipalActivity.this, CadastroLocalActivity.class);
            it.putExtra(CadastroLocalActivity.LATITUDE, mLocalizacao.latitude);
            it.putExtra(CadastroLocalActivity.LONGITUDE, mLocalizacao.longitude);
            startActivityForResult(it, COD_CADASTRO_LOCAL);
        } else {
            AndroidUtils.alertDialog(this, getString(R.string.atencao), getString(R.string.aguarde_o_servidor_de_gps));
        }
    }

    private void updateUsuarioTela() {
        if (mTxtUsuario != null) {
            if (mTxtEmail != null) {
                Usuario u = FindEasyApplication.getInstance().getUsuarioLogado();
                if (u != null) {
                    mTxtUsuario.setText(u.getNome());
                    mTxtEmail.setText(u.getEmail());
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        mSearchItem = menu.findItem(R.id.action_search);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (item.getItemId() == R.id.action_search) {
            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
            //searchView.setQueryHint(getString(R.string.hint_pesquisa_produto));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (mLocalizacao != null) {
                        new GetCarregarLocalJson(PrincipalActivity.this,
                                query,
                                String.valueOf(mLocalizacao.latitude),
                                String.valueOf(mLocalizacao.longitude),
                                FindEasyApplication.getInstance().getToken()).execute();
                    }
                    searchView.clearFocus();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_principal) {
            mFragmentManager.beginTransaction().replace(R.id.container_activity, mMapFragment).commit();
        } else if (id == R.id.nav_meus_locais) {

        } else if (id == R.id.nav_perfil) {

        } else if (id == R.id.nav_sair) {
            FindEasyApplication.getInstance().setUsuarioLogado(null);
            FindEasyApplication.getInstance().setToken("");
            Intent i = new Intent(PrincipalActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                abrirLocal( (Long) arg0.getTag());
                return true;
            }

        });
    }

    private class PostCarregarUsuarioJson extends AsyncTask<Void, Void, Usuario> {
        private String msgException;
        private String token;

        public PostCarregarUsuarioJson(String token) {
            this.token = token;
        }

        @Override
        protected void onPreExecute() {
            load = ProgressDialog.show(PrincipalActivity.this, getString(R.string.aguarde), getString(R.string.comunicando_com_servidor));
        }

        @Override
        protected Usuario doInBackground(Void... params) {
            UsuarioJson json = new UsuarioJson(PrincipalActivity.this);
            Usuario retorno = null;
            try {
                retorno = json.getUsuario(AndroidUtils.URL_USUARIOS, token);
            } catch (Exception e) {
                msgException = e.getMessage();
            }
            return retorno;
        }

        @Override
        protected void onPostExecute(Usuario retorno) {
            if (retorno != null) {
                if (retorno.getCodigo() != null) {
                    FindEasyApplication.getInstance().setUsuarioLogado(retorno);
                    updateUsuarioTela();
                    load.dismiss();
                } else {
                    AndroidUtils.alertDialog(PrincipalActivity.this, getString(R.string.atencao), getString(R.string.erro_ao_comunicar_com_servidor));
                    load.dismiss();
                }
            } else if (msgException != null && !msgException.isEmpty()) {
                load.dismiss();
                AndroidUtils.alertDialog(PrincipalActivity.this, getString(R.string.atencao), msgException);
            } else {
                AndroidUtils.alertDialog(PrincipalActivity.this, getString(R.string.atencao), getString(R.string.erro_ao_comunicar_com_servidor));
                load.dismiss();
            }
        }
    }

    private class GetCarregarLocalJson extends AsyncTask<Void, Void, List<Local>> {
        private String msgException;
        private String token;
        private String where;
        private String latitude;
        private String longitude;
        private Context context;

        public GetCarregarLocalJson(Context context, String where, String latitude, String longitude, String token) {
            this.token = token;
            this.context = context;
            this.where = where;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected void onPreExecute() {
            load = ProgressDialog.show(context, getString(R.string.aguarde), getString(R.string.comunicando_com_servidor));
        }

        @Override
        protected List<Local> doInBackground(Void... params) {
            LocalJson json = new LocalJson(context);
            List<Local> retorno = null;
            try {
                retorno = json.getLocal(AndroidUtils.URL_LOCAL+"/"+
                                        java.net.URLEncoder.encode(where, "UTF-8")+"/" +
                                        latitude +"/" + longitude + "/100/0/2000", token);
            } catch (Exception e) {
                msgException = e.getMessage();
            }
            return retorno;
        }

        @Override
        protected void onPostExecute(List<Local> retorno) {
            if (retorno != null) {
                updateMaps(retorno);
                load.dismiss();
            } else if (msgException != null && !msgException.isEmpty()) {
                load.dismiss();
                AndroidUtils.alertDialog(context, getString(R.string.atencao), msgException);
            } else {
                AndroidUtils.alertDialog(context, getString(R.string.atencao), getString(R.string.erro_ao_comunicar_com_servidor));
                load.dismiss();
            }
        }

        private void updateMaps(List<Local> locais){
            for (Marker m : mMarkerLocais) {
                m.remove();
            }

            for (Local local : locais) {
                LatLng lat = new LatLng(Double.parseDouble(local.getLatitude()),
                                        Double.parseDouble(local.getLongitude()));
                Marker m = mMap.addMarker(new MarkerOptions().position(lat)
                        .title(local.getDescricao()));
                m.showInfoWindow();
                m.setTag(local.getCodigo());
                mMarkerLocais.add(m);

            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Quando Retorna
        if (requestCode == COD_CADASTRO_LOCAL) {
            // Verifica se foi cadastrado
            if (data != null) {
                Long codigo = data.getLongExtra(CadastroLocalActivity.RES_RESULT, 0);
                if (codigo > 0){
                    abrirLocal(codigo);
                }
            }
        }
    }

    public void abrirLocal(Long codigo){
        Intent it = new Intent(PrincipalActivity.this, LocalActivity.class);
        it.putExtra(LocalActivity.COD_LOCAL, codigo);
        startActivity(it);
    }
}
