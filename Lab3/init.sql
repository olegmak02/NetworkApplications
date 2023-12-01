CREATE TABLE IF NOT EXISTS public.users
(
    userid character varying COLLATE pg_catalog."default" NOT NULL,
    userpassword character varying COLLATE pg_catalog."default" NOT NULL,
    homedirectory character varying COLLATE pg_catalog."default" NOT NULL,
    enableflag boolean DEFAULT true,
    idletime integer NOT NULL DEFAULT 300,
    writepermission boolean DEFAULT false,
    maxloginnumber integer DEFAULT 2,
    maxloginperip integer DEFAULT 2,
    downloadrate integer DEFAULT 100000,
    uploadrate integer DEFAULT 100000,
    CONSTRAINT users_pkey PRIMARY KEY (userid)
    );

ALTER TABLE IF EXISTS public.users
    OWNER to postgres;