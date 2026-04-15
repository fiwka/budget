--liquibase formatted sql
--changeset fiwka:03-users

create extension if not exists "uuid-ossp";

create table users(
    id uuid primary key,
    username text not null unique,
    email text not null unique,
    password_hash text not null
);

create table roles(
    id uuid primary key,
    key int not null unique,
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
    user_id uuid not null references users (id),
    role_id uuid not null references roles (id)
);