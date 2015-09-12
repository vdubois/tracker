#!/bin/sh
mvn3 clean install -Pprod,fast -DskipTests
heroku login
heroku create --app vdubois-tracker-app
heroku addons:create heroku-postgresql --app vdubois-tracker-app
heroku config --app vdubois-tracker-app
heroku deploy:jar --jar target/*.war --app vdubois-tracker-app
