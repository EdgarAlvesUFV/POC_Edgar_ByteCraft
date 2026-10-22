import React, { useState, useEffect, useRef, useCallback } from 'react';
import './App.css';

import playerImg from './assets/fase4/dove-.gif';
import obstacleImg from './assets/fase4/beetle.gif';
import collectibleImg from './assets/fase4/star.gif';

const LARGURA_JOGO = 800;
const ALTURA_JOGO = 600;
const PLAYER_SPEED = 8;
const OBJECT_SPEED = 2;
const GOAL_SCORE = 20;

const Fase04 = ({ onSair }) => {
    const [playerPositionY, setPlayerPositionY] = useState(ALTURA_JOGO / 2);
    const [obstacles, setObstacles] = useState([]);
    const [collectibles, setCollectibles] = useState([]);
    const [score, setScore] = useState(0);
    const [lives, setLives] = useState(3);
    const [gameOver, setGameOver] = useState(false);
    const [isWinner, setIsWinner] = useState(false);

    const keysPressed = useRef({});
    const playerY = useRef(ALTURA_JOGO / 2);
    const obstaclesRef = useRef([]);
    const collectiblesRef = useRef([]);
    const scoreRef = useRef(0);
    const livesRef = useRef(3);
    const requestRef = useRef();

    useEffect(() => {
        const handleKeyDown = (e) => { keysPressed.current[e.key] = true; };
        const handleKeyUp = (e) => { keysPressed.current[e.key] = false; };
        window.addEventListener('keydown', handleKeyDown);
        window.addEventListener('keyup', handleKeyUp);
        return () => {
            window.removeEventListener('keydown', handleKeyDown);
            window.removeEventListener('keyup', handleKeyUp);
        };
    }, []);

    const gameLoop = () => {
        // Mover jogador
        if (keysPressed.current['ArrowUp']) {
            playerY.current = Math.max(0, playerY.current - PLAYER_SPEED);
        }
        if (keysPressed.current['ArrowDown']) {
            playerY.current = Math.min(ALTURA_JOGO - 50, playerY.current + PLAYER_SPEED);
        }

        // Mover obstáculos e itens
        obstaclesRef.current = obstaclesRef.current
            .map(o => ({ ...o, x: o.x - OBJECT_SPEED }))
            .filter(o => o.x > -50);
        collectiblesRef.current = collectiblesRef.current
            .map(c => ({ ...c, x: c.x - OBJECT_SPEED }))
            .filter(c => c.x > -50);

        // Gerar novos objetos
        if (Math.random() < 0.02) {
            const newY = Math.random() * (ALTURA_JOGO - 50);
            obstaclesRef.current.push({ id: Date.now(), x: LARGURA_JOGO, y: newY });
        }
        if (Math.random() < 0.02) {
            const newY = Math.random() * (ALTURA_JOGO - 30);
            collectiblesRef.current.push({ id: Date.now(), x: LARGURA_JOGO, y: newY });
        }

        // Detecção de Colisão
        const playerRect = { x: 50, y: playerY.current, width: 50, height: 50 };
        let hasCollision = false;

        // Itera sobre os obstáculos. Se colidir, remove e para o loop.
        obstaclesRef.current = obstaclesRef.current.filter(obs => {
            if (hasCollision) return true; // Se já colidiu neste quadro, não verifica mais
            const obsRect = { x: obs.x, y: obs.y, width: 50, height: 50 };
            if (isColliding(playerRect, obsRect)) {
                livesRef.current -= 1;
                hasCollision = true;
                return false; // Remove o obstáculo
            }
            return true;
        });

        collectiblesRef.current = collectiblesRef.current.filter(col => {
            const colRect = { x: col.x, y: col.y, width: 30, height: 30 };
            if (isColliding(playerRect, colRect)) {
                scoreRef.current += 2;
                return false; // Remove o item
            }
            return true;
        });

        setPlayerPositionY(playerY.current);
        setObstacles(obstaclesRef.current);
        setCollectibles(collectiblesRef.current);
        setScore(scoreRef.current);
        setLives(livesRef.current);

        requestRef.current = requestAnimationFrame(gameLoop);
    };

    useEffect(() => {
        if (!gameOver) {
            requestRef.current = requestAnimationFrame(gameLoop);
        }
        return () => cancelAnimationFrame(requestRef.current);
    }, [gameOver]);

    useEffect(() => {
        if (lives <= 0 || score >= GOAL_SCORE) {
            setGameOver(true);
            setIsWinner(score >= GOAL_SCORE);
        }
    }, [lives, score]);

    const isColliding = (rect1, rect2) => (
        rect1.x < rect2.x + rect2.width &&
        rect1.x + rect1.width > rect2.x &&
        rect1.y < rect2.y + rect2.height &&
        rect1.y + rect1.height > rect2.y
    );

    const restartGame = () => {
        playerY.current = ALTURA_JOGO / 2;
        obstaclesRef.current = [];
        collectiblesRef.current = [];
        scoreRef.current = 0;
        livesRef.current = 3;
        setPlayerPositionY(ALTURA_JOGO / 2);
        setObstacles([]);
        setCollectibles([]);
        setScore(0);
        setLives(3);
        setGameOver(false);
        setIsWinner(false);
    };

    return (
        <div>
            <h2>Fase 4: Navegação na Fibra Óptica</h2>
            <div className="game-area" style={{ backgroundColor: 'white' }}>
                <img src={playerImg} className="player" style={{ top: playerPositionY, left: 50 }} alt="Jogador" />

                {obstacles.map(obs => <img key={obs.id} src={obstacleImg} className="game-object obstacle" style={{ top: obs.y, left: obs.x }} alt="Obstáculo"/>)}
                {collectibles.map(col => <img key={col.id} src={collectibleImg} className="game-object collectible" style={{ top: col.y, left: col.x }} alt="Coletável"/>)}

                <div className="game-hud" style={{ color: '#333', textShadow: '1px 1px 2px #fff' }}>
                    <span>Pontos: {score} / {GOAL_SCORE}</span>
                    <span>Vidas: {'❤️'.repeat(lives)}</span>
                </div>

                {gameOver && (
                    <div className="game-over-screen">
                        {isWinner ? <h2>Mensagem Entregue com Sucesso! ✨</h2> : <h2>A mensagem se perdeu!</h2>}
                        <p>Pontuação Final: {score}</p>
                        <button onClick={restartGame}>Tentar Novamente</button>
                        <button onClick={onSair} style={{marginLeft: '10px'}}>Sair</button>
                    </div>
                )}
            </div>
        </div>
    );
};

export default Fase04;