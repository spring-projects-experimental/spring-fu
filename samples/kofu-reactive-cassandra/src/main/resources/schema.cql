CREATE KEYSPACE SpringKofuReactiveCassandraExamples WITH REPLICATION = {'class':'SimpleStrategy', 'replication_factor':1};
CREATE TABLE SpringKofuReactiveCassandraExamples.Users (login VARCHAR PRIMARY KEY, firstname VARCHAR, lastname VARCHAR);
USE SpringKofuReactiveCassandraExamples;
