package br.com.psousa.findeasy.service;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.psousa.findeasy.domain.modelo.CategoriaLocal;
import br.com.psousa.findeasy.domain.modelo.Local;
import br.com.psousa.findeasy.util.NetworkUtils;

/**
 * Created by Paulo on 19/09/2017.
 */

public class LocalJson {
    private Context context;

    public LocalJson(Context context) {
        this.context = context;
    }


    public Local postLocal(String end, Local local, String token) throws Exception {
        String json;
        Local retorno;

        json = NetworkUtils.setJSONFromAPI(end, parseLocal(local).toString(), token);

        if (!json.isEmpty()) {
            retorno = parseJson(json);
        } else {
            return new Local();
        }
        return retorno;

    }

    public List<Local> getLocal(String end, String token) throws Exception {
        String json;
        List<Local> retorno;

        json = NetworkUtils.getJSONFromAPI(end, token);

        if (!json.isEmpty()) {
            retorno = parseJsonArray(json);
        } else {
            return new ArrayList<Local>();
        }
        return retorno;

    }

    public Local getLocalCodigo(String end, String token) throws Exception {
        String json;
        Local retorno;

        json = NetworkUtils.getJSONFromAPI(end, token);

        if (!json.isEmpty()) {
            retorno = parseJson(json);
        } else {
            return new Local();
        }
        return retorno;

    }

    private JSONObject parseLocal(Local local) throws Exception {
        try {
            JSONObject obj = new JSONObject();
            if (local.getCodigo() != null) {
                obj.put("codigo", local.getCodigo());
            }
            obj.put("ativo", local.getAtivo());
            obj.put("descricao", local.getDescricao());
            obj.put("chcategoria", local.getCategoria().getCodigo());
            obj.put("endereco", local.getEndereco());
            obj.put("latitude", local.getLatitude());
            obj.put("longitude", local.getLongitude());
            obj.put("telefone",local.getTelefone());
            return obj;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Local> parseJsonArray(String json) throws Exception {
        try {
            List<Local> locais = null;

            //Atribui os objetos que estão nas camadas mais altas
            try {
                //Atribui os objetos que estão nas camadas mais altas
                if (json.contains("erro")) {
                    throw new Exception("Erro ao carregar locais");
                }
            } catch (Exception e) {

            }
            JSONArray obje = new JSONArray(json);

            locais = new ArrayList<Local>();

            for (int i = 0; i < obje.length(); i++) {

                JSONObject obj = obje.getJSONObject(i);

                Local local = new Local();
                local.setAtivo(obj.getBoolean("ativo"));
                local.setDescricao(obj.getString("descricao"));
                local.setTelefone(obj.getString("telefone"));
                local.setNivelBusca(obj.getInt("nivel_busca"));
                local.setLatitude(obj.getString("latitude"));
                local.setCodigo(obj.getLong("codigo"));
                local.setEndereco(obj.getString("endereco"));
                local.setLongitude(obj.getString("longitude"));

                locais.add(local);
            }

            return locais;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Local parseJson(String json) throws Exception {
        try {
            Local local = null;

            //Atribui os objetos que estão nas camadas mais altas
            JSONObject obje = new JSONObject(json);
            if (obje.has("erro")) {
                throw new Exception("Erro ao cadastrar Local.");
            }
            if (obje.has("mensagem")) {
                throw new Exception(obje.getString("mensagem"));
            }

            JSONObject obj = new JSONObject(json);

            local = new Local();
            local.setAtivo(obj.getBoolean("ativo"));
            local.setDescricao(obj.getString("descricao"));
            local.setTelefone(obj.getString("telefone"));
            local.setNivelBusca(obj.getInt("nivel_busca"));
            local.setLatitude(obj.getString("latitude"));
            local.setCodigo(obj.getLong("codigo"));
            local.setEndereco(obj.getString("endereco"));
            local.setLongitude(obj.getString("longitude"));
            JSONObject objCategoria = obj.getJSONObject("categoria");
            CategoriaLocal categoriaLocal = new CategoriaLocal();
            categoriaLocal.setDescricao(objCategoria.getString("descricao"));
            categoriaLocal.setCodigo(objCategoria.getLong("codigo"));
            local.setCategoria(categoriaLocal);

            return local;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
