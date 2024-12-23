1. 프로젝트 클론
   먼저, GitHub 저장소에서 프로젝트를 클론합니다.

```
git clone https://github.com/mhje11/kokoatalk-server.git
```

2. local 브랜치로 전환

```
git checkout local
```

3. Docker 컨테이너 실행
   인텔리제이 터미널(루트 프로젝트 경로에서) docker-compose.yml 파일을 사용하여 MySQL 및 Redis 컨테이너를 실행합니다.

```
docker-compose up -d
```

4. 프로젝트 빌드

```
./gradlew build
```

5. 애플리케이션 실행

- Jar 파일로 실행

```
java -jar build/libs/kokoatalk-server-0.0.1-SNAPSHOT.jar
```

- IntelliJ IDEA를 통해 실행

  IntelliJ IDEA에서 프로젝트를 열고, KokoatalkServerApplication 클래스를 실행합니다.
  해당 클래스는 src/main/java/org/kokoatalkserver/KokoatalkServerApplication.java 경로에 위치합니다.
    