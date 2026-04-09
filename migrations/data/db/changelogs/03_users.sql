--liquibase formatted sql
--changeset fiwka:03-users

create extension if not exists "uuid-ossp";

create table roles(
    id uuid primary key,
    key int not null,
    can_view bool not null,
    can_edit bool not null,
    can_manage bool not null
);

insert into roles (id, key, can_view, can_edit, can_manage)
values (uuidv4(), 0, true, false, false),
       (uuidv4(), 1, true, true, false),
       (uuidv4(), 2, true, true, true),
       (uuidv4(), 3, true, true, true);

create table budget_roles(
    id uuid primary key,
    budget_id uuid not null references budgets (id),
    user_id text not null,
    role_id uuid not null references roles (id)
);