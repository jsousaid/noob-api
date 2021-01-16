CREATE TABLE client
(
  id                  UUID                    NOT NULL,
  name                CHARACTER VARYING(200)  NOT NULL,
  email               CHARACTER VARYING(200)  NOT NULL,
  phone_number        CHARACTER VARYING(200)  NOT NULL,
  
  CONSTRAINT client_pkey PRIMARY KEY (id)
);