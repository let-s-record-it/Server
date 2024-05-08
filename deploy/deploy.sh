REPOSITORY=/home/ec2-user/

cd $REPOSITORY

sudo curl -L https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
docker-compose -f docker-compose.yml down --rmi all
docker-compose -f docker-compose.yml up -d
