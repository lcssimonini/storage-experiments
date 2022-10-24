-- liquibase formatted sql

-- changeset simonini:1666621208700-2
ALTER TABLE public.consultant ADD answer_inbounds BOOLEAN;

-- changeset simonini:1666621208700-1
ALTER SEQUENCE public.hibernate_sequence INCREMENT BY 1;

