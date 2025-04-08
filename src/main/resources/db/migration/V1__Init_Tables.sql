create table tasks
(
id bigint not null auto_increment,
date date,
title varchar(255),
description varchar(2048),
status enum ('ACTIVE','CANCELED','DONE'),
user_id bigint not null,
primary key (id)
);

create table user_role
(
user_id bigint not null,
roles enum ('ADMIN','USER')
);

create table users
(
id bigint not null auto_increment,
first_name varchar(255),
password varchar(255),
username varchar(255),
primary key (id)
);

alter table tasks
add constraint tasks_user_fk foreign key (user_id) references users (id);
alter table user_role
add constraint user_role_user_fk foreign key (user_id) references users (id);