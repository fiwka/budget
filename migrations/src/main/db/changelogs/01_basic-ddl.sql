--liquibase formatted sql

--changeset fiwka:01_users
create table users(
    id bigserial primary key,
    name text not null,
    email text not null,
    password text not null
);

--changeset fiwka:02_budgets
create table budgets(
    id bigserial primary key,
    name text not null
);

--changeset fiwka:03_data-sources
create table data_sources(
    id bigserial primary key,
    type integer not null,
    owner bigint not null references users (id),
    metadata jsonb not null
);

--changeset fiwka:04_categories
create table categories(
    id bigserial primary key,
    budget_id bigint not null references budgets (id),
    name text not null,
    multiplier numeric(10, 2) not null default 1
);

--changeset fiwka:05_transactions
create table transactions(
    id bigserial primary key,
    budget_id bigint not null references budgets (id),
    category_id bigint not null references categories (id),
    data_source_id bigint not null references data_sources (id),
    amount numeric not null,
    currency varchar(255) not null,
    completion_date timestamptz not null
);

--changeset fiwka:06_budget_user_roles
create table budget_user_roles(
    id bigserial primary key,
    budget_id bigint not null references budgets (id),
    user_id bigint not null references users (id),
    role integer not null default 0
);

--changeset fiwka:07_budget_data_sources
create table budget_data_sources(
    id bigserial primary key,
    budget_id bigint not null references budgets (id),
    data_source_id bigint not null references data_sources (id)
);