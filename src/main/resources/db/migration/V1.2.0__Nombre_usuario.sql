ALTER TABLE users ADD COLUMN name varchar(255) not null default 'user';

UPDATE users SET name = 'administrador' WHERE id = '2a8789bc-8088-46ec-baa0-34bd5a1d0936';