FROM openjdk:11.0.9-jre
RUN groupadd -g 1003 minio && useradd -m -u 1003 -g minio minio
ADD ./build/libs/minio-util-1.0.0.BUILD-SNAPSHOT-all.jar /mu.jar
USER minio
ENTRYPOINT ["java", "-jar", "/mu.jar"]
CMD ["alias", "list"]
