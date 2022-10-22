-- liquibase formatted sql

-- changeset simonini:1666479838674-2
CREATE TABLE public.customer (id BIGSERIAL NOT NULL, birth_date date, document VARCHAR(255), last_name VARCHAR(255), name VARCHAR(255), CONSTRAINT "customerPK" PRIMARY KEY (id));

-- changeset simonini:1666479838674-1
ALTER SEQUENCE public.hibernate_sequence INCREMENT BY 1;

