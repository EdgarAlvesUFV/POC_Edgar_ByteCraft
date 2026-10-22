package ufv.desconecta.Desconecta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ufv.desconecta.Desconecta.model.Desafio;

import java.util.List; // Importar List
import java.util.Optional;

@Repository
public interface RepositorioDesafio extends JpaRepository<Desafio, Integer> {

    /**
     * Busca todos os desafios de um aluno específico, navegando através das
     * entidades Ilha e ProgressoAluno.
     * Esta consulta é a tradução direta da junção de chaves que você descreveu.
     *
     * @param alunoId O ID (PK_Aluno) do aluno.
     * @return Uma lista de todos os Desafios associados ao aluno.
     */
    @Query("SELECT d FROM Desafio d WHERE d.ilha.progressoAluno.aluno.PK_Aluno = :alunoId")
    List<Desafio> findDesafiosByAlunoId(@Param("alunoId") long alunoId);


    // Mantenha o método anterior também, ele é útil para buscas mais específicas.
    @Query("SELECT d FROM Desafio d WHERE " +
            "d.ilha.progressoAluno.aluno.PK_Aluno = :alunoId AND " +
            "d.ilha.PK_Ilha = :ilhaId AND " +
            "d.id = :desafioId")
    Optional<Desafio> findDesafioByIDs(
            @Param("alunoId") long alunoId,
            @Param("ilhaId") int ilhaId,
            @Param("desafioId") int desafioId
    );

    @Query("SELECT d FROM Desafio d WHERE d.ilha.PK_Ilha = :ilhaId")
    Desafio findDesafiosByIlhaId(@Param("ilhaId") int ilhaId);
}