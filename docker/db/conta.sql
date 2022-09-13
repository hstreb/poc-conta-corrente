CREATE TABLE conta (
	id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
	agencia VARCHAR(4) NOT NULL,
	numero INTEGER NOT NULL,
	digito_verificador INTEGER NOT NULL,
	estado VARCHAR(10) NOT null,
  data_criacao TIMESTAMP WITHOUT TIME ZONE,
	UNIQUE (agencia, numero)
);

CREATE SEQUENCE conta_seq_numero START 1001;

CREATE TABLE titular (
	id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
	conta uuid NOT NULL,
	documento VARCHAR(30) NOT NULL,
	nome VARCHAR(255) NOT NULL,
	UNIQUE (conta, documento),
	CONSTRAINT titular_conta_fk FOREIGN KEY (conta) REFERENCES conta(id)
);
