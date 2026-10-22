package app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Resposta {
    private Long id;
    private String descricao;
    private int ordem;
    private String tipo;
    @JsonIgnore
    private Misterio misterioID;
    private Boolean status;
    //Getters
    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getOrdem() {
        return ordem;
    }

    public String getTipo() {
        return tipo;
    }

    public Misterio getMisterioID() {
        return misterioID;
    }

    public Boolean getStatus() {
        return status;
    }

    //Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setMisterioID(Misterio misterioID) {
        this.misterioID = misterioID;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
