#!/bin/sh

# Set safe shell script
set -xu

# Clear current context
docker volume prune -f;
docker-compose down;

# External Dependencies
docker-compose run -d -p 27017:27017 mongo;
