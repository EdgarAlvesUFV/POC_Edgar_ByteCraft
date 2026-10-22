package app.service;

import app.model.dto.TurmaResponseDto;
import app.repository.TurmaRepository;
import app.model.dto.TurmaRequestDto;
import app.model.Professor;
import app.model.Turma;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TurmaService {

    private final TurmaRepository turmaRepository;

    public Turma cadastrarTurma(TurmaRequestDto turma, Professor professor) {

        if (turma.nome().isEmpty()) {
            throw new IllegalArgumentException("O nome da turma não pode ser vazio.");
        }

        Turma novaTurma = new Turma();
        novaTurma.setNome(turma.nome().toUpperCase());
        novaTurma.setAno(turma.ano());
        novaTurma.setTurno(turma.turno());
        novaTurma.setProfessor(professor);


        return turmaRepository.save(novaTurma);
    }

    public List<TurmaResponseDto> listarTodasTurmas() {
        List<TurmaResponseDto> turmas = turmaRepository.buscarTurmas();
        return turmas;
    };

    public List<TurmaResponseDto> listarTurmas(Professor professor) {
        // Busca as turmas do professor pelo repositório
        List<Turma> listaTurmas = turmaRepository.listarTurmasDoProfessor(professor.getId());

        // Converte cada Turma para TurmaRequestDto
        return listaTurmas.stream()
                .map(t -> new TurmaResponseDto(
                        t.getId(),
                        t.getNome(),
                        t.getAno(),
                        t.getTurno()
                ))
                .toList();
    }

    public void deletarTurma(Long id) {
        turmaRepository.deletarTurma(id);
    }

    public TurmaResponseDto modificarTurma(Long id, TurmaRequestDto dto, Professor professor) {
        return turmaRepository.modificarTurma(id, professor.getId(), dto);
    }

    public TurmaResponseDto buscarTurma(Long id) {

        return turmaRepository.buscarTurmaEspecifica(id);
    }

    public Turma buscarTurmaEntity(Long id){
        return turmaRepository.buscarTurmaEntity(id);
    }
}