CREATE TABLE IF NOT EXISTS alternativas_prova (
    id_alternativas BIGINT AUTO_INCREMENT PRIMARY KEY,
    opcao1 VARCHAR(100) NOT NULL,
    opcao2 VARCHAR(100) NOT NULL,
    opcao3 VARCHAR(100) NOT NULL,
    opcao4 VARCHAR(100) NOT NULL
    );

CREATE TABLE IF NOT EXISTS pergunta_prova (
    id_perguntas BIGINT AUTO_INCREMENT PRIMARY KEY,
    texto VARCHAR(200) NOT NULL UNIQUE,
    imagemUrl VARCHAR(200) NOT NULL,
    audioUrl VARCHAR(200) NOT NULL,
    alternativas_id BIGINT NOT NULL,
    FOREIGN KEY (alternativas_id) REFERENCES alternativas_prova(id_alternativas)
    );

CREATE TABLE IF NOT EXISTS professores (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    hash_senha VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS turmas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    ano INT NOT NULL,
    turno VARCHAR(50) NOT NULL,
    professor_id BIGINT NOT NULL,
    CONSTRAINT fk_turmas_professor
    FOREIGN KEY (professor_id) REFERENCES professores(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    );

CREATE TABLE IF NOT EXISTS misterios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS pistas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conteudo TEXT NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    misterio_id BIGINT NOT NULL,
    CONSTRAINT fk_pista_misterio
    FOREIGN KEY (misterio_id) REFERENCES misterios(id)
    ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS respostas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    descricao TEXT NOT NULL,
    ordem INT NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    misterio_id BIGINT NOT NULL,
    status BOOLEAN NOT NULL,
    CONSTRAINT fk_resposta_misterio
    FOREIGN KEY (misterio_id) REFERENCES misterios(id)
    ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS sessoes (
    id_sessao BIGINT AUTO_INCREMENT PRIMARY KEY,
    turma_id BIGINT NOT NULL,
    status_sessao VARCHAR(50) NOT NULL DEFAULT 'ATIVA',
    CONSTRAINT fk_sessao_turma
    FOREIGN KEY (turma_id) REFERENCES turmas(id)
    ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS sessao_misterios (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  sessao_id BIGINT NOT NULL,
  misterio_id BIGINT NOT NULL,
  FOREIGN KEY (sessao_id) REFERENCES sessoes(id_sessao) ON DELETE CASCADE,
  FOREIGN KEY (misterio_id) REFERENCES misterios(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS progresso (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,
     sessao_misterio_id BIGINT UNIQUE NOT NULL,
     pontuacaoObtida INT DEFAULT 0,
     tempoResolucaoMs BIGINT DEFAULT 0,
     resolvido BOOLEAN DEFAULT FALSE,
     FOREIGN KEY (sessao_misterio_id) REFERENCES sessao_misterios(id) ON DELETE CASCADE
);



CREATE TABLE IF NOT EXISTS Tentativa (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,
     progressoId BIGINT NOT NULL,
     misterioId BIGINT NOT NULL,
     alternativaEscolhida VARCHAR(255) NOT NULL,
    correta BOOLEAN NOT NULL,
    tempoRespostaMs BIGINT,
    dataHora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tentativa_progresso
    FOREIGN KEY (progressoId) REFERENCES Progresso(id)
    ON DELETE CASCADE,
    CONSTRAINT fk_tentativa_misterio
    FOREIGN KEY (misterioId) REFERENCES misterios(id)
    ON DELETE CASCADE
    );


CREATE TABLE IF NOT EXISTS pistauso (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        progresso_id BIGINT NOT NULL,
                                        sessao_misterio_id BIGINT NOT NULL,
                                        pista_id BIGINT NOT NULL,
                                        ordem INT,
                                        data_hora DATETIME DEFAULT CURRENT_TIMESTAMP,
                                        tipo_pista VARCHAR(50),
    FOREIGN KEY (progresso_id) REFERENCES progresso(id) ON DELETE CASCADE,
    FOREIGN KEY (sessao_misterio_id) REFERENCES sessao_misterios(id) ON DELETE CASCADE
    );

