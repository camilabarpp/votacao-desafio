CREATE TABLE IF NOT EXISTS pautas (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS associados (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    cpf VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sessoes_votacao (
    id SERIAL PRIMARY KEY,
    pauta_id BIGINT NOT NULL,
    voting_session_started_at TIMESTAMP NOT NULL,
    voting_session_ended_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sessao_pauta
        FOREIGN KEY (pauta_id)
        REFERENCES pautas (id)
); 

CREATE TABLE IF NOT EXISTS votos (
    id SERIAL PRIMARY KEY,
    voting_session_id BIGINT NOT NULL,
    associated_id VARCHAR(255) NOT NULL,
    voted_option VARCHAR(10) NOT NULL,
    voted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_voto_sessao
        FOREIGN KEY (voting_session_id)
        REFERENCES sessoes_votacao (id)
);
