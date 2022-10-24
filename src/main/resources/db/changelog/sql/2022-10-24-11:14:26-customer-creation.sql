-- liquibase formatted sql

-- changeset simonini:1666620869264-1
CREATE SEQUENCE  IF NOT EXISTS public.hibernate_sequence START WITH 1 INCREMENT BY 1;

-- changeset simonini:1666620869264-2
CREATE TABLE public.customer (id BIGSERIAL NOT NULL, birth_date date, document VARCHAR(255), last_name VARCHAR(255), name VARCHAR(255), CONSTRAINT "customerPK" PRIMARY KEY (id));

