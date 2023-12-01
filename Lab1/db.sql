create sequence specialities_id_seq;
create sequence vacancies_id_seq;

create table specialities (
                              id integer primary key default specialities_id_seq.nextval,
                              title character varying(32) not null
);

create table vacancies (
                           id integer primary key default vacancies_id_seq.nextval,
                           speciality_id integer references specialities(id),
                           company character varying(32),
                           position character varying(32),
                           higher_age_limit integer,
                           lower_age_limit integer,
                           salary integer
);