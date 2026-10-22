package ufv.desconecta.Desconecta.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TB_CacaPalavras")
public class CacaPalavras {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int PK_CacaPalavras;

    @ElementCollection
    @CollectionTable(
            name = "TB_RespostasCacas",
            joinColumns = @JoinColumn(name = "FK_CacaPalavras")
    )
    @Column(name = "resposta_certa", nullable = false)
    private List<String> respostaCerta;
}
