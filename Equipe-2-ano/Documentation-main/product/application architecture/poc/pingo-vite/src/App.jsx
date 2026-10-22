import React, { useState } from 'react';
import Login from './Login';
import ListaFases from './ListaFases';
import Fase03 from './Fase03';
import Fase04 from './Fase04';
import './App.css';

/**
 * Componente principal da aplicação.
 */
function App() {
    /**
     * Cria uma variável de estado chamada 'aluno' e uma função para atualizá-la, 'setAluno'.
     * O valor inicial de 'aluno' é null (nenhum aluno logado).
     *
     * Cria uma variável de estado chamada 'faseAtiva' e uma função para atualizá-la, 'setFaseAtiva'.
     * O valor inicial de 'faseAtiva' é null (nenhuma fase ativa).
     */
    const [aluno, setAluno] = useState(null);
    const [faseAtiva, setFaseAtiva] = useState(null);

    /**
     * As funções abaixo são manipuladores de eventos.
     * Elas são chamadas em resposta a eventos na interface do usuário.
     */

    /**
     * O estado 'aluno': Guarda os dados do aluno que fez login.
     * Começa como 'null' porque, ao iniciar a aplicação, ninguém está logado.
     * Quando o aluno faz login com sucesso, essa função é chamada com os dados do aluno.
     * A função 'setAluno' atualiza o estado 'aluno' com os dados do aluno logado.
     * Isso faz com que a aplicação saiba quem está usando e possa mostrar as fases disponíveis.
     */
    const handleLoginSuccess = (alunoLogado) => {
        setAluno(alunoLogado);
    };

    /**
     * Função chamada quando uma fase é selecionada na lista.
     * Se a fase não estiver bloqueada, atualiza o estado 'faseAtiva' com a fase selecionada.
     * Se a fase estiver bloqueada, exibe um alerta pedindo para completar a fase anterior.
     */
    const handleFaseSelect = (fase) => {
        if (!fase.bloqueada) {
            setFaseAtiva(fase);
        } else {
            alert("Ops! E a fase anterior?");
        }
    };

    /**
     * Função chamada para sair da fase ativa.
     * Atualiza o estado 'faseAtiva' para null, retornando à lista de fases.
     */
    const handleSairDaFase = () => {
        setFaseAtiva(null);
    }

    /**
     * Aqui é decidido qual componente renderizar com base no estado atual.
     * Se 'aluno' for null, renderiza o componente de Login.
     * Se houver uma 'faseAtiva', renderiza o componente da fase correspondente.
     * Caso contrário, renderiza a lista de fases disponíveis.
     */
    if (!aluno) {
        return <Login onLoginSuccess={handleLoginSuccess} />;
    }

    if (faseAtiva) {
        if (faseAtiva.id === 3) {
            return <Fase03 onSair={handleSairDaFase} />;
        }
        if (faseAtiva.id === 4) {
            return <Fase04 onSair={handleSairDaFase} />;
        }
        return (
            <div>
                <h2>Fase {faseAtiva.id} ainda não implementada.</h2>
                <button onClick={handleSairDaFase}>Voltar</button>
            </div>
        );
    }

    return <ListaFases onFaseSelect={handleFaseSelect} />;
}

/**
 * Exporta o componente App para que possa ser usado em outros arquivos.
 */
export default App;