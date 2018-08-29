create table usr_department (
	id varchar(50) not null,
	created_by_id varchar(50),
	created_by_name varchar(50),
	created_date datetime,
	last_modified_by_id varchar(50),
	last_modified_by_name varchar(50),
	last_modified_date datetime,
	name varchar(50),
	manager_id varchar(50),
	primary key (id)
) engine=InnoDB;

create table usr_group (
	id varchar(50) not null,
	created_by_id varchar(50),
	created_by_name varchar(50),
	created_date datetime,
	last_modified_by_id varchar(50),
	last_modified_by_name varchar(50),
	last_modified_date datetime,
	name varchar(30),
	primary key (id)
) engine=InnoDB;

create table usr_group_role (
	group_id varchar(50) not null,
	role_id varchar(50)
) engine=InnoDB;

create table usr_user (
	id varchar(50) not null,
	account_non_expired bit not null,
	account_non_locked bit not null,
	created_by_id varchar(50),
	created_by_name varchar(50),
	created_date datetime,
	credentials_non_expired bit not null,
	email varchar(60),
	enabled bit not null,
	last_modified_by_id varchar(50),
	last_modified_by_name varchar(50),
	last_modified_date datetime,
	mobile_phone_number varchar(40),
	name varchar(50),
	password varchar(100),
	position varchar(20),
	department_id varchar(50),
	primary key (id)
) engine=InnoDB;

create table usr_user_group (
	user_id varchar(50) not null,
	group_id varchar(50) not null,
	primary key (user_id,group_id)
) engine=InnoDB;

create table usr_user_role (
	user_id varchar(50) not null,
	role_id varchar(50)
) engine=InnoDB;

alter table usr_user
	add constraint USR_USER_EMAIL_IDX unique (email);

alter table usr_department
	add constraint FKaaxkuoxm4nli9wl1n0h58a1ao foreign key (manager_id)
	references usr_user (id);

alter table usr_group_role
	add constraint FKkfigodg9cddatb5swfpyygcrg foreign key (group_id)
	references usr_group (id);

alter table usr_user
	add constraint FKffx5ipbymmvgn63y6s9j1k7g5 foreign key (department_id)
	references usr_department (id);

alter table usr_user_group
	add constraint FK35ekeqb09qbv3sgn3hi57ryjy foreign key (group_id)
	references usr_group (id);

alter table usr_user_group
	add constraint FKsa35ytowhhsb82trnu65cdkbl foreign key (user_id)
	references usr_user (id);

alter table usr_user_role
	add constraint FKg94o7wnk9i0u1hm6ga2ev1vtj foreign key (user_id)
	references usr_user (id);
