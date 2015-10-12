DROP TABLE ContactEntity if exists;
DROP TABLE UserEntity if exists;

CREATE TABLE UserEntity(
	userId integer generated by default as identity (start with 1),
  	username varchar(255) UNIQUE,	
	password varchar(255),
	token varchar(255) UNIQUE,
  	role varchar(255)
  	
);

CREATE TABLE ContactEntity
(
	id integer generated by default as identity (start with 1),
	userIdFk integer,
	customerId character varying(40),
	email character varying(80),
  	firstname character varying(40),
  	lastname character varying(80),
  	name character varying(121),
  	mailingcity character varying(40),
  	mailingcountry character varying(80),
  	mailingpostalcode character varying(20),
  	mailingstreet character varying(255),
  	phone character varying(40),
  	CONSTRAINT contact_pkey PRIMARY KEY (id)
);




