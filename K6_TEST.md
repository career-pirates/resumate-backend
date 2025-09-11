# K6 (Docker Compose)

이 프로젝트는 **Spring Boot 애플리케이션**과 **k6 부하 테스트 도구**를 `docker-compose`로 구성한 예제입니다.  
Spring Boot 애플리케이션은 독립적으로 실행되고, k6는 필요할 때마다 수동으로 실행합니다.  

## k6 부하 테스트 실행
테스트 스크립트(k6/test.js)를 실행하려면 아래 명령어를 사용합니다:

``` bash
docker compose -f docker-compose.local.yaml run --rm k6 run test.js
```
- --rm: 실행 후 컨테이너 자동 삭제
- run test.js: ./k6/test.js 파일을 실행

예시 실행 결과:
``` bash
running (30s), 10/10 VUs, 300 complete and 0 interrupted iterations
default ✓ [======================================] 10 VUs  30s
```
