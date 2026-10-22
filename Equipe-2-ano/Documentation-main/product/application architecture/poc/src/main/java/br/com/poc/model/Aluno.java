package br.com.poc.model;

public class Aluno {
    private Long id;
    private String apelido;
    private String senha;

    public Aluno() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getApelido() { return apelido; }
    public void setApelido(String apelido) { this.apelido = apelido; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}
