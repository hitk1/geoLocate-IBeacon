create table Usuarios
(
	ID int not null primary key auto_increment,
    Prontuario varchar(10) not null unique,
    Passwd varchar(300) not null, 
    Tk_Grant varchar(300)
);

create table Tokens 
(
	ID int not null primary key auto_increment,
    Prontuario varchar(10) not null, 
    Tk_Autorizacao varchar(300) not null,
    Tk_Tempo datetime not null,
    
    foreign key (Prontuario) references Usuarios(Prontuario)
);

create table Registros
(
	ID int not null primary key auto_increment,
    Prontuario varchar(10) not null,
    ID_Beacon varchar(25) not null,
    Beacon_Name varchar(30) not null,
    Rssi int not null,
    Dbm int not null,
    Distancia decimal(10, 2) not null,
    Bateria int not null,
    Delay int not null,
    DataHora datetime not null,
    
    foreign key (Prontuario) references Tokens(Prontuario)
);

