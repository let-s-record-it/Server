REPOSITORY=/home/ec2-user/server

cd $REPOSITORY

docker compose -f docker-compose.yml down --rmi all
docker compose -f docker-compose.yml --env-file /home/ec2-user/.env up -d
