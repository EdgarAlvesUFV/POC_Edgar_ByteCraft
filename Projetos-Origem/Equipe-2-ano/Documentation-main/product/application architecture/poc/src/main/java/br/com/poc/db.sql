CREATE DATABASE pingo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pingo_db;

CREATE TABLE aluno (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       apelido VARCHAR(255) NOT NULL UNIQUE,
                       senha VARCHAR(255) NOT NULL
);

CREATE TABLE fase (
                      id BIGINT PRIMARY KEY,
                      nome VARCHAR(255) NOT NULL,
                      bloqueada BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE ponto_de_rede (
                               id BIGINT PRIMARY KEY,
                               fase_id BIGINT NOT NULL,
                               tipo VARCHAR(50) NOT NULL,
                               pos_x DOUBLE NOT NULL,
                               pos_y DOUBLE NOT NULL,
                               FOREIGN KEY (fase_id) REFERENCES fase(id)
);

INSERT INTO fase (id, nome, bloqueada) VALUES (1, 'Ligando Aparelhos', true);
INSERT INTO fase (id, nome, bloqueada) VALUES (2, 'Enviando Mensagens', true);
INSERT INTO fase (id, nome, bloqueada) VALUES (3, 'Definindo o Caminho', false);
INSERT INTO fase (id, nome, bloqueada) VALUES (4, 'Navegando na Fibra Óptica', false);

INSERT INTO ponto_de_rede (id, fase_id, tipo, pos_x, pos_y) VALUES
                                                                (1, 3, 'partida', 10, 50),
                                                                (2, 3, 'roteador', 30, 30),
                                                                (3, 3, 'roteador', 30, 70),
                                                                (4, 3, 'destino_errado', 50, 30),
                                                                (5, 3, 'switch', 60, 70),
                                                                (6, 3, 'destino', 80, 70);