# 基础镜像
FROM ubuntu:20.04
RUN apt-get update && apt-get install -y net-tools vim git curl wget iputils-ping

# 设置时区
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo "Asia/Shanghai" > /etc/timezone
# 安装 JDK 17
RUN apt-get update && \
    apt-get install -y openjdk-17-jdk && \
    apt-get clean

# 拷贝jar包
COPY target/git-doc-synchronizer-0.1.0.jar /app/git-doc-synchronizer.jar
# 容器工作目录
WORKDIR /app
# 设置容器启动命令并调用接口
CMD nohup java -jar git-doc-synchronizer.jar  > /var/log/sofadoc.log
