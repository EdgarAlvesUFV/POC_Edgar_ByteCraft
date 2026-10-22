package br.com.poc.dao;

import br.com.poc.model.Aluno;

import java.util.Optional;

/**
 * Interface para operações de acesso a dados de Aluno.
 * Define métodos para buscar e salvar alunos.
 *
 * @author Nana
 */
public interface AlunoDAO {
    /**
     * Busca um aluno pelo seu apelido.
     * Optional é usado para evitar null pointer exception.
     *
     * @param apelido - Apelido do aluno a ser buscado.
     * @return Optional<Aluno> - Aluno encontrado ou vazio se não encontrado.
     */
    Optional<Aluno> findByApelido(String apelido);

    /**
     * Salva um aluno no banco de dados.
     *
     * @param aluno - Aluno a ser salvo.
     * @return Aluno - Aluno salvo com ID gerado.
     */
    Aluno salvar(Aluno aluno);
}