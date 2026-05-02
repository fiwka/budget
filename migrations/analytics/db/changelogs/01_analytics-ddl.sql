--liquibase formatted sql
--changeset fiwka:01-analytics-ddl

create table analytics_transaction_snapshots (
    transaction_id uuid primary key,
    budget_id uuid not null,
    category_id uuid not null,
    completed_date timestamptz not null,
    amount decimal(12, 2) not null,
    is_consumption boolean not null,
    updated_at timestamptz not null default now()
);

create index idx_analytics_transaction_snapshots_budget_id
    on analytics_transaction_snapshots (budget_id);

create index idx_analytics_transaction_snapshots_completed_date
    on analytics_transaction_snapshots (completed_date);

create table analytics_processed_events (
    event_id text primary key,
    processed_at timestamptz not null default now()
);
