package br.com.psousa.findeasy.service;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.psousa.findeasy.domain.modelo.Usuario;
import br.com.psousa.findeasy.util.NetworkUtils;

/**
 * Created by Paulo on 20/06/2017.
 */

public class RecuperaSenhaJson {

    private Context context;

    public RecuperaSenhaJson(Context context) {
        this.context = context;
    }

    public String postRecuperaSenha(String end, String email) throws Exception {
        String json;
        String retorno;
        json = NetworkUtils.setJSONFromAPI(end, parseEmail(email).toString());
        if (!json.isEmpty()) {
            retorno = parseRecuperaSenhaJson(json);
        } else {
            return "";
        }
        return retorno;
    }

    public Usuario postRetornaUsuario(String end, String codigo, String email) throws Exception {
        String json;
        Usuario retorno;

        json = NetworkUtils.setJSONFromAPI(end, parseEmailCodigo(email, codigo).toString());

        if (!json.isEmpty()) {
            retorno = parseUsuarioJson(json);
        } else {
            return new Usuario();
        }
        return retorno;

    }

    public Usuario postAlteraSenha(String end, Usuario usuario) throws Exception {
        String json;
        Usuario retorno;

        json = NetworkUtils.setJSONFromAPI(end, parseUsuario(usuario).toString());

        if (!json.isEmpty()) {
            retorno = parseUsuarioJson(json);
        } else {
            return new Usuario();
        }
        return retorno;
    }

    private JSONObject parseEmail(String email) throws Exception {
        try {
            JSONObject obj = new JSONObject();
            obj.put("email", email);
            return obj;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONObject parseEmailCodigo(String email, String codigo) throws Exception {
        try {
            JSONObject obj = new JSONObject();
            obj.put("email", email);
            obj.put("codigo", codigo);
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

    private String parseRecuperaSenhaJson(String json) throws Exception {
        //Atribui os objetos que estão nas camadas mais altas
        JSONObject obje = new JSONObject(json);
        if (obje.has("erro")) {
            throw new Exception("Não foi possivel comunicar com servidor.");
        }
        if (obje.has("mensagem")) {
            return obje.getString("mensagem");
        }
        return "";
    }

    private Usuario parseUsuarioJson(String json) throws Exception {
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
