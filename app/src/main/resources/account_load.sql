-- total load
-- Если хотим узнать количество кредитов на пользователя (без учета id кредита)
select art.user_login, count(a.account_id) account_load from act_ru_task art
    join relationship_client_account rca on art.client_id = rca.client_id
    join account a on rca.account_id = a.account_id where a.is_deleted = 0 and (a.b1 > 0 or a.b2 > 0 or a.b3 > 0)
    group by art.user_login;

-- load for a certain day
-- Если хотим узнать нагрузку по количеству кредитов на определенный день (без учета id кредита)
select art.user_login, art.allocation_date, count(a.account_id) account_load from act_ru_task art
    join relationship_client_account rca on art.client_id = rca.client_id
    join account a on rca.account_id = a.account_id where a.is_deleted = 0 and (a.b1 > 0 or a.b2 > 0 or a.b3 > 0)
    group by art.user_login, art.allocation_date order by user_login;

-- расчет нагрузки на каждый день.
select art.user_login, art.allocation_date, a.account_id from act_ru_task art
    join relationship_client_account rca on art.client_id = rca.client_id
    join account a on rca.account_id = a.account_id where a.is_deleted = 0 and (a.b1 > 0 or a.b2 > 0 or a.b3 > 0)
    order by user_login, allocation_date;
