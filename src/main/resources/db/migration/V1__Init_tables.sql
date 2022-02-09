create table "accounts"."user"
(
    id       bigserial primary key,
    email    varchar(50) not null unique,
    username varchar(50) not null unique,
    password varchar(255) not null,
    role     varchar(15) not null,
    active   boolean     not null
);

create table "accounts"."slot"
(
    id      bigserial primary key,
    state   bigint  not null,
    user_id bigint references "user" (id),
    active  boolean not null
);