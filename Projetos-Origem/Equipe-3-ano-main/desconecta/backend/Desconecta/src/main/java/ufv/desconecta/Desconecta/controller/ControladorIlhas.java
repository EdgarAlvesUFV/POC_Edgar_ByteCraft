package ufv.desconecta.Desconecta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ufv.desconecta.Desconecta.model.EnumNomeIlha;
import ufv.desconecta.Desconecta.model.EnumTiposDesafios;
import ufv.desconecta.Desconecta.model.Desafio;
import ufv.desconecta.Desconecta.model.Ilha;
import ufv.desconecta.Desconecta.model.ProgressoAluno;
import ufv.desconecta.Desconecta.repository.AcessoBDIlha;
import ufv.desconecta.Desconecta.repository.AcessoBDProgressoAluno;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ilhas")
@CrossOrigin(origins = "*")
public class ControladorIlhas {

    private final AcessoBDIlha acessoBDIlha;
    private final AcessoBDProgressoAluno acessoBDProgressoAluno;


    @Autowired
    public ControladorIlhas(AcessoBDIlha acessoBDIlha, AcessoBDProgressoAluno acessoBDProgressoAluno) {
        this.acessoBDIlha = acessoBDIlha;
        this.acessoBDProgressoAluno = acessoBDProgressoAluno;
    }


    @PostMapping("/desbloquear")
    public boolean desbloquearIlha(@RequestBody Ilha ilha) {
        ilha.setEstado(true);
        return acessoBDIlha.alterarEstadoIlha(ilha);
    }


    @PostMapping("/bloquear")
    public boolean bloquearIlha(@RequestBody Ilha ilha) {
        // Define o estado como false antes de passar para a camada de acesso ao banco.
        ilha.setEstado(false);
        return acessoBDIlha.alterarEstadoIlha(ilha);
    }


    @GetMapping("/recuperar/{idProgressoAluno}")
    public List<Ilha> recuperarIlhas(@PathVariable int idProgressoAluno) {
        return acessoBDIlha.recuperarIlhasPorProgressoId(idProgressoAluno);
    }

    @GetMapping("/posicoes-ilhas/{idProgressoAluno}")
    public List<Integer> recuperarPosicoesIlhas(@PathVariable int idProgressoAluno) {
        try {
            // 1. Busca a lista de ilhas (ela virá desordenada do banco).
            List<Ilha> ilhasDoProgresso = acessoBDIlha.recuperarIlhasPorProgressoId(idProgressoAluno);

            // 2. Ordena a lista de ilhas e extrai os ordinais.
            return ilhasDoProgresso.stream() // Converte a lista para um fluxo (stream) para processamento.
                    .filter(ilha -> ilha.getNomeIlha() != null) // Garante que não temos ilhas com nome nulo.
                    .sorted(Comparator.comparing(ilha -> ilha.getNomeIlha().ordinal())) // Ordena a lista com base no ordinal do enum.
                    .map(ilha -> ilha.getNomeIlha().ordinal()) // Transforma cada objeto Ilha no seu respectivo ordinal (Integer).
                    .collect(Collectors.toList()); // Coleta os resultados em uma nova lista de inteiros.

        } catch (Exception e) {
            // Em caso de erro, loga a mensagem e retorna uma lista vazia.
            System.err.println("Erro ao recuperar posições das ilhas: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Avança a ilha do aluno para a próxima posição no enum
     * @param idProgressoAluno ID do progresso do aluno
     * @return Nova posição da ilha ou -1 se erro
     */
    @PutMapping("/avancar-ilha/{idProgressoAluno}")
    public int avancarIlha(@PathVariable int idProgressoAluno) {
        try {
            // 1. Busca o progresso do aluno.
            ProgressoAluno progresso = acessoBDProgressoAluno.getProgressoAluno(idProgressoAluno);
            if (progresso == null) {
                System.err.println("Progresso com ID " + idProgressoAluno + " não encontrado.");
                return -1;
            }

            // 2. Busca a lista de ilhas que o aluno já possui.
            List<Ilha> ilhasDoAluno = acessoBDIlha.recuperarIlhasPorProgressoId(idProgressoAluno);

            // 3. Descobre qual é a ilha mais avançada (com maior ordinal).
            Optional<Ilha> ilhaMaisRecenteOpt = ilhasDoAluno.stream()
                    .max(Comparator.comparing(ilha -> ilha.getNomeIlha().ordinal()));

            if (ilhaMaisRecenteOpt.isEmpty()) {
                System.err.println("Aluno não possui nenhuma ilha para poder avançar.");
                return -1;
            }

            // 4. Pega o objeto da ilha anterior (a mais avançada até agora).
            Ilha ilhaAnterior = ilhaMaisRecenteOpt.get();

            // ***** AJUSTE PRINCIPAL: Marca a ilha anterior como jogada. *****
            ilhaAnterior.setFoiJogada(true);

            // 5. Pega o nome e a posição da ilha anterior para calcular a próxima.
            EnumNomeIlha nomeIlhaAtual = ilhaAnterior.getNomeIlha();
            int posicaoAtual = nomeIlhaAtual.ordinal();

            // 6. Verifica se o aluno já está na última ilha.
            EnumNomeIlha[] todasAsIlhas = EnumNomeIlha.values();
            if (posicaoAtual >= todasAsIlhas.length - 1) {
                System.out.println("Aluno já está na última ilha, não pode avançar.");
                // Mesmo estando na última ilha, ainda salvamos a alteração de 'foiJogada'.
                acessoBDProgressoAluno.salvarProgressoAluno(progresso);
                return -1;
            }

            // 7. Determina qual é a próxima ilha na sequência.
            EnumNomeIlha proximaIlhaEnum = todasAsIlhas[posicaoAtual + 1];

            // 8. Cria a nova ilha a ser adicionada.
            Ilha novaIlha = new Ilha();
            novaIlha.setNomeIlha(proximaIlhaEnum);
            novaIlha.setEstado(true);
            novaIlha.setFoiJogada(false);
            novaIlha.setProgressoAluno(progresso);


            // vincular ilha e desafio
            switch (proximaIlhaEnum) {
                case CIENCIAS, MATEMATICA -> {
                    Desafio desafio1 = new Desafio(EnumTiposDesafios.JogoConecta, novaIlha);
                    novaIlha.getDesafios().add(desafio1);
                }
                case GEOGRAFIA , HISTORIA -> {
                    Desafio desafio2 = new Desafio(EnumTiposDesafios.JogoPalavras, novaIlha);
                    novaIlha.getDesafios().add(desafio2);
                }
                default -> {
                    return  -1;
                }
            }
            // 9. Adiciona a nova ilha à lista do progresso e salva TUDO no banco.
            progresso.getIlhas().add(novaIlha);
            acessoBDProgressoAluno.salvarProgressoAluno(progresso);

            // 10. Retorna o ordinal da nova ilha criada.
            return proximaIlhaEnum.ordinal();

        } catch (Exception e) {
            System.err.println("Erro genérico ao avançar ilha: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Verifica se uma ilha específica já foi jogada
     * @param idProgressoAluno ID do progresso do aluno
     * @param nomeIlha Nome da ilha (DADOLANDIA, CIENCIAS, MATEMATICA, GEOGRAFIA, HISTORIA)
     * @return true se foi jogada, false caso contrário
     */
    @GetMapping("/verificar-foi-jogada")
    public boolean verificarSeIlhaFoiJogada(
            @RequestParam int idProgressoAluno,
            @RequestParam String nomeIlha) {
        try {
            // Converte a string para o enum
            EnumNomeIlha enumNomeIlha = EnumNomeIlha.valueOf(nomeIlha.toUpperCase());
            
            // Busca e retorna o status foiJogada
            Optional<Boolean> foiJogada = acessoBDIlha.verificarSeIlhaFoiJogada(idProgressoAluno, enumNomeIlha);
            
            // Se a ilha não foi encontrada, retorna false
            return foiJogada.orElse(false);
            
        } catch (IllegalArgumentException e) {
            System.err.println("Nome de ilha inválido: " + nomeIlha);
            return false;
        } catch (Exception e) {
            System.err.println("Erro ao verificar se ilha foi jogada: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
