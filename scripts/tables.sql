create table ACCOUNT (ACCOUNT_NUMBER VARCHAR(7) not null	primary key,	NAME VARCHAR(30) not null,	BALANCE DOUBLE not null, CURRENCY VARCHAR(10) not null, VERSION number(4), CREATION_DATE TIMESTAMP(1))