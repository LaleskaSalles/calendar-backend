create table users(
                      id int AUTO_INCREMENT primary key,
                      login varchar(100) unique not null,
                      password varchar(255) not null
);

create table events(
                       id int AUTO_INCREMENT primary KEY ,
                       title varchar(50) not null,
                       start datetime not null,
                       end datetime not null,
                       user_id integer not null,
                       foreign key (user_id) references users(id)
);