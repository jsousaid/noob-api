CREATE TABLE order_of_service
(
  id                  UUID                    NOT NULL,
  client_id           UUID                    NOT NULL,
  description	      CHARACTER VARYING(255)  NOT NULL,
  price               DECIMAL(10,2)  		  NOT NULL,
  status              CHARACTER VARYING(20)   NOT NULL,
  opened_date	      TIMESTAMP      		  NOT NULL,
  finished_date		  TIMESTAMP,
  
  CONSTRAINT order_of_service_pkey PRIMARY KEY (id),
  CONSTRAINT order_of_service_client_fkey FOREIGN KEY (client_id) REFERENCES client(id)
);