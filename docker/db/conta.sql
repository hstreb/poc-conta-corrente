CREATE TABLE conta (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY NOT NULL,
    versao INTEGER NOT NULL,
    agencia VARCHAR(4) NOT NULL,
    numero INTEGER NOT NULL,
    digito_verificador INTEGER NOT NULL,
    estado VARCHAR(10) NOT NULL,
    data_criacao TIMESTAMP WITHOUT TIME ZONE,
    UNIQUE (agencia, numero)
);

CREATE SEQUENCE conta_seq_numero START 1001;

CREATE TABLE titular (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY NOT NULL,
    versao INTEGER NOT NULL,
    conta UUID NOT NULL,
    documento VARCHAR(30) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    UNIQUE (conta, documento),
    CONSTRAINT titular_conta_fk FOREIGN KEY (conta) REFERENCES conta(id)
);
