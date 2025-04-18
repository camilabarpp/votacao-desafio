INSERT INTO associados (nome, cpf, email)
VALUES ('João Silva', '12345678900', 'joao@email.com'),
       ('Maria Santos', '23456789010', 'maria@email.com'),
       ('Pedro Oliveira', '34567890120', 'pedro@email.com'),
       ('Ana Pereira', '78912345600', 'ana@email.com'),
       ('Carlos Ferreira', '45678901230', 'carlos@email.com'),
       ('Lucia Mendes', '56789012340', 'lucia@email.com'),
       ('Roberto Alves', '67890123450', 'roberto@email.com'),
       ('Juliana Costa', '89012345670', 'juliana@email.com'),
       ('Fernando Souza', '90123456780', 'fernando@email.com'),
       ('Beatriz Lima', '01234567890', 'beatriz@email.com');

INSERT INTO pautas (titulo, descricao)
VALUES ('Reforma do Estatuto Social', 'Discussão sobre as alterações propostas no estatuto social da organização.'),
       ('Aumento da Taxa de Condomínio',
        'Proposta para aumentar a taxa mensal de condomínio em 10% para cobrir novos serviços.'),
       ('Construção de Área de Lazer',
        'Avaliação da proposta para construção de uma nova área de lazer com piscina e churrasqueira.'),
       ('Mudança no Regulamento Interno', 'Revisão das regras de convivência e uso das áreas comuns.'),
       ('Contratação de Empresa de Segurança',
        'Avaliação das propostas recebidas para o serviço de segurança patrimonial.');

INSERT INTO sessao_votos (pauta_id, data_inicio, data_fim)
VALUES (1, '2025-01-18T01:06:55.127154', '2025-01-30T10:26:55.12717'),
       (2, '2025-02-15T09:00:00.000000', '2025-02-28T23:59:59.999999'),
       (3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '7 days'),
       (4, '2025-04-01T14:30:00.000000', '2025-05-22T14:30:00.000000'),
       (5, '2025-04-01T10:00:00.000000', '2025-06-25T18:00:00.000000');

INSERT INTO votos (sessao_id, associado_id, opcao_voto, data_voto)
VALUES (1, 1, 'YES', '2025-01-18T01:07:00.123456'),
       (1, 2, 'YES', '2025-01-18T01:07:02.456789'),
       (1, 3, 'NO', '2025-01-18T01:08:12.123456'),
       (1, 4, 'NO', '2025-01-18T01:07:04.978857'),
       (1, 5, 'YES', '2025-01-18T02:15:30.567890'),

       (2, 1, 'NO', '2025-02-15T10:15:00.123456'),
       (2, 2, 'NO', '2025-02-15T11:20:00.456789'),
       (2, 3, 'NO', '2025-02-16T09:05:00.789012'),
       (2, 4, 'YES', '2025-02-16T14:30:00.345678'),
       (2, 6, 'YES', '2025-02-17T08:45:00.901234'),
       (2, 7, 'NO', '2025-02-17T16:20:00.567890'),

       (3, 1, 'YES', CURRENT_TIMESTAMP - INTERVAL '30 minutes'),
       (3, 3, 'YES', CURRENT_TIMESTAMP - INTERVAL '25 minutes'),
       (3, 5, 'YES', CURRENT_TIMESTAMP - INTERVAL '20 minutes'),
       (3, 7, 'NO', CURRENT_TIMESTAMP - INTERVAL '15 minutes'),

       (4, 2, 'YES', '2025-05-01T15:10:00.123456'),
       (4, 4, 'NO', '2025-05-05T16:05:00.456789');