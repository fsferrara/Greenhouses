#! /bin/bash

if [ $# -ne 2 ]
then
	echo usage: $0 hello hello.c
else
	gcc -o $1 $2 -L/usr/local/lib/mysql/ -lmysqlclient -lz
fi

