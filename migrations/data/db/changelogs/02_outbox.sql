create table outbox(
    id uuid primary key,
    type text not null,
    topic text not null,
    payload jsonb not null,
    created_at timestamptz not null default now()
);