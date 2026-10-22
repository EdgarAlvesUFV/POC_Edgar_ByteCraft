package ufv.desconecta.Desconecta.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_Aluno")
@NoArgsConstructor
@AllArgsConstructor
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long PK_Aluno;

    @Column(nullable = false)
    private String apelido;

    @Column(nullable = false)
    private String senha;

    @OneToOne(mappedBy = "aluno", cascade = CascadeType.ALL)
    private ProgressoAluno progresso;

    public Aluno(String apelido, String senha) {
        this.apelido = apelido;
        this.senha = senha;

    }


    public long getPK_Aluno() {
        return PK_Aluno;
    }

    public String getApelido() {
        return apelido;
    }

    public String getSenha() {
        return senha;
    }

    public void setProgresso(ProgressoAluno progresso) {
        this.progresso = progresso;

    }

    public ProgressoAluno getProgresso() {
        return progresso;
    }
}
