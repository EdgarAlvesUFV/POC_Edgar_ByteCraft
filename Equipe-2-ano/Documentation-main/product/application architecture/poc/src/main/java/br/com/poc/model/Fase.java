package br.com.poc.model;

public class Fase {
    private Long id;
    private String nome;
    private boolean bloqueada;

    public Fase(Long id, String nome, boolean bloqueada) {
        this.id = id;
        this.nome = nome;
        this.bloqueada = bloqueada;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public boolean isBloqueada() { return bloqueada; }
    public void setBloqueada(boolean bloqueada) { this.bloqueada = bloqueada; }
}
