# 使用合適的基底映像，這裡假設使用 OpenJDK 11
FROM openjdk:11-jre-slim

# 設定工作目錄
WORKDIR /app

# 複製打包好的 Spring Boot JAR 文件到容器內的 /app 目錄
COPY target/demo-0.0.1-SNAPSHOT.jar /app/demo.jar

# 複製本地的 data 資料夾到容器內的 /app/data 資料夾
COPY data /app/data

# 開放容器內部的 8081 埠
EXPOSE 8081

# 運行 Spring Boot 應用
ENTRYPOINT ["java", "-jar", "demo.jar"]
