package ufv.desconecta.Desconecta.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_Desafio")
@Getter
@Setter
public class Desafio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private EnumTiposDesafios tipoDesafio;
    private int pontuacaoDesafio;
    private boolean isConcluido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_Ilha") // Esta será a coluna da chave estrangeira.
    private Ilha ilha;

    public Desafio() {
    }

    public Desafio(EnumTiposDesafios tipoDesafio, Ilha ilha) {
        this.tipoDesafio = tipoDesafio;
        this.ilha = ilha;
        this.isConcluido = false;
        this.pontuacaoDesafio = 0;
    }
}