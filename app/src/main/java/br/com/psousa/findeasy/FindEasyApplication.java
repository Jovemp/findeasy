package br.com.psousa.findeasy;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.psousa.findeasy.domain.modelo.Usuario;

/**
 * Created by Paulo on 08/06/2017.
 */

public class FindEasyApplication extends Application {

    public static final String PREFS_APP = "findeasy";
    public static final String PREFS_USER_LOGADO = "user_logado";
    public static final String PREFS_TOKEN = "token";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private static FindEasyApplication instance = null;

    public static FindEasyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public void setUsuarioLogado(Usuario usuario){
        SharedPreferences mPrefs = getSharedPreferences(PREFS_APP,MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        String json = "";
        if (usuario != null) {
            try {
                json = parseUsuario(usuario).toString();
            } catch (Exception e) {

            }
        } else {
            json = "";
        }
        prefsEditor.putString(PREFS_USER_LOGADO, json);
        prefsEditor.commit();
    }

    public Usuario getUsuarioLogado(){
        SharedPreferences mPrefs = getSharedPreferences(PREFS_APP,MODE_PRIVATE);
        String json = "";
        try {
            json = mPrefs.getString(PREFS_USER_LOGADO, "");
        } catch (Exception e) {

        }
        if (json == ""){
            return null;
        } else {
            try {
                return parseJson(json);
            } catch (Exception e) {
                return null;
            }
        }
    }

    public void setToken(String token){
        SharedPreferences mPrefs = getSharedPreferences(PREFS_APP,MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString(PREFS_TOKEN, token);
        prefsEditor.commit();
    }

    public String getToken(){
        SharedPreferences mPrefs = getSharedPreferences(PREFS_APP,MODE_PRIVATE);
        return mPrefs.getString(PREFS_TOKEN, "");
    }

    private JSONObject parseUsuario(Usuario usuario) throws Exception {
        try {
            JSONObject obj = new JSONObject();
            if (usuario.getCodigo() != null) {
                obj.put("codigo", usuario.getCodigo());
            }
            obj.put("ativo", usuario.getAtivo());
            obj.put("celular", usuario.getCelular());
            obj.put("email", usuario.getEmail());
            obj.put("senha", usuario.getSenha());
            obj.put("nome", usuario.getNome());
            return obj;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Usuario parseJson(String json) throws Exception {
        try {
            Usuario usuario = null;

            //Atribui os objetos que estão nas camadas mais altas
            JSONObject obje = new JSONObject(json);
            if (obje.has("erro")) {
                throw new Exception("Erro ao cadastrar Usuário.");
            }
            if (obje.has("mensagem")) {
                throw new Exception(obje.getString("mensagem"));
            }

            JSONObject obj = new JSONObject(json);

            usuario = new Usuario();
            usuario.setAtivo(obj.getBoolean("ativo"));
            usuario.setSenha(obj.getString("senha"));
            usuario.setNome(obj.getString("nome"));
            usuario.setEmail(obj.getString("email"));
            usuario.setCelular(obj.getString("celular"));
            usuario.setCodigo(obj.getLong("codigo"));

            return usuario;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
