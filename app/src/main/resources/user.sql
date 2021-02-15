drop table "user";

create table "user"(
  id number,
  "login" varchar(128) not null,
  first_name varchar(128) not null,
  last_name varchar(128) not null,
  primary key("login")
);
COMMIT;

insert into "user" (id, "login", first_name, last_name) values (3, 'USER1', 'IVANOV', 'IVAN');
insert into "user" (id, "login", first_name,last_name) values (8, 'USER2', 'PETRON', 'PETR');
insert into "user" (id, "login", first_name, last_name) values (10, 'USER3', 'SOLTIKOV', 'PAVEL');
insert into "user" (id, "login", first_name, last_name) values (11, 'USER4', 'ABAGUAEVA', 'LUDMILA');
