INSERT INTO users(
	id, annual_goal, email, password)
	VALUES ( '2a8789bc-8088-46ec-baa0-34bd5a1d0936', 12, 'manja@example.com', '$2a$12$fz7SpRYAZ25/FMjiVU9Sa.xx0RVO/Sof6mr4Gf6ZXkAJr1lmOPGw6');

INSERT INTO user_roles(
	user_id, role_id)
	VALUES ('2a8789bc-8088-46ec-baa0-34bd5a1d0936', 1);

INSERT INTO user_preferences(
	user_id, preference)
	VALUES ('2a8789bc-8088-46ec-baa0-34bd5a1d0936', 'Theme=Dark');
INSERT INTO user_preferences(
	user_id, preference)
	VALUES ('2a8789bc-8088-46ec-baa0-34bd5a1d0936', 'Language=es');