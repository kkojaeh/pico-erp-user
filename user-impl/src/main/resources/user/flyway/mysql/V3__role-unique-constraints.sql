alter table usr_group_role
	add constraint USR_GROUP_ROLE_UC unique (group_id,role_id);

alter table usr_user_role
	add constraint USR_USER_ROLE_UC unique (user_id,role_id);
