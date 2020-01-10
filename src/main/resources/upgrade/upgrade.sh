sed -i 's/\r$//' upgrade.sh
curl -o /opt/program/docker/docker_clinet_conf/auth_file.sh http://192.168.236.194:8082/ws/download/auth_file.sh
curl -o /opt/program/docker/docker_clinet_conf/rollback.sh http://192.168.236.194:8082/ws/download/rollback.sh
