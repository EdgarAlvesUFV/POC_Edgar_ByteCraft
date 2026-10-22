package ufv.desconecta.Desconecta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ufv.desconecta.Desconecta.model.EnumNomeIlha;
import ufv.desconecta.Desconecta.model.EnumTiposDesafios;
import ufv.desconecta.Desconecta.model.Desafio;
import ufv.desconecta.Desconecta.model.Ilha;
import ufv.desconecta.Desconecta.model.ProgressoAluno;
import ufv.desconecta.Desconecta.repository.AcessoBDDesafio;
import ufv.desconecta.Desconecta.repository.AcessoBDIlha;
import ufv.desconecta.Desconecta.repository.AcessoBDProgressoAluno;
import ufv.desconecta.Desconecta.service.PontuacaoService;
import ufv.desconecta.Desconecta.service.SolucionarDesafio; // Importe a nova interface

import java.util.List;
import java.util.Map; // Importe a classe Map

@RestController
@RequestMapping("/api/desafio")
@CrossOrigin(origins = "*")
public class ControladorDesafio {

    private final AcessoBDDesafio acessoBDDesafio;
    private final PontuacaoService pontuacaoService;
    private final AcessoBDIlha acessoBDIlha;
    private final AcessoBDProgressoAluno acessoBDProgressoAluno;


    private final Map<String, SolucionarDesafio> solucionarDesafioMap;

    @Autowired
    public ControladorDesafio(AcessoBDDesafio acessoBDDesafio, PontuacaoService pontuacaoService,
                              AcessoBDIlha acessoBDIlha, AcessoBDProgressoAluno acessoBDProgressoAluno,
                              Map<String, SolucionarDesafio> solucionarDesafioMap) {
        this.acessoBDDesafio = acessoBDDesafio;
        this.pontuacaoService = pontuacaoService;
        this.acessoBDIlha = acessoBDIlha;
        this.acessoBDProgressoAluno = acessoBDProgressoAluno;
        this.solucionarDesafioMap = solucionarDesafioMap;
    }


    @PostMapping("/verificar")
    public String verificar(@RequestParam("tipoDesafio") EnumTiposDesafios tipoDesafio,
                            @RequestParam("id") int id,
                            @RequestParam("tentativa") String tentativa) {

        // Converte o nome do Enum (ex: JogoConecta) para String para usar como chave do mapa
        String chaveDoServico = tipoDesafio.name();

        // Pega o serviço (ControladorConecta ou ControladorCacaPalavra) do mapa
        SolucionarDesafio servico = solucionarDesafioMap.get(chaveDoServico);

        if (servico != null) {
            // Chama o método verificarAgrupamento do serviço encontrado
            return servico.verificarAgrupamento(id, tentativa);
        } else {
            // Se nenhum serviço for encontrado para aquele tipo de desafio, retorna um erro.
            return "Tipo de desafio inválido ou não implementado.";
        }
    }




    @PostMapping("/concluir/{idDesafio}")
    public boolean concluirDesafio(@PathVariable int idDesafio) {
        Desafio desafio = new Desafio();
        desafio.setId(idDesafio);
        desafio.setConcluido(true);
        return acessoBDDesafio.alterarEstadoDesafio(desafio);
    }

