FROM gitpod/workspace-mysql
RUN sudo apt-get -y update && sudo apt-get -y install libmysqlcppconn-dev
