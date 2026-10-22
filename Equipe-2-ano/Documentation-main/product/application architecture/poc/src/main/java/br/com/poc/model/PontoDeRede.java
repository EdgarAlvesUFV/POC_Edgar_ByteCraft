package br.com.poc.model;

public class PontoDeRede {
    private Long id;
    private String tipo;
    private double posX;
    private double posY;

    public PontoDeRede(Long id, String tipo, double posX, double posY) {
        this.id = id;
        this.tipo = tipo;
        this.posX = posX;
        this.posY = posY;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public double getPosX() { return posX; }
    public void setPosX(double posX) { this.posX = posX; }
    public double getPosY() { return posY; }
    public void setPosY(double posY) { this.posY = posY; }
}