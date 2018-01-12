package br.com.psousa.findeasy.service;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import br.com.psousa.findeasy.domain.modelo.Usuario;
import br.com.psousa.findeasy.util.NetworkUtils;

/**
 * Created by Paulo on 03/04/2017.
 */

public class UsuarioJson {
    private Context context;

    public UsuarioJson(Context context) {
        this.context = context;
    }

    public String getLogin(String end, String email, String senha) throws Exception {
        String json = "";
        String retorno;
        json = NetworkUtils.setJSONFromAPI(end, parseLogin(email, senha).toString());
        if (!json.isEmpty()) {
            retorno = parseTokenJson(json);
        } else {
            return "";
        }


        return retorno;
    }

    public Usuario getUsuario(String end, String token) throws Exception {
        String json;
        Usuario retorno;
        json = NetworkUtils.getJSONFromAPI(end, token);
        if (!json.isEmpty()) {
            retorno = parseJson(json);
        } else {
            return new Usuario();
        }


        return retorno;
    }

    public Usuario postUsuario(String end, Usuario usuario) throws Exception {
        String json;
        Usuario retorno;

        json = NetworkUtils.setJSONFromAPI(end, parseUsuario(usuario).toString());

        if (!json.isEmpty()) {
            retorno = parseJson(json);
        } else {
            return new Usuario();
        }
        return retorno;

    }

    private JSONObject parseLogin(String email, String senha){
        try {
            JSONObject obj = new JSONObject();
            obj.put("email", email);
            obj.put("senha", senha);
            return obj;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
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

    private String parseTokenJson(String json) throws Exception {
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

            if (obje.has("token")) {
                return obje.getString("token");
            } else {
                return "";
            }

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
