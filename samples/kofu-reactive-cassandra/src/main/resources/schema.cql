CREATE KEYSPACE Kofu WITH REPLICATION = {'class':'SimpleStrategy', 'replication_factor':1};
CREATE TABLE Kofu.Users (login VARCHAR PRIMARY KEY, firstname VARCHAR, lastname VARCHAR);
USE Kofu;
