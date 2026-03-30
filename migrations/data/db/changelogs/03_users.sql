--liquibase formatted sql
--changeset fiwka:03-users

create table roles(
    id uuid primary key,
    key int not null,
    can_view bool not null,
    can_edit bool not null,
    can_manage bool not null
);

insert into roles (key, can_view, can_edit, can_manage)
values (0, true, false, false),
       (1, true, true, false),
       (2, true, true, true),
       (3, true, true, true);

create table budget_roles(
    id uuid primary key,
    budget_id uuid not null references budget (id),
    user_id text not null,
    role_id uuid not null references roles (id)
);