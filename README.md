# `Silhouette REST Slick Seed` [![Build Status](https://travis-ci.org/adamzareba/play-silhouette-rest-slick.svg)](https://travis-ci.org/adamzareba/play-silhouette-rest-slick) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/6e8dae3c1bf346428db76e782ae68c41)](https://www.codacy.com/app/adamzareba/play-silhouette-rest-slick?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=adamzareba/play-silhouette-rest-slick&amp;utm_campaign=Badge_Grade)

Example project for Play Framework that uses [Silhouette](https://github.com/mohiva/play-silhouette) for authentication and authorization, exposed REST API for sign-up, sign-in.

## Basic usage

### Sign-up

```bash
curl -X POST http://localhost:9000/api/auth/signup  -H 'Content-Type: application/json' -d '{"identifier": "adam.zareba", "password": "this!Password!Is!Very!Very!Strong!", "email": "adam.zareba@test.pl", "firstName": "Adam", "lastName": "Zaręba"}' -v
```

```
< HTTP/1.1 200 OK
< Content-Type: application/json; charset=utf-8
< X-Auth-Token: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...

{
  "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...",
  "expiresOn": "2017-10-06T07:49:27.238+02:00"
}
```

### Sign-in

_Not necessary just after the sign-up because you already have a valid token._

```bash
curl -X POST http://localhost:9000/api/auth/signin/credentials -H 'Content-Type: application/json' -d '{"identifier": "adam.zareba", "password": "this!Password!Is!Very!Very!Strong!"}' -v
```

```
< HTTP/1.1 200 OK
< Content-Type: application/json; charset=utf-8
< X-Auth-Token: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...

{
  "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...",
  "expiresOn": "2017-10-06T07:49:27.238+02:00"
}
```

### Secured Action with autorization

_CRUD Operation on databases_

```bash
curl http://localhost:9000/api/databases -H 'X-Auth-Token:eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...' -v
```

```
< HTTP/1.1 200 OK
< Content-Type: application/json; charset=utf-8

[{"id": "1", "name": "test", "engine": "mysql", "status": "available}]
```

_The token must belong to a user with Admin role_

```bash
curl http://localhost:9000/badPassword -H 'X-Auth-Token:eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...' -v
```

```
< HTTP/1.1 200 OK
< Content-Type: application/json; charset=utf-8

{"result":"qwerty1234"}
```
## Built-in users

| username    | password        |
| ----------- |:---------------:|
| test1       | test1Password   |
| test2       | test2Password   |

## Database reload

Slick evolutions are responsible for data reloading.

## API documentation

Documentation is available under address: [REST API](http://localhost:9000/docs)

## Install deprecated scala things
```
apt-get install software-properties-common
apt-add-repository 'deb http://security.debian.org/debian-security stretch/updates main'
apt-get update
apt-get install openjdk-8-jdk
apt-get install git
curl -L -o sbt.deb http://dl.bintray.com/sbt/debian/sbt-1.0.3.deb
dpkg -i sbt.deb
apt-get update
apt-get install sbt
apt-get install bc
git clone https://github.com/abteilung6/scala_play_silhouette_rest.git
```

## Install MySQL Server
```
apt install gnupg
wget https://dev.mysql.com/get/mysql-apt-config_0.8.13-1_all.deb
# Select MySQL Server & Cluster 8.0
apt update
apt install mysql-server
# Use Strong Password Encryption
systemctl status mysql
mysql_secure_installation
# remove anonymous user
# disallow remote root login
# drop test database
# reload privilege tables
mysql -u root -p
GRANT ALL PRIVILEGES ON *.* TO 'username'@'localhost' IDENTIFIED BY 'password';
# change username and password
CREATE DATABASE 'auth';
GRANT ALL PRIVILEGES ON auth . * TO 'username'@'localhost';
FLUSH PRIVILEGES;
```

## Configure settings in conf/application.conf
```
play.filters.hosts {
  # Allow requests to example.com, its subdomains, and localhost:9000.
  allowed = ["localhost:9000"]
}
```

## Production
```
cd scala_play_silhouette_rest.git
sbt dist
unzip target/universal/play-silhouette-rest-slick-1.0.zip
play-silhouette-rest-slick-1.0/bin/play-silhouette-rest-slick -Dplay.http.secret.key=CHANGEME
# CHANGEME needs 32 characters
```

# License

The code is licensed under [Apache License v2.0](http://www.apache.org/licenses/LICENSE-2.0). 

