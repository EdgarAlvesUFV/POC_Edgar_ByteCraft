package ufv.desconecta.Desconecta.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_Ilha")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ilha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int PK_Ilha; // chave primária

    @Enumerated(EnumType.STRING)
    private EnumNomeIlha nomeIlha;       // nome da ilha
    private boolean estado;    // true se desbloqueada
    private boolean foiJogada; // true se o aluno já jogou

    @OneToMany(mappedBy = "ilha", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Desafio> desafios = new ArrayList<>();
    public EnumNomeIlha buscaIlha(int idIlha) {
        if(idIlha < 0 ||idIlha > 4) {
            throw new IllegalArgumentException("Índice inválido para EnumNomeIlha: " + idIlha);
        }
        return EnumNomeIlha.values()[idIlha];
    }

    // relacionamento ManyToOne com ProgressoAluno
    @ManyToOne
    @JoinColumn(name = "PK_ProgressoAluno")
    @JsonBackReference
    private ProgressoAluno progressoAluno;
}