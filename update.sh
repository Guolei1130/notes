#!/bin/bash
git pull

gitmessage="update"


if [ "#$" != 1 ]
then
	gitmessage=$1
fi

git add ./

git commit -m $gitmessage

git push
