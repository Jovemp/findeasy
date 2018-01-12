package br.com.psousa.findeasy.domain.modelo;

import java.io.Serializable;

/**
 * Created by Paulo on 23/06/2017.
 */

public class Local implements Serializable{

    private Long codigo;
    private String descricao;
    private String telefone;
    private String endereco;
    private int nivelBusca;
    private Boolean ativo;
    private String latitude;
    private String longitude;
    private Usuario usuario;
    private CategoriaLocal categoria;

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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public int getNivelBusca() {
        return nivelBusca;
    }

    public void setNivelBusca(int nivelBusca) {
        this.nivelBusca = nivelBusca;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public CategoriaLocal getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaLocal categoria) {
        this.categoria = categoria;
    }
}
