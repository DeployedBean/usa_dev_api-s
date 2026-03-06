-- Create sequences
CREATE SEQUENCE IF NOT EXISTS login_loginid_seq;
CREATE SEQUENCE IF NOT EXISTS signup_id_seq;
CREATE SEQUENCE IF NOT EXISTS users_userid_seq;

-- Login table
CREATE TABLE IF NOT EXISTS public.login
(
    loginid integer NOT NULL DEFAULT nextval('login_loginid_seq'::regclass),
    username character varying COLLATE pg_catalog."default",
    userpassword character varying COLLATE pg_catalog."default",
    CONSTRAINT login_pkey PRIMARY KEY (loginid)
);

-- Signup table
CREATE TABLE IF NOT EXISTS public.signup
(
    id integer NOT NULL DEFAULT nextval('signup_id_seq'::regclass),
    username character varying COLLATE pg_catalog."default",
    userpassword character varying COLLATE pg_catalog."default",
    confirmpassword character varying COLLATE pg_catalog."default",
    CONSTRAINT signup_pkey PRIMARY KEY (id)
);

-- Users table
CREATE TABLE IF NOT EXISTS public.users
(
    userid integer NOT NULL DEFAULT nextval('users_userid_seq'::regclass),
    email character varying COLLATE pg_catalog."default",
    fullname character varying COLLATE pg_catalog."default",
    userpassword character varying COLLATE pg_catalog."default",
    nwpassword character varying COLLATE pg_catalog."default",
    securitytoken character varying COLLATE pg_catalog."default",
    CONSTRAINT users_pkey PRIMARY KEY (userid)
);