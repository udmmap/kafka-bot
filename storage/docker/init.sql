create table clinic.public.doctor (
                                      id bigint primary key
    , name varchar
);
create table clinic.public.schedule (
                                        id bigint primary key
    , time timestamp
    , doctor_id bigint references doctor (id)
    , unique  (doctor_id, time)
);
create table clinic.public.journal (
                                       id bigint primary key
    , user_id int
    , schedule_id bigint references schedule (id) unique
);

insert into doctor
    select d.n as id , 'доктор ' || d.n::varchar as name from generate_series(1,10) as d(n);

insert into schedule (id, doctor_id, time)
select
        row_number() OVER () as id
       , d.doctor_id
       , s.day + interval '1 hours' * h.hours as time
from
     generate_series(1,10) as d(doctor_id)
    , generate_series(8,16) as h(hours)
    , generate_series('2021-12-01 00:00'::timestamp,
                      '2021-12-05 00:00'::timestamp, '1 day') as s(day)
;