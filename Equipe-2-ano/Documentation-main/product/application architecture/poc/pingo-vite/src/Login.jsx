/**
 * O React é uma biblioteca JavaScript para construir interfaces de usuário.
 * useState é um hook do React que permite adicionar estado a componentes funcionais.
 */
import React, { useState } from 'react';
/**
 * A biblioteca axios é usada para fazer requisições HTTP.
 */
import axios from 'axios';
import './App.css';

/**
 * Componente de Login (Login.jsx)
 */
const Login = ({ onLoginSuccess }) => {
    /**
     * Cria uma variável de estado chamada 'apelido' e uma função para atualizá-la, 'setApelido'.
     * O valor inicial de 'apelido' é uma string vazia ('').
     */
    const [apelido, setApelido] = useState('');

    /** Cria uma variável de estado chamada 'senha' e uma função para atualizá-la, 'setSenha'.
     * O valor inicial de 'senha' é uma string vazia ('').
     */
    const [senha, setSenha] = useState('');

    /**
     * Cria uma função assíncrona chamada 'handleSubmit' que é chamada quando o formulário é enviado.
     * - async indica que a função é assíncrona, geralmente usado em funções demoradas.
     */
    const handleSubmit = async (e) => {
        /**
         * e.preventDefault() impede o comportamento padrão do formulário, que é recarregar a página.
         */
        e.preventDefault();
        if (!apelido || !senha) {
            alert("Apelido e senha são obrigatórios!");
            return;
        }
        try {
            /**
             * Faz uma requisição POST para o endpoint '/api/login' com os dados do formulário.
             * - 'await' é usado para esperar a resposta da requisição antes de continuar.
             * - axios.post é um método da biblioteca axios para fazer requisições POST.
             * - {apelido, senha} é o payload da requisição, um objeto json.
             */
            const response = await axios.post('/api/login', { apelido, senha });
            /**
             * 'onLoginSuccess' é a função chamada quando a requisição foi bem sucedida.
             */
            onLoginSuccess(response.data);
        } catch (error) {
            console.error("Erro ao fazer login", error);
            alert("Falha no login. Verifique se o backend está rodando.");
        }
    };

    /**
     *  O componente retorna a estrutura visual que será renderizada na tela.
     */
    return (
        <div className="login-container">
            <h2>Login do Aluno</h2>
            <form onSubmit={handleSubmit} className="login-form">
                <input
                    type="text"
                    value={apelido}
                    onChange={e => setApelido(e.target.value)}
                    placeholder="Apelido"
                    aria-label="Apelido"
                />
                <input
                    type="password"
                    value={senha}
                    onChange={e => setSenha(e.target.value)}
                    placeholder="Senha"
                    aria-label="Senha"
                />
                <button type="submit">Entrar</button>
            </form>
        </div>
    );
};

/**
 * Exporta o componente Login para que possa ser usado em outros arquivos.
 */
export default Login;