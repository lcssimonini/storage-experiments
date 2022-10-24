-- liquibase formatted sql

-- changeset simonini:1666621104331-2
CREATE TABLE public.consultant (id BIGSERIAL NOT NULL, name VARCHAR(255), CONSTRAINT "consultantPK" PRIMARY KEY (id));

-- changeset simonini:1666621104331-1
ALTER SEQUENCE public.hibernate_sequence INCREMENT BY 1;

