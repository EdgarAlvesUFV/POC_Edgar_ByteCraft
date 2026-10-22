package app.controller;

import app.model.Misterio;
import app.model.Sessao;
import app.model.dto.SessaoMisterioResponseDTO;
import app.model.dto.SessaoRequestDTO;
import app.model.dto.SessaoResponseDTO;
import app.service.MisterioService;
import app.service.SessaoService;
import app.service.TurmaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sessao")
public class SessaoController {

    private final SessaoService sessaoService;
    private final MisterioService misterioService;

    /**
     * Endpoint para iniciar uma nova sessao de jogo para uma turma
     * parametro - idTurma, recebido como uma variavel na URL
     * return - ResponseEntity com status 201 Created, um header location e o corpo da resposta formatado pelo SessaoRepository
     */

    @PostMapping("/{idTurma}")
    public ResponseEntity<SessaoMisterioResponseDTO> iniciarSessao(
            @PathVariable Long idTurma,
            @RequestBody SessaoRequestDTO sessaoRequestDTO
    ) {
        List<Misterio> misterioList = misterioService.sortearMisterios(sessaoRequestDTO.n_misterios());
        SessaoMisterioResponseDTO sessaoResponse = sessaoService.iniciarNovoJogo(idTurma, misterioList);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(sessaoResponse.idSessao())
                .toUri();

        return ResponseEntity.created(location).body(sessaoResponse);
    }

    /**
     * endpoint para finalizar (deletar) uma sessao de jogo existente
     * parametros - idSessao - o id da sessao que queremos finalizar
     * return - ResponseEntity com o status 200 OK e o corpo da sessao que foi finalizada(deletada)
     */

@PutMapping("/{idSessao}/finalizar")
    public ResponseEntity<SessaoResponseDTO> finalizarSessao(@PathVariable Long idSessao){
        Sessao sessaoFinalizada = sessaoService.finalizarSessao(idSessao);
        SessaoResponseDTO responseDTO = new SessaoResponseDTO(sessaoFinalizada);
        return ResponseEntity.ok(responseDTO);
}

}
