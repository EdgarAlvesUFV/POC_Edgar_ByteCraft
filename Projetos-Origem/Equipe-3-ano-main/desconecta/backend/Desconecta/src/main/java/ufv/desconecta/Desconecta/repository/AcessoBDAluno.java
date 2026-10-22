package ufv.desconecta.Desconecta.repository;

import org.springframework.stereotype.Service;
import ufv.desconecta.Desconecta.model.Aluno;


@Service
public class AcessoBDAluno {

    private final RepositorioAluno repositorioAluno;

    public AcessoBDAluno(RepositorioAluno repo) {
        this.repositorioAluno = repo;
    }





    public Boolean inserirAluno(Aluno aluno){
        try {
            // Verifica se já existe um aluno com o mesmo apelido
            if (verificarApelidoExistente(aluno.getApelido())) {
                return false; // Não pode inserir - apelido já existe
            }

            // Salva o aluno no banco de dados
            repositorioAluno.save(aluno);
            return true; // Inserção bem-sucedida

        } catch (Exception e) {
            // Em caso de erro na inserção
            return false;
        }
    }

    public Aluno buscarApelido(String apelido){
        try {
            // Usa método otimizado do repositório
            return repositorioAluno.findByApelido(apelido).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    public Boolean verificarApelidoExistente(String apelido){
        try {
            // Usa método otimizado do repositório
            return repositorioAluno.existsByApelido(apelido);
        } catch (Exception e) {
            // Em caso de erro, assume que não existe
            return false;
        }
    }

    public Boolean apagarAluno(String apelido){
        try {
            // Busca o aluno pelo apelido
            Aluno aluno = buscarApelido(apelido);
            
            if (aluno != null) {
                // Remove o aluno do banco de dados
                repositorioAluno.delete(aluno);
                return true;
            }
            
            return false; // Aluno não encontrado
            
        } catch (Exception e) {
            return false;
        }
    }




}
