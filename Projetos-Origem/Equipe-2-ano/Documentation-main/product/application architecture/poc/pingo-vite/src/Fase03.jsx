/**
 * useRef é um hook do React que permite criar uma referência mutável que persiste por todo o ciclo de vida do componente.
 */
import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios';

import imgPartida from './assets/fase3/start.gif';
import imgRoteador from './assets/hotspot.gif';
import imgSwitch from './assets/fase3/server-security.gif';
import imgDestino from './assets/fase3/destination.gif';
import imgDestinoErrado from './assets/fase3/wrong-decision.gif';

const nodeImageMap = {
    partida: imgPartida,
    roteador: imgRoteador,
    switch: imgSwitch,
    destino: imgDestino,
    destino_errado: imgDestinoErrado
};

/**
 * Componente responsável por renderizar um ponto.
 */
const Ponto = ({ ponto, onClick }) => (
    <g onClick={() => onClick(ponto)} style={{ cursor: 'pointer' }} className="ponto-container">
        <image
            href={nodeImageMap[ponto.tipo]}
            x={`${ponto.posX}%`}
            y={`${ponto.posY}%`}
            height="70"
            width="70"
            transform="translate(-35, -35)"
        />
    </g>
);

/**
 * Componente responsável por renderizar uma linha ANIMADA entre dois pontos.
 */
const Linha = ({ de, para }) => {
    const pathRef = useRef(null);
    const [comprimento, setComprimento] = useState(0);

    const x1 = de.posX * 8;
    const y1 = de.posY * 6;
    const x2 = para.posX * 8;
    const y2 = para.posY * 6;

    const pathData = `M ${x1} ${y1} L ${x2} ${y2}`;

    useEffect(() => {
        if (pathRef.current) {
            setComprimento(pathRef.current.getTotalLength());
        }
    }, [de, para]);

    return (
        <path
            ref={pathRef}
            d={pathData}
            fill="none"
            stroke="cyan"
            strokeWidth="3"
            strokeDasharray={comprimento}
            strokeDashoffset={comprimento}
            className="linha-animada"
        />
    );
};

/**
 * Componente principal da Fase 03.
 */
const Fase03 = ({ onSair }) => {
    /**
     * Cria uma variável de estado chamada 'pontos' e uma função para atualizá-la, 'setPontos'.
     * O valor inicial de 'pontos' é um array vazio ([]).
     *
     * Cria uma variável de estado chamada 'caminhoAtual' e uma função para atualizá-la, 'setCaminhoAtual'.
     * O valor inicial de 'caminhoAtual' é um array vazio ([]).
     */
    const [pontos, setPontos] = useState([]);
    const [caminhoAtual, setCaminhoAtual] = useState([]);

    /**
     * Efeito que busca os pontos do layout da fase 3 ao montar o componente.
     * O array vazio ([]) como segundo argumento indica que o efeito deve ser executado apenas uma vez, após a montagem do componente.
     * Dentro do useEffect, fazemos uma requisição GET para o endpoint '/api/fases/3/layout' usando axios.
     * Se a requisição for bem-sucedida, atualizamos o estado 'pontos' com os dados recebidos.
     */
    useEffect(() => {
        axios.get('/api/fases/3/layout').then(res => setPontos(res.data));
    }, []);

    /**
     * Função chamada quando um ponto é clicado.
     * Adiciona o ponto clicado ao caminho atual.
     * Se o caminho estiver vazio, só permite adicionar o ponto de "partida".
     */
    const handlePontoClick = (pontoClicado) => {
        if (caminhoAtual.length === 0 && pontoClicado.tipo !== 'partida') {
            alert('Comece pelo ponto de "partida"!');
            return;
        }
        const novoCaminho = [...caminhoAtual, pontoClicado];
        setCaminhoAtual(novoCaminho);
        console.log('Ponto adicionado:', pontoClicado);
    };

    /**
     * Função assíncrona que valida o caminho atual.
     * Envia os IDs dos pontos do caminho atual para o backend para validação.
     * Se o caminho for válido, exibe uma mensagem de sucesso e chama onSair para voltar à lista de fases.
     */
    const handleValidar = async () => {
        if (caminhoAtual.length < 2) {
            alert("Você precisa criar um caminho antes de validar!");
            return;
        }
        const idsDoCaminho = caminhoAtual.map(ponto => ponto.id);
        console.log('Enviando para validação:', idsDoCaminho);
        try {
            const response = await axios.post('/api/fases/3/validar', idsDoCaminho);
            if (response.data === true) {
                alert("Caminho correto!");
                onSair();
            } else {
                alert("Caminho incorreto.");
            }
        } catch (error) {
            console.error("Erro ao validar", error);
        }
    };

    /**
     * Função que refaz o caminho, limpando o estado do caminho atual.
     */
    const handleRefazer = () => {
        setCaminhoAtual([]);
    };

    /**
     * O componente retorna a estrutura visual que será renderizada na tela.
     */
    return (
        <div>
            <h2>Fase 3: Definindo o Caminho</h2>
            <p>Ligue os pontos e depois clique em validar!</p>

            <svg width="800" height="600" style={{ border: '3px solid #ff6384', background: 'white', margin: 'auto', borderRadius: '15px' }}>
                {caminhoAtual.map((ponto, index) => {
                    if (index === 0) return null;
                    const pontoAnterior = caminhoAtual[index - 1];
                    return <Linha key={`line-${index}`} de={pontoAnterior} para={ponto} />;
                })}
                {pontos.map(p => (
                    <Ponto key={`point-${p.id}`} ponto={p} onClick={handlePontoClick} />
                ))}
            </svg>

            <div style={{marginTop: '10px'}}>
                <button onClick={handleValidar}>Validar Caminho</button>
                <button onClick={handleRefazer} style={{marginLeft: '10px'}}>Refazer Caminho</button>
                <button onClick={onSair} style={{marginLeft: '10px'}}>Voltar para Fases</button>
            </div>
        </div>
    );
};

/**
 * Exporta o componente Fase03 para que possa ser usado em outros arquivos.
 */
export default Fase03;