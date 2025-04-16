CREATE TABLE IF NOT EXISTS pautas (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descricao TEXT,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sessoes_votacao (
    id SERIAL PRIMARY KEY,
    pauta_id BIGINT NOT NULL,
    inicio_sessao TIMESTAMP NOT NULL,
    fim_sessao TIMESTAMP NOT NULL,
    sessao_aberta BOOLEAN DEFAULT TRUE,
    CONSTRAINT fk_sessao_pauta
        FOREIGN KEY (pauta_id)
        REFERENCES pautas (id)
);

CREATE TABLE IF NOT EXISTS votos (
    id SERIAL PRIMARY KEY,
    sessao_id BIGINT NOT NULL,
    id_associado VARCHAR(255) NOT NULL,
    voto BOOLEAN NOT NULL,
    data_voto TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_voto_sessao
        FOREIGN KEY (sessao_id)
        REFERENCES sessoes_votacao (id),
    CONSTRAINT uk_associado_sessao
        UNIQUE (sessao_id, id_associado)
);