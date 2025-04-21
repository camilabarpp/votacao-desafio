CREATE TABLE associados
(
    id            SERIAL PRIMARY KEY,
    nome          VARCHAR(100) NOT NULL,
    cpf           VARCHAR(11)  NOT NULL UNIQUE,
    email         VARCHAR(100) NOT NULL UNIQUE,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pautas
(
    id           SERIAL PRIMARY KEY,
    titulo       VARCHAR(200) NOT NULL,
    descricao    TEXT,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sessao_votacao
(
    id          SERIAL PRIMARY KEY,
    pauta_id    INTEGER NOT NULL REFERENCES pautas (id),
    data_inicio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_fim    TIMESTAMP,
    CONSTRAINT fk_pauta FOREIGN KEY (pauta_id) REFERENCES pautas (id) ON DELETE CASCADE
);

CREATE TABLE votos
(
    id           SERIAL PRIMARY KEY,
    sessao_id    INTEGER    NOT NULL,
    associado_id INTEGER    NOT NULL,
    opcao_voto   VARCHAR(3) NOT NULL CHECK (opcao_voto IN ('YES', 'NO')),
    data_voto    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sessao FOREIGN KEY (sessao_id) REFERENCES sessao_votacao (id) ON DELETE CASCADE,
    CONSTRAINT fk_associado FOREIGN KEY (associado_id) REFERENCES associados (id) ON DELETE CASCADE,
    CONSTRAINT unique_voto UNIQUE (sessao_id, associado_id)
);
