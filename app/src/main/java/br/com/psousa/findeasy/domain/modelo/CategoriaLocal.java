package br.com.psousa.findeasy.domain.modelo;

import java.io.Serializable;

/**
 * Created by Paulo on 23/06/2017.
 */

public class CategoriaLocal implements Serializable{

    private Long codigo;
    private String descricao;

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return getDescricao();
    }
}
