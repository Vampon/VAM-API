# Docker 镜像构建

# 使用 Maven 3.9.6 和 JDK 17 的官方镜像作为构建阶段的基础镜像
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Copy local code to the container image.
# 设置工作目录
WORKDIR /app
# 复制 Maven 配置文件和项目源码到容器中
COPY pom.xml .
COPY src ./src

# Build a release artifact.
# 构建项目并跳过测试
RUN mvn package -DskipTests

# Run the web service on container startup.
CMD ["java","-jar","/app/target/vamapi-backend-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]