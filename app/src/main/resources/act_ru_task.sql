drop table act_ru_task;

create table act_ru_task(
  id number generated by default as identity,
  user_login varchar(128) not null,
  client_id number not null,
  allocation_date date not null,
  primary key(id),
  foreign key(user_login) references "user"("login"),
  foreign key(client_id) references client(client_id)
);

COMMIT;

declare
  u_login varchar(128);
  cl_id number;
  d date;
BEGIN
for i in 1..50
loop
  select "login" into u_login from (select "login" from "user" order by dbms_random.value) where rownum = 1;
  select client_id into cl_id from (select client_id from client order by dbms_random.value) where rownum = 1;
  select to_date(
              trunc(
                   dbms_random.value(to_char(DATE '2020-02-01','J')
                                    ,to_char(DATE '2021-02-01','J')
                                    )
                    ),'J'
               ) into d FROM DUAL;
  insert into act_ru_task(user_login, client_id, allocation_date) values (u_login, cl_id, d);
end loop;

END;