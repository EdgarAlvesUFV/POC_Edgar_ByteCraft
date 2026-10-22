Você é um analista de segurança de software especializado em análise estática de código.

Sua tarefa é analisar o código-fonte fornecido e determinar se ele contém vulnerabilidades de segurança.

Siga estas instruções rigorosamente:

Identifique vulnerabilidades de segurança com base em padrões conhecidos, como CWE (Common Weakness Enumeration).
Relate apenas vulnerabilidades reais. Não especule nem invente problemas.
Se nenhuma vulnerabilidade for encontrada, retorne uma lista vazia.
Para cada vulnerabilidade encontrada, forneça:
"cwe_id": Identificador CWE (ex: CWE-89)
"vulnerability_type": Descrição curta da vulnerabilidade
"severity": Um dos valores ["LOW", "MEDIUM", "HIGH", "CRITICAL"]
"line": Número da linha onde o problema ocorre (ou a melhor aproximação)
Não inclua explicações fora do JSON.
Garanta que a saída seja um JSON válido.

Formato de saída:

{
"vulnerabilities": [
{
"cwe_id": "CWE-XXX",
"vulnerability_type": "Descrição",
"severity": "LOW|MEDIUM|HIGH|CRITICAL",
"line": <número>,
"justification": "Explicação técnica curta"
}
]
}

Agora analise o seguinte código:
=== Código Fonte ===
package ufv.desconecta.Desconecta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ufv.desconecta.Desconecta.model.EnumNomeIlha;
import ufv.desconecta.Desconecta.model.Aluno;
import ufv.desconecta.Desconecta.model.Ilha;
import ufv.desconecta.Desconecta.model.ProgressoAluno;
import ufv.desconecta.Desconecta.repository.AcessoBDAluno;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/aluno")
@CrossOrigin(origins = "*") // permitir requisições do React
public class ControladorAluno {

    private final AcessoBDAluno acessoBDAluno;

    @Autowired
    public ControladorAluno(AcessoBDAluno acessoBDAluno) {
        this.acessoBDAluno = acessoBDAluno;
    }

    public Boolean validarDadosAluno(String apelido, String senha){
        if(apelido == null || apelido.isEmpty() || senha == null || senha.isEmpty()){
            return false;
        }
        return true;
    }

    @PostMapping("/cadastro")
    public Boolean realizarCadastro(@RequestBody Aluno aluno){
        if(validarDadosAluno(aluno.getApelido(), aluno.getSenha())){
            // Verifica se o apelido já existe antes de tentar inserir
            if(acessoBDAluno.verificarApelidoExistente(aluno.getApelido())){
                return false; // Apelido já existe, retorna false
            }
            ProgressoAluno novoProgresso = new ProgressoAluno();
            novoProgresso.setPontuacaoTotalAluno(0);

            Ilha ilhaInicial = new Ilha();
            ilhaInicial.setNomeIlha(EnumNomeIlha.DADOLANDIA);
            ilhaInicial.setEstado(true);
            ilhaInicial.setFoiJogada(false);

            novoProgresso.setAluno(aluno);

            novoProgresso.setIlhas(new ArrayList<>());
            novoProgresso.getIlhas().add(ilhaInicial);
            ilhaInicial.setProgressoAluno(novoProgresso);


            return acessoBDAluno.inserirAluno(aluno);
        } else {
            return false;
        }
    }

    @PostMapping("/login")
    public Long autenticarAluno(@RequestBody Aluno loginData){
        if(validarDadosAluno(loginData.getApelido(), loginData.getSenha())){
            Aluno aluno = acessoBDAluno.buscarApelido(loginData.getApelido());
            if(aluno != null && aluno.getSenha().equals(loginData.getSenha())){
                return aluno.getPK_Aluno(); // Retorna o ID do aluno
            }
        }
        return 0L; // Retorna 0 para falha na autenticação
    }

    @GetMapping("/buscar/{apelido}")
    public Aluno buscarAluno(@PathVariable String apelido){
        return acessoBDAluno.buscarApelido(apelido);
    }

    @GetMapping("/verificar/{apelido}")
    public Boolean verificarApelidoExistente(@PathVariable String apelido){
        return acessoBDAluno.verificarApelidoExistente(apelido);
    }

    @DeleteMapping("/deletar/{apelido}")
    public Boolean deletarAluno(@PathVariable String apelido){
        return acessoBDAluno.apagarAluno(apelido);
    }


}package ufv.desconecta.Desconecta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; // Adicione esta importação
import org.springframework.web.bind.annotation.*;
import ufv.desconecta.Desconecta.repository.AcessoBDCacaPalavras;
import ufv.desconecta.Desconecta.service.SolucionarDesafio; // Adicione esta importação

import java.util.List;

@Service("JogoPalavras")
@RequestMapping("/api/cacaPalavras")
@CrossOrigin("*")
public class ControladorCacaPalavra implements SolucionarDesafio { // Implemente a interface

    @Autowired
    private AcessoBDCacaPalavras acessoBDCacaPalavras;

    // Dentro da classe ControladorCacaPalavra

    @Override
    public String verificarAgrupamento(int id, String tentativa) {
        System.out.println("Tentativa recebida: " + tentativa);

        // 1. Pega a lista de respostas do banco
        List<String> respostas = acessoBDCacaPalavras.getRespostas(id);
        System.out.println("Respostas corretas do banco: " + respostas);

        // 2. Verifica se a tentativa do jogador (a string de índices) está na lista
        if (respostas != null && respostas.contains(tentativa)) {
            return "True";
        }

        // 3. Se não, a resposta está incorreta
        return "False";
    }

    @GetMapping("/getList/{id}")
    public List<String> getListaRespostas(@PathVariable("id") int PK_CacaPalavras){
        List<String> respostas = acessoBDCacaPalavras.getRespostas(PK_CacaPalavras);
        System.out.println(respostas);
        return respostas;
    }
}package ufv.desconecta.Desconecta.controller;

import org.springframework.web.bind.annotation.*;
import ufv.desconecta.Desconecta.dto.ClassificacaoDTO;
import ufv.desconecta.Desconecta.model.ProgressoAluno;
import ufv.desconecta.Desconecta.repository.AcessoBDProgressoAluno;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/classificacao")
@CrossOrigin(origins = "*")
public class ControladorClassificacao {

  private AcessoBDProgressoAluno acessoBDProgressoAluno;

  public ControladorClassificacao(AcessoBDProgressoAluno acessoBDProgressoAluno) {
    this.acessoBDProgressoAluno = acessoBDProgressoAluno;
  }

  @GetMapping("/classificacaoGeral")
  public List<ClassificacaoDTO> buscarClassificacaoGeral() {
    List<ClassificacaoDTO> listaClassificacaoGeral = acessoBDProgressoAluno.obterClassificacaoGeralOrdenada();
    for (ClassificacaoDTO progresso : listaClassificacaoGeral) {
      System.out.println("Nome: " + progresso.getNomeAluno() +
          " Pontuação: " + progresso.getPontuacaoTotalAluno());
    }
    return listaClassificacaoGeral;
  }

  @GetMapping("/classificacaoAluno")
  public Map<String, Object> buscarClassificacaoAluno(@RequestParam String apelidoAluno) {
    List<ClassificacaoDTO> listaClassificacaoGeral = acessoBDProgressoAluno.obterClassificacaoGeralOrdenada();

    Map<String, Object> resultado = new HashMap<>();

    // Procurar o aluno na lista ordenada
    for (int i = 0; i < listaClassificacaoGeral.size(); i++) {
      ClassificacaoDTO classificacao = listaClassificacaoGeral.get(i);
      if (classificacao.getNomeAluno().equalsIgnoreCase(apelidoAluno)) {
        resultado.put("posicao", i + 1);
        resultado.put("nomeAluno", classificacao.getNomeAluno());
        resultado.put("pontuacaoTotalAluno", classificacao.getPontuacaoTotalAluno());

        System.out.println("Classificação do aluno '" + apelidoAluno + "': " +
            "Posição: " + (i + 1) + ", Pontuação: " + classificacao.getPontuacaoTotalAluno());

        return resultado;
      }
    }

    // Se não encontrar o aluno
    System.err.println("Aluno '" + apelidoAluno + "' não encontrado no ranking.");
    resultado.put("erro", "Aluno não encontrado");
    return resultado;
  }
}
package ufv.desconecta.Desconecta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ufv.desconecta.Desconecta.repository.AcessoBDConecta;
import ufv.desconecta.Desconecta.service.SolucionarDesafio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service("JogoConecta")
@CrossOrigin(origins = "*")
public class ControladorConecta implements SolucionarDesafio { // Implemente a interface

    @Autowired
    private AcessoBDConecta acessoBDConecta;


    @Override
    public String verificarAgrupamento(int idDesafio, String respostaAluno) {
        // 1. Pega a lista de respostas do banco
        List<String> respostasCertas = new ArrayList<>(acessoBDConecta.getRespostas(idDesafio));

        System.out.println(respostaAluno);

        if (respostasCertas.isEmpty()) {
            return "False"; // ID não encontrado
        }

        // 2. Ordena a lista de respostas em ordem alfabética
        Collections.sort(respostasCertas);

        // 3. Junta a lista ordenada em uma única string (ex: "CachorroGatoPássaroPeixe")
        String respostaCorretaConcatenada = String.join("", respostasCertas);
        System.out.println(respostaCorretaConcatenada);

        // 4. Compara a resposta correta concatenada com a tentativa do aluno
        if (respostaCorretaConcatenada.contains(respostaAluno)) {
            return "True";
        } else {
            return "False";
        }
    }


    @PutMapping("/atualizar/{id}")
    public Boolean atualizarRespostaCerta(
            @PathVariable("id") int PK_Conecta,
            @RequestParam("novaResposta") String novaResposta,
            @RequestParam("idResposta") int idResposta) {
        System.out.println("ID Resposta: " + idResposta);
        System.out.println("Nova Resposta: " + novaResposta);
        System.out.println("PK_Conecta: " + PK_Conecta);
        return acessoBDConecta.atualizarRespostaCerta(PK_Conecta, novaResposta, idResposta);
    }
}
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


}package ufv.desconecta.Desconecta.controller;

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
package ufv.desconecta.Desconecta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ufv.desconecta.Desconecta.model.Aluno;
import ufv.desconecta.Desconecta.model.Desafio;
import ufv.desconecta.Desconecta.model.Ilha;
import ufv.desconecta.Desconecta.repository.AcessoBDAluno;
import ufv.desconecta.Desconecta.repository.AcessoBDDesafio;
import ufv.desconecta.Desconecta.repository.AcessoBDProgressoAluno;

import ufv.desconecta.Desconecta.model.ProgressoAluno;

import java.util.List;

@RestController
@RequestMapping("/api/progresso-aluno")
@CrossOrigin(origins = "*")
public class ControladorProgressoAluno {

  private final AcessoBDProgressoAluno acessoBDProgressoAluno;

  private final AcessoBDAluno acessoBDAluno;

  private final AcessoBDDesafio acessoBDDesafio;

  @Autowired
  public ControladorProgressoAluno(AcessoBDProgressoAluno acessoBDProgressoAluno, AcessoBDAluno acessoBDAluno,
      AcessoBDDesafio acessoBDDesafio) {
    this.acessoBDDesafio = acessoBDDesafio;
    this.acessoBDProgressoAluno = acessoBDProgressoAluno;
    this.acessoBDAluno = acessoBDAluno;
  }

  @GetMapping("/{idAluno}")
  public ProgressoAluno recuperarProgressoAluno(@PathVariable int idAluno) {
    return acessoBDProgressoAluno.getProgressoAluno(idAluno);
  }

  @GetMapping("/id/{idAluno}")
  public Long recuperarIdProgressoAluno(@PathVariable int idAluno) {
    ProgressoAluno progresso = acessoBDProgressoAluno.getProgressoAluno(idAluno);
    return progresso != null ? progresso.getPK_ProgressoAluno() : null;
  }

  @PostMapping("/calcularPontuacaoTotal")
  public int calcularPontuacaoTotal(@RequestParam String apelidoAluno) {
    System.out.println(apelidoAluno);

    Aluno aluno = acessoBDAluno.buscarApelido(apelidoAluno);
    if (aluno == null) {
      System.err.println("Erro ao calcular pontuação: Aluno '" + apelidoAluno + "' não encontrado.");
      return 0;
    }

    ProgressoAluno progresso = acessoBDProgressoAluno.getProgressoPeloAlunoId(aluno.getPK_Aluno());
    if (progresso == null) {
      System.err.println("Erro ao calcular pontuação: Progresso não encontrado para o aluno: " + apelidoAluno);
      return -1;
    }

    List<Desafio> desafiosDoAluno = acessoBDDesafio.getDesafiosDoAluno(aluno.getPK_Aluno());

    int pontuacaoTotal = desafiosDoAluno.stream()
        .filter(Desafio::isConcluido)
        .mapToInt(Desafio::getPontuacaoDesafio)
        .sum();

    progresso.setPontuacaoTotalAluno(pontuacaoTotal);
    acessoBDProgressoAluno.salvarProgressoAluno(progresso);

    System.out.println("Nova pontuação total para '" + apelidoAluno + "': " + pontuacaoTotal);

    return 1;
  }

}
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