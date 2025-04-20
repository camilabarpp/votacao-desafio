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

-- Criação da view que retorna os dados no formato JSON (calculando votingSessionOpen dinamicamente)
CREATE OR REPLACE VIEW vw_resultado_votacao AS
SELECT json_build_object(
               'id', p.id,
               'title', p.titulo,
               'description', p.descricao,
               'createdAt', p.data_criacao,
               'voatingSessions', json_build_object(
                       'votingSessionStartedAt', sv.data_inicio,
                       'votingSessionEndedAt', sv.data_fim,
                       'votingSessionOpen', (
                           CASE
                               WHEN sv.data_inicio <= CURRENT_TIMESTAMP AND
                                    (sv.data_fim IS NULL OR sv.data_fim > CURRENT_TIMESTAMP)
                                   THEN TRUE
                               ELSE FALSE
                               END
                           ),
                       'votes', (SELECT json_agg(
                                                json_build_object(
                                                        'id', v.id,
                                                        'associateId', a.id,
                                                        'associateName', a.nome,
                                                        'associateCpf', a.cpf,
                                                        'votedOption', v.opcao_voto,
                                                        'votedAt', v.data_voto
                                                )
                                        )
                                 FROM votos v
                                          JOIN associados a ON v.associado_id = a.id
                                 WHERE v.sessao_id = sv.id),
                       'votesCount', (SELECT COUNT(*) FROM votos WHERE sessao_id = sv.id),
                       'votesCountYes', (SELECT COUNT(*) FROM votos WHERE sessao_id = sv.id AND opcao_voto = 'YES'),
                       'votesCountNo', (SELECT COUNT(*) FROM votos WHERE sessao_id = sv.id AND opcao_voto = 'NO')
                                  )
       ) AS resultado_json
FROM pautas p
         JOIN sessao_votacao sv ON p.id = sv.pauta_id;

-- Função para registrar voto e retornar resultado JSON (verificando se a sessão está aberta dinamicamente)
CREATE OR REPLACE FUNCTION registrar_voto(
    p_pauta_id INTEGER,
    p_associado_id INTEGER,
    p_opcao_voto VARCHAR(3)
) RETURNS JSON AS
$$
DECLARE
    v_sessao_id INTEGER;
    v_resultado JSON;
BEGIN
    -- Verificar se existe uma sessão de votação aberta para a pauta (calculado dinamicamente)
    SELECT id
    INTO v_sessao_id
    FROM sessao_votacao
    WHERE pauta_id = p_pauta_id
      AND data_inicio <= CURRENT_TIMESTAMP
      AND (data_fim IS NULL OR data_fim > CURRENT_TIMESTAMP);

    IF v_sessao_id IS NULL THEN
        RAISE EXCEPTION 'Não existe sessão de votação aberta para esta pauta';
    END IF;

    -- Tentar inserir o voto (a constraint unique_voto impedirá votos duplicados)
    INSERT INTO votos (sessao_id, associado_id, opcao_voto)
    VALUES (v_sessao_id, p_associado_id, p_opcao_voto);

    -- Obter o resultado JSON
    SELECT resultado_json
    INTO v_resultado
    FROM vw_resultado_votacao
    WHERE (resultado_json ->> 'id')::INTEGER = p_pauta_id;

    RETURN v_resultado;
EXCEPTION
    WHEN unique_violation THEN
        RAISE EXCEPTION 'Este associado já votou nesta pauta';
END;
$$ LANGUAGE plpgsql;

-- Função auxiliar para buscar sessões de votação abertas
CREATE OR REPLACE FUNCTION obter_sessoes_abertas()
    RETURNS TABLE
            (
                id          INTEGER,
                pauta_id    INTEGER,
                data_inicio TIMESTAMP,
                data_fim    TIMESTAMP
            )
AS
$$
BEGIN
    RETURN QUERY
        SELECT sv.id,
               sv.pauta_id,
               sv.data_inicio,
               sv.data_fim
        FROM sessao_votacao sv
        WHERE sv.data_inicio <= CURRENT_TIMESTAMP
          AND (sv.data_fim IS NULL OR sv.data_fim > CURRENT_TIMESTAMP);
END;
$$ LANGUAGE plpgsql;
