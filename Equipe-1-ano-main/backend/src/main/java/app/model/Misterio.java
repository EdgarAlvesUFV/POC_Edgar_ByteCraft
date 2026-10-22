package app.model;

import java.util.List;

public class Misterio {
    private Long id;
    private String titulo;
    private List<Pista> pistas;
    private List<Resposta> respostas;

    //Getters
    public Long getId(){
        return id;
    }
    public String getTitulo(){
        return titulo;
    }

    public List<Pista> getPistas(){
        return pistas;
    }

    public List<Resposta> getRespostas(){
        return respostas;
    }

    //Setters
    public void setId(Long id){
        this.id = id;
    }

    public void setTitulo(String titulo){
        this.titulo = titulo;
    }

    public void setPistas(List<Pista> pistas){
        this.pistas = pistas;
    }
    

    public void setRespostas(List<Resposta> respostas){
        this.respostas = respostas;
    }

}
