package ufv.desconecta.Desconecta.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "TB_ProgressoAluno")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressoAluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long PK_ProgressoAluno;

    @OneToMany(mappedBy = "progressoAluno", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Ilha> ilhas;

    @Builder.Default
    @Column(name = "pontuacao_total_aluno")
    private int pontuacaoTotalAluno = 0;


    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_Aluno", referencedColumnName = "PK_Aluno")
    private Aluno aluno;

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
        if (aluno != null && aluno.getProgresso() != this) {
            aluno.setProgresso(this);
        }
    }

    public void setIlhas(List<Ilha> ilhas) {
        this.ilhas = ilhas;
        if (ilhas != null) {
            for (Ilha ilha : ilhas) {
                if (ilha.getProgressoAluno() != this) {
                    ilha.setProgressoAluno(this);
                }
            }
        }
    }
}