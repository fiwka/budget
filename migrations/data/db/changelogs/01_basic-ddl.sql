--liquibase formatted sql
--changeset fiwka:01-basic-ddl

create table budgets(
    id uuid primary key,
    name text not null,
    description text not null
);

create table categories(
    id uuid primary key,
    budget_id uuid not null references budgets (id),
    name text not null,
    is_consumption boolean not null
);

create table transactions(
    id uuid primary key,
    category_id uuid not null references categories (id),
    completed_date timestamptz not null,
    amount decimal(12, 2) not null,
    appendix jsonb,
    version int not null default 0,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);