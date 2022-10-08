CREATE TABLE conta (
    id UUID PRIMARY KEY NOT NULL,
    versao INTEGER NOT NULL,
    agencia VARCHAR(4) NOT NULL,
    conta VARCHAR(10) NOT NULL,
    estado VARCHAR(10) NOT NULL,
    data_criacao TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE tipo_transacao (
    id SERIAL PRIMARY KEY NOT NULL,
    descricao VARCHAR(10) NOT NULL,
    UNIQUE (descricao)
);

CREATE TABLE transacao (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY NOT NULL,
    versao INTEGER NOT NULL,
    conta UUID NOT NULL,
    tipo_transacao INTEGER NOT NULL,
    data_transacao DATE NOT NULL,
    valor NUMERIC(10,2) NULL,
    participante_banco VARCHAR(4) NULL,
    participante_agencia VARCHAR(4) NULL,
    participante_conta VARCHAR(10) NULL,
    descricao VARCHAR(256) NULL,
    transacao_referente UUID NULL,
    data_criacao TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT transacao_conta_fk FOREIGN KEY (conta) REFERENCES conta(id),
    CONSTRAINT transacao_tipo_transacao_fk FOREIGN KEY (tipo_transacao) REFERENCES tipo_transacao(id)
);

CREATE TABLE limite (
    conta UUID PRIMARY KEY NOT NULL,
    versao INTEGER NOT NULL,
    saldo NUMERIC(10,2) NULL,
    saldo_diario NUMERIC(10,2) NULL,
    CONSTRAINT transacao_conta_fk FOREIGN KEY (conta) REFERENCES conta(id)
);

INSERT INTO tipo_transacao(id, descricao) VALUES(1, 'CREDITO');
INSERT INTO tipo_transacao(id, descricao) VALUES(2, 'DEBITO');
INSERT INTO tipo_transacao(id, descricao) VALUES(3, 'ESTORNO');