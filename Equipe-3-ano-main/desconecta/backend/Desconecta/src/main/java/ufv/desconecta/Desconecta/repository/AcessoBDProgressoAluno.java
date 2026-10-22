package ufv.desconecta.Desconecta.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ufv.desconecta.Desconecta.dto.ClassificacaoDTO;
import ufv.desconecta.Desconecta.model.Aluno;
import ufv.desconecta.Desconecta.model.Ilha;
import ufv.desconecta.Desconecta.model.ProgressoAluno;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class AcessoBDProgressoAluno  {

    private final RepositorioProgressoAluno repositorioProgressoAluno;


    public AcessoBDProgressoAluno(RepositorioProgressoAluno repo) {
        this.repositorioProgressoAluno = repo;
    }
    // Método para armazenar a pontuação
    @Transactional
    public boolean armazenarPontuacaoAluno(int idProgressoAluno, int pontuacao) {
        ProgressoAluno progresso = entityManager.find(ProgressoAluno.class, (long) idProgressoAluno);
        if (progresso != null) {
            progresso.setPontuacaoTotalAluno(pontuacao); // supondo que ProgressoAluno tenha o atributo pontuacao
            entityManager.merge(progresso); // atualiza o registro no banco
            return true;
        } else {
            return false; // não encontrou o aluno
        }
    }

    //Método para recuperar o progresso de um aluno
    public ProgressoAluno getProgressoAluno(int idProgressoAluno) {
        return entityManager.find(ProgressoAluno.class, (long) idProgressoAluno);
    }

    // Método para recuperar as ilhas associadas a um progresso de aluno
    public List<Ilha> getIlhasDoProgresso(int idProgressoAluno) {
        try {
            TypedQuery<Ilha> query = entityManager.createQuery(
                    "SELECT i FROM Ilha i WHERE i.progressoAluno.PK_ProgressoAluno = :idProgresso", Ilha.class);
            query.setParameter("idProgresso", (long) idProgressoAluno);
            return query.getResultList();
        } catch (Exception e) {
            // Em caso de erro (ex: progresso não encontrado), retorna uma lista vazia para evitar erros.
            System.err.println("Erro ao buscar ilhas do progresso: " + e.getMessage());
            return Collections.emptyList();
        }
    }


    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void criarProgressoParaAluno(Aluno aluno) {
        ProgressoAluno progresso = new ProgressoAluno();
        progresso.setAluno(aluno); // vincula o aluno

        progresso.setPontuacaoTotalAluno(0);
        progresso.setIlhas(new ArrayList<>());

        // Persiste no banco
        entityManager.persist(progresso);

    }

    // Método para salvar/atualizar o progresso do aluno
    @Transactional
    public ProgressoAluno salvarProgressoAluno(ProgressoAluno progresso) {
        return entityManager.merge(progresso);
    }

    public List<ClassificacaoDTO> obterClassificacaoGeralOrdenada(){
        return repositorioProgressoAluno.findClassificacaoOrdenada();
    }

    public ProgressoAluno getProgressoPeloAlunoId(long alunoId) {
        return repositorioProgressoAluno.findByAlunoId(alunoId).orElse(null);
    }
}