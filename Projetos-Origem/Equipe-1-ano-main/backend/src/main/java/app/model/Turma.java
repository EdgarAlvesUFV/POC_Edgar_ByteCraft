package app.model;

import jakarta.persistence.*;

import java.util.Locale;

@Entity
@Table(name = "turmas")
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private int ano;

    @Column(nullable = false)
    private String turno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professor_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_turmas_professor"))
    private Professor professor;


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {this.nome = nome.toUpperCase();}
    public int getAno() {
        return ano;
    }
    public void setAno(int ano) {
        this.ano = ano;
    }
    public String getTurno() {
        return turno;
    }
    public void setTurno(String turno){
        this.turno = turno.toUpperCase();
    }

    public Professor getProfessor() {
        return professor;
    }
    public void setProfessor(Professor professor){
        this.professor = professor;
    }
}
