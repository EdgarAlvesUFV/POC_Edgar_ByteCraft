package app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class Pista {

    //identificador unico da pista
    private Long id;

    //conteudo da pista
    private String conteudo;

    //formato da pista(texto,audio,imagem)
    private String tipo;

    //indicador de qual misterio a pista pertence
    @JsonIgnore
    private Misterio misterioID;


    //getters e setters
    public Long getId(){ return id; }
    public String getConteudo(){ return conteudo; }
    public String getTipo(){ return tipo; }
    public Misterio getMisterioID(){ return misterioID; }

    public void setId(Long id){ this.id = id; }
    public void setConteudo(String conteudo){ this.conteudo = conteudo; }
    public void setTipo(String tipo){ this.tipo = tipo; }
    public void setMisterioID(Misterio misterio){ this.misterioID = misterio; }

}
