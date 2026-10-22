package ufv.desconecta.Desconecta.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassificacaoDTO {
    private String nomeAluno;
    private int pontuacaoTotalAluno;

    public ClassificacaoDTO(String nomeAluno, int pontuacaoTotalAluno) {
        this.nomeAluno = nomeAluno;
        this.pontuacaoTotalAluno = pontuacaoTotalAluno;
    }


}
