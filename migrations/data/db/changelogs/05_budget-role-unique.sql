--liquibase formatted sql
--changeset fiwka:05-budget-role-unique

alter table budget_roles
    add constraint uq_budget_roles_budget_user unique (budget_id, user_id);
