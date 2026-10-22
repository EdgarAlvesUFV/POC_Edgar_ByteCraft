package ufv.desconecta.Desconecta.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "TB_Conecta")
public class Conecta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int PK_Conecta;

    @Column(nullable = false)
    private String respostaCerta1;

    @Column(nullable = false)
    private String respostaCerta2;

    @Column(nullable = false)
    private String respostaCerta3;
    @Column(nullable = false)
    private String respostaCerta4;




}