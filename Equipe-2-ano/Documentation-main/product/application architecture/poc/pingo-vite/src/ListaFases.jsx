import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

/**
 * Importa imagens para serem usadas nas fases.
 */
import imagemFase1 from './assets/power-button.gif';
import imagemFase2 from './assets/message.gif';
import imagemFase3 from './assets/way.gif';
import imagemFase4 from './assets/hotspot.gif';

/**
 * Cria um objeto que mapeia o número da fase para a imagem correspondente.
 */
const imagensDasFases = {
    1: imagemFase1,
    2: imagemFase2,
    3: imagemFase3,
    4: imagemFase4,
};


const ListaFases = ({ onFaseSelect }) => {
    /**
     * Cria uma variável de estado chamada 'fases' e uma função para atualizá-la, 'setFases'.
     * O valor inicial de 'fases' é um array vazio ([]).
     */
    const [fases, setFases] = useState([]);

    /**
     * useEffect é um hook do React que permite executar efeitos colaterais em componentes funcionais.
     * O array vazio ([]) como segundo argumento indica que o efeito deve ser executado apenas uma vez, após a montagem do componente.
     * Dentro do useEffect, fazemos uma requisição GET para o endpoint '/api/fases' usando axios.
     * Se a requisição for bem-sucedida, atualizamos o estado 'fases' com os dados recebidos.
     * Se houver um erro na requisição, ele é capturado e logado no console.
     */
    useEffect(() => {
        axios.get('/api/fases')
            .then(response => {
                setFases(response.data);
            })
            .catch(error => {
                console.error("Erro ao buscar fases!", error);
            });
    }, []);

    /**
     * O componente retorna a estrutura visual que será renderizada na tela.
     */
    return (
        <div className="selecao-container">
            <h2>Escolha sua Aventura!</h2>
            <div className="fases-grid">
                {fases.map(fase => (
                    // Cada fase é um card
                    <div
                        key={fase.id}
                        className={`fase-card ${fase.bloqueada ? 'bloqueada' : 'liberada'}`}
                        onClick={() => onFaseSelect(fase)}
                    >
                        {fase.bloqueada && <div className="fase-overlay"></div>}

                        <div
                            className="fase-imagem"
                            style={{ backgroundImage: `url(${imagensDasFases[fase.id]})` }}
                        ></div>

                        {fase.bloqueada && <span className="fase-lock-icon">🔒</span>}

                        <h3>{fase.nome}</h3>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default ListaFases;