import { useState, useEffect } from 'react';

// Hook personalizado para gerenciar o estado do aluno logado
export const useAlunoLogado = () => {
  const [alunoId, setAlunoId] = useState(null);
  const [alunoApelido, setAlunoApelido] = useState(null);
  const [isLogado, setIsLogado] = useState(false);

  // Verificar se há dados do aluno no localStorage ao carregar
  useEffect(() => {
    const id = localStorage.getItem('alunoId');
    const apelido = localStorage.getItem('alunoApelido');
    
    if (id && id !== '0') {
      setAlunoId(parseInt(id));
      setAlunoApelido(apelido);
      setIsLogado(true);
    }
  }, []);

  // Função para fazer login do aluno
  const logarAluno = (id, apelido) => {
    setAlunoId(id);
    setAlunoApelido(apelido);
    setIsLogado(true);
    localStorage.setItem('alunoId', id.toString());
    localStorage.setItem('alunoApelido', apelido);
  };

  // Função para fazer logout do aluno
  const deslogarAluno = () => {
    setAlunoId(null);
    setAlunoApelido(null);
    setIsLogado(false);
    localStorage.removeItem('alunoId');
    localStorage.removeItem('alunoApelido');
  };

  // Função para obter o ID do aluno para requisições
  const obterAlunoId = () => {
    return alunoId || parseInt(localStorage.getItem('alunoId')) || null;
  };

  return {
    alunoId,
    alunoApelido,
    isLogado,
    logarAluno,
    deslogarAluno,
    obterAlunoId
  };
};

export default useAlunoLogado;