    @PostMapping("/salvarPontuacao")
    public int salvarPontuacaoDesafio(@RequestParam long pkAluno,
                                      @RequestParam String nomeIlha,
                                      @RequestParam int tempo,
                                      @RequestParam int numErros) {

        System.out.println("=== ENDPOINT SALVAR PONTUAÇÃO ===");
        System.out.println("PK Aluno: " + pkAluno);
        System.out.println("Nome Ilha: " + nomeIlha);
        System.out.println("Tempo: " + tempo + "s");
        System.out.println("Erros: " + numErros);

        if (pkAluno <= 0 || nomeIlha == null || nomeIlha.isEmpty()) {
            System.out.println("❌ Dados de entrada inválidos!");
            return -1; // Dados de entrada inválidos
        }

        try {
            // Converter nome da ilha para enum
            EnumNomeIlha enumIlha = EnumNomeIlha.valueOf(nomeIlha.toUpperCase());
            System.out.println("Enum da ilha: " + enumIlha);

            // Buscar o progresso do aluno
            ProgressoAluno progressoAluno = acessoBDProgressoAluno.getProgressoAluno((int) pkAluno);

            if (progressoAluno == null) {
                System.out.println("❌ Progresso do aluno não encontrado!");
                return -3; // Progresso não encontrado
            }

            System.out.println("ID Progresso: " + progressoAluno.getPK_ProgressoAluno());

            // Buscar todas as ilhas do progresso do aluno
            List<Ilha> ilhas = acessoBDIlha.recuperarIlhasPorProgressoId(progressoAluno.getPK_ProgressoAluno().intValue());
            System.out.println("Total de ilhas encontradas: " + ilhas.size());

            // Encontrar a ilha específica pelo enum
            Ilha ilhaEncontrada = null;
            for (Ilha ilha : ilhas) {
                if (ilha.getNomeIlha() == enumIlha) {
                    ilhaEncontrada = ilha;
                    break;
                }
            }

            if (ilhaEncontrada == null) {
                System.out.println("❌ Ilha não encontrada: " + nomeIlha);
                return -4; // Ilha não encontrada
            }

            int idIlha = ilhaEncontrada.getPK_Ilha();
            System.out.println("ID da ilha encontrada: " + idIlha);

            // Buscar desafio da ilha
            Desafio desafioASerPontuado = acessoBDDesafio.getDesafioByIlhaId(idIlha);

            if (desafioASerPontuado == null) {
                System.out.println("❌ Desafio não encontrado para a ilha!");
                return -5; // Desafio não encontrado
            }

            if (desafioASerPontuado.isConcluido()) {
                System.out.println("⚠️ Desafio já foi concluído!");
                return -2; // Desafio já concluído
            }

            // Calcular pontuação
            int pontuacao = pontuacaoService.calcularPontuacao(tempo, numErros);
            System.out.println("Pontuação calculada: " + pontuacao);

            // Salvar pontuação
            pontuacaoService.salvarPontuacaoDesafio(pontuacao, desafioASerPontuado);

            // Somar com a pontuação total do aluno
            int novaPontuacaoTotal = progressoAluno.getPontuacaoTotalAluno() + pontuacao;
            progressoAluno.setPontuacaoTotalAluno(novaPontuacaoTotal);
            acessoBDProgressoAluno.salvarProgressoAluno(progressoAluno);
            System.out.println("Nova pontuação total do aluno: " + novaPontuacaoTotal);

            // Marcar desafio como concluído
            concluirDesafio(desafioASerPontuado.getId());

            System.out.println("✅ Pontuação salva com sucesso!");
            return pontuacao;

        } catch (IllegalArgumentException e) {
            System.out.println("❌ Nome de ilha inválido: " + nomeIlha);
            return -6; // Nome de ilha inválido
        } catch (Exception e) {
            System.out.println("❌ Erro ao salvar pontuação: " + e.getMessage());
            e.printStackTrace();
            return -7; // Erro genérico
        }
    }

    @GetMapping("/verificarConcluido")
    public boolean verificarDesafioConcluido(@RequestParam long pkAluno,
                                             @RequestParam String nomeIlha) {

        System.out.println("=== VERIFICAR SE DESAFIO ESTÁ CONCLUÍDO ===");
        System.out.println("PK Aluno: " + pkAluno);
        System.out.println("Nome Ilha: " + nomeIlha);

        if (pkAluno <= 0 || nomeIlha == null || nomeIlha.isEmpty()) {
            System.out.println("❌ Dados de entrada inválidos!");
            return false;
        }

        try {
            // Converter nome da ilha para enum
            EnumNomeIlha enumIlha = EnumNomeIlha.valueOf(nomeIlha.toUpperCase());

            // Buscar o progresso do aluno
            ProgressoAluno progressoAluno = acessoBDProgressoAluno.getProgressoAluno((int) pkAluno);

            if (progressoAluno == null) {
                System.out.println("❌ Progresso do aluno não encontrado!");
                return false;
            }

            // Buscar todas as ilhas do progresso do aluno
            List<Ilha> ilhas = acessoBDIlha.recuperarIlhasPorProgressoId(progressoAluno.getPK_ProgressoAluno().intValue());

            // Encontrar a ilha específica pelo enum
            Ilha ilhaEncontrada = null;
            for (Ilha ilha : ilhas) {
                if (ilha.getNomeIlha() == enumIlha) {
                    ilhaEncontrada = ilha;
                    break;
                }
            }

            if (ilhaEncontrada == null) {
                System.out.println("❌ Ilha não encontrada: " + nomeIlha);
                return false;
            }

            int idIlha = ilhaEncontrada.getPK_Ilha();

            // Buscar desafio da ilha
            Desafio desafio = acessoBDDesafio.getDesafioByIlhaId(idIlha);

            if (desafio == null) {
                System.out.println("❌ Desafio não encontrado para a ilha!");
                return false;
            }

            boolean concluido = desafio.isConcluido();
            System.out.println("Status do desafio: " + (concluido ? "✅ Concluído" : "⏳ Não concluído"));

            return concluido;

        } catch (IllegalArgumentException e) {
            System.out.println("❌ Nome de ilha inválido: " + nomeIlha);
            return false;
        } catch (Exception e) {
            System.out.println("❌ Erro ao verificar desafio: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


}