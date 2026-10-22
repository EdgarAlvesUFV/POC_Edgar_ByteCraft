package app.service;

import app.exception.BusinessRuleException;
import app.exception.ResourceNotFoundException;
import app.model.Misterio;
import app.model.Sessao;
import app.model.StatusSessao;
import app.model.Turma;
import app.model.dto.MisterioSessaoDTO;
import app.model.dto.SessaoMisterioResponseDTO;
import app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessaoService {

        //Injecao de dependencia dos repositorios necessarios
        private final MisterioRepository misterioRepository;
        private final SessaoRepository sessaoRepository;
        private final TurmaRepository turmaRepository;
    private final PistaRepository pistaRepository;
    private final RespostaRepository respostaRepository;

    //implementado no caso de uso 3?
   //     public Sessao CriarSessao(long turmaId){
   //         Turma turma = turmaService.buscarTurmaEntity(turmaId);
   //         Sessao sessao =  sessaoRepository.criarSessao(turma);
   //         List<Misterio> misterios = misterioService.sortearMisterios(sessao.getIdSessao(), 10);
   //         sessao.setMisterios(misterios);
   //         return sessao;
   //     }

       /**
        * Criacao de uma nova sessao - CSU02
        * parametros - id da turma para a qual o jogo sera iniciado
        * returns - a entidade sessao que foi criada e persistida
        * throws - ResourceNotFoundException Se a turma com o ID fornecido não existir
        * throws - BusinessRuleException Se não houver mistérios cadastrados no sistema.
       */

       @Transactional
       public SessaoMisterioResponseDTO iniciarNovoJogo(Long idTurma, List<Misterio> misterios) {
           // 1️⃣ Verifica se a turma existe
           Turma turma;
           try {
               turma = turmaRepository.buscarTurmaEntity(idTurma);
           } catch (EmptyResultDataAccessException e) {
               throw new ResourceNotFoundException("Turma com id " + idTurma + " não encontrada.");
           }

           // 2️⃣ Regra de negócio - deve haver ao menos um mistério
           if (misterioRepository.count() == 0) {
               throw new BusinessRuleException("Não é possível iniciar o jogo, pois não há mistérios cadastrados.");
           }

           // 3️⃣ Cria a nova sessão
           Sessao novaSessao = new Sessao(turma);
           Sessao sessaoCriada = sessaoRepository.salvarSessao(novaSessao);

           // 4️⃣ Persiste vínculos e monta lista DTOs
           List<MisterioSessaoDTO> misterioDTOs = new ArrayList<>();

           for (Misterio misterio : misterios) {
               Long sessaoMisterioId = misterioRepository.adicionarMisterioNaSessao(sessaoCriada.getIdSessao(), misterio.getId());
               misterio.setPistas(pistaRepository.retornaPistasVinculadasMisterio(misterio));
               misterio.setRespostas(respostaRepository.alternativasMisterioID(misterio));

               misterioDTOs.add(MisterioSessaoDTO.from(misterio, sessaoMisterioId));
           }

           // 5️⃣ Retorna DTO consolidado
           return new SessaoMisterioResponseDTO(
                   sessaoCriada.getIdSessao(),
                   turma.getId(),
                   misterioDTOs
           );
       }



    /**
     * Finalização de uma sessao - CSU02
     * Logica é encontrar a sessao, altera seu status para FINALIZADA e persiste a mudança
     * parametros - idSessao - id da sessao a ser removida
     * return - o objeto sessao que foi removido
     * throws - ResourceNotFoundException se a sessao nao foi encontrada
     */
    public Sessao finalizarSessao(Long idSessao){
        //Achar a sessao pelo id dela e retorna excessao se nao achar
        Sessao sessao = sessaoRepository.acharPeloId(idSessao)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão com id " + idSessao + "nao encontrada."));
        //Altera o status do objeto em memoria, para FINALIZADA
        sessao.setStatusSessao(StatusSessao.FINALIZADA);
        //Persistir a mudanca no banco de dados
        sessaoRepository.salvarSessao(sessao);
        //retorna o objeto atualizado
        return sessao;
    }

}
