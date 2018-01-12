package br.com.psousa.findeasy.service;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.psousa.findeasy.domain.modelo.CategoriaLocal;
import br.com.psousa.findeasy.util.NetworkUtils;

/**
 * Created by Paulo on 18/09/2017.
 */

public class CategoriaLocalJson {
    private Context context;

    public CategoriaLocalJson(Context context) {
        this.context = context;
    }

    public List<CategoriaLocal> getCategorias(String end, String token) throws Exception {
        String json = "";
        List<CategoriaLocal> retorno;
        json = NetworkUtils.getJSONFromAPI(end, token);
        if (!json.isEmpty()) {
            retorno = parseJson(json);
        } else {
            return null;
        }


        return retorno;
    }

    private List<CategoriaLocal> parseJson(String json) throws Exception {
        try {
            List<CategoriaLocal> categorias = null;

            //Atribui os objetos que estão nas camadas mais altas
            try {
                //Atribui os objetos que estão nas camadas mais altas
                if (json.contains("erro")) {
                    throw new Exception("Erro ao carregar categorias");
                }
            } catch (Exception e) {

            }

            JSONArray obje = new JSONArray(json);
            categorias = new ArrayList<CategoriaLocal>();
            for (int i = 0; i < obje.length(); i++ ) {
                JSONObject o = obje.getJSONObject(i);
                CategoriaLocal categoria = new CategoriaLocal();
                categoria.setCodigo(o.getLong("codigo"));
                categoria.setDescricao(o.getString("descricao"));
                categorias.add(categoria);
            }

            return categorias;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
