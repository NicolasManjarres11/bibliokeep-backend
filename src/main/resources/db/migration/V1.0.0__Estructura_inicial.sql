CREATE TABLE users
(
    id uuid NOT NULL,
    annual_goal integer NOT NULL,
    email varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT idx_email UNIQUE (email),
    CONSTRAINT idx_user_email UNIQUE (email)
);

CREATE TABLE books
(
    id bigserial,
    description text,
    is_lent boolean NOT NULL,
    isbn varchar(13)NOT NULL,
    owner_id uuid NOT NULL,
    rating integer,
    status varchar(255) NOT NULL,
    thumbnail text,
    title varchar(255) NOT NULL,
    CONSTRAINT books_pkey PRIMARY KEY (id),
    CONSTRAINT idx_book_isbn UNIQUE (isbn),
    CONSTRAINT idx_isbn UNIQUE (isbn),
    CONSTRAINT books_owner_fk FOREIGN KEY (owner_id)
        REFERENCES users (id),
    CONSTRAINT books_status_check CHECK (status::text = ANY (ARRAY['DESEADO'::character varying, 'COMPRADO'::character varying, 'LEYENDO'::character varying, 'LEIDO'::character varying, 'ABANDONADO'::character varying]::text[]))
);

CREATE TABLE book_authors
(
    book_id bigint NOT NULL,
    author varchar(255),
    CONSTRAINT book_authors_book_fk FOREIGN KEY (book_id)
        REFERENCES books (id)

);

CREATE TABLE loans
(
    id bigserial,
    book_id bigint NOT NULL,
    contact_name varchar(255) NOT NULL,
    due_date date NOT NULL,
    loan_date date NOT NULL,
    returned boolean NOT NULL,
    CONSTRAINT loans_pkey PRIMARY KEY (id),
    CONSTRAINT loans_book_fk FOREIGN KEY (book_id)
        REFERENCES books (id)
);


CREATE TABLE role
(
    id bigserial,
    name varchar(100) NOT NULL,
    CONSTRAINT roles_pkey PRIMARY KEY (id),
    CONSTRAINT roles_name_uk UNIQUE (name)
);



CREATE TABLE user_preferences
(
    user_id uuid NOT NULL,
    preference varchar(255),
    CONSTRAINT user_preferences_user_fk FOREIGN KEY (user_id)
        REFERENCES users (id)
);

CREATE TABLE user_roles
(
    user_id uuid NOT NULL,
    role_id bigint NOT NULL,
    CONSTRAINT user_roles_pkey PRIMARY KEY (user_id, role_id),
    CONSTRAINT user_roles_user_fk FOREIGN KEY (user_id)
        REFERENCES users (id),
    CONSTRAINT user_roles_role_fk FOREIGN KEY (role_id)
        REFERENCES role (id)
);

