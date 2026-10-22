package app.model;

import java.util.List; 

public class Pergunta {
    private int id;
    private String texto;
    private String imagemUrl;
    private String audioUrl;
    private List<String> alternativas;

    //Getters
    public int getId() {
        return id;
    }

    public String getTexto(){
        return texto;
    }

    public String getImagemUrl(){
        return imagemUrl;
    }

    public String getAudioUrl(){
        return audioUrl;
    }

    public List<String> getAlternativas(){
        return alternativas;
    }


    //Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTexto(String texto){
        this.texto = texto;
    }

    public void setImagemUrl(String image){
        this.imagemUrl = image;
    }

    public void setAudioUrl(String audio){
        this.audioUrl = audio;
    }

    public void setAlternativas(List<String> alternativas){
        this.alternativas = alternativas;
    }

}
