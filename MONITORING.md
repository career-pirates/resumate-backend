# 📊 ResuMate 모니터링 가이드

## 🚀 빠른 시작

### 1. 모니터링 스택 실행

스프링 애플리케이션 시작 시 자동으로 실행

### 3. 서비스 접속
| 서비스 | URL                   | 기본 계정 |
|--------|-----------------------|-----------|
| **Prometheus** | http://localhost:9090 | - |
| **Grafana** | http://localhost:3000 | admin/admin |
| **Spring Boot** | http://localhost:8080 | - |
| **Redis Insight** | http://localhost:5540 | - |

---

## 📈 메트릭 정보

### 🎯 분석 관련 커스텀 메트릭
| 메트릭명 | 설명 | 타입 |
|----------|------|------|
| `analysis_pending_count` | 현재 진행 중인 분석 수 | Gauge |
| `analysis_total_count{status="success"}` | 성공한 분석 총 개수 | Gauge |
| `analysis_total_count{status="error"}` | 실패한 분석 총 개수 | Gauge |
| `analysis_total_count{status="idle"}` | 대기 중인 분석 총 개수 | Gauge |

### 📊 주요 시스템 메트릭
- **JVM 메모리**: `jvm_memory_used_bytes`, `jvm_memory_max_bytes`
- **HTTP 요청**: `http_server_requests_seconds_count`
- **애플리케이션 상태**: `up{job="resumate-backend"}`

---

## 🎛️ Grafana 대시보드

### 메인 대시보드: "ResuMate 분석 메트릭 모니터링"

**주요 패널:**
1. **진행 중인 분석 수** - 실시간 진행 중인 분석 개수
2. **분석 상태별 총 개수** - 성공/실패/대기 상태 파이차트
3. **분석 완료율** - 성공률을 백분율로 표시
4. **시간별 분석 처리 현황** - 시간대별 분석 처리 추이
5. **시간당 완료된 분석 수** - 처리량 모니터링
6. **JVM (Micrometer)** - 기존에 존재하던 대시보드

---

## 🧪 테스트 방법

### 1. 메트릭 상태 확인
```bash
# 현재 메트릭 상태 조회
curl http://localhost:8080/api/analysis/metrics/status
```

### 2. 테스트용 메트릭 조작
```bash
# 분석 시작 시뮬레이션 (pending_count +1)
curl -X POST http://localhost:8080/api/analysis/metrics/test/start

# 분석 완료 시뮬레이션 (pending_count -1)
curl -X POST http://localhost:8080/api/analysis/metrics/test/complete

# 분석 실패 시뮬레이션 (pending_count -1)
curl -X POST http://localhost:8080/api/analysis/metrics/test/error
```

### 3. 메트릭 동기화
```bash
# DB와 메모리 상태 동기화
curl -X POST http://localhost:8080/api/analysis/metrics/sync
```

---

## 🔍 Prometheus 쿼리 예시

### 기본 메트릭 조회
```promql
# 현재 진행 중인 분석 수
analysis_pending_count

# 성공한 분석 수
analysis_total_count{status="success"}

# 분석 성공률 (%)
100 * analysis_total_count{status="success"} / (analysis_total_count{status="success"} + analysis_total_count{status="error"})
```

### 고급 쿼리
```promql
# 시간당 완료된 분석 수
rate(analysis_total_count{status="success"}[1h]) * 3600

# 5분간 평균 진행 중인 분석 수
avg_over_time(analysis_pending_count[5m])

# 애플리케이션 다운타임 감지
up{job="resumate-backend"} == 0
```

---

## ⚙️ 설정 커스터마이징

### Prometheus 설정 수정
```yaml
# monitoring/prometheus/prometheus.yml
scrape_configs:
  - job_name: 'resumate-backend'
    scrape_interval: 5s  # 수집 간격 조정
    static_configs:
      - targets: ['host.docker.internal:8080']
```

### Grafana 환경변수
```bash
# .env 파일에서 설정
GRAFANA_USER=your_username
GRAFANA_PASSWORD=your_secure_password
GRAFANA_PORT=3001  # 포트 변경
```

---

## 🚨 알람 설정 (고급)

### 1. Prometheus Alerting Rules 생성
```yaml
# monitoring/prometheus/rules/analysis_alerts.yml
groups:
  - name: analysis_alerts
    rules:
    - alert: TooManyPendingAnalyses
      expr: analysis_pending_count > 10
      for: 2m
      labels:
        severity: warning
      annotations:
        summary: "진행 중인 분석이 너무 많습니다"
        description: "현재 {{ $value }}개의 분석이 진행 중입니다."
```

### 2. Grafana 알림 채널 설정
- Slack, Email, Discord 등 다양한 채널 지원
- Grafana UI에서 설정 가능

---

## 📝 주의사항

1. **프로덕션 환경에서는** 테스트용 API 엔드포인트 제거 권장:
   - `/api/analysis/metrics/test/*` 엔드포인트들

2. **보안 고려사항**:
   - Grafana 기본 비밀번호 변경 필수
   - 방화벽에서 모니터링 포트 접근 제한

3. **성능 고려사항**:
   - Prometheus 스크래핑 간격 조정
   - 메트릭 보존 기간 설정

4. **데이터 백업**:
   - Docker 볼륨 백업 필수
   - 대시보드 설정 백업

---

## 🛠️ 트러블슈팅

### 자주 발생하는 문제

#### 1. Prometheus가 Spring Boot 메트릭을 수집하지 못할 때
```bash
# Spring Boot 애플리케이션이 실행 중인지 확인
curl http://localhost:8080/actuator/health

# Prometheus 타겟 상태 확인
# http://localhost:9090/targets
```

#### 2. Grafana 대시보드가 로드되지 않을 때
```bash
# Grafana 로그 확인
docker logs resumate-grafana

# 프로비저닝 파일 권한 확인
ls -la monitoring/grafana/
```

#### 3. 메트릭 값이 부정확할 때
```bash
# 메트릭 동기화 실행
curl -X POST http://localhost:8080/api/analysis/metrics/sync
```

### 로그 확인
```bash
# 모든 서비스 로그 확인
docker-compose -f docker-compose.local.yaml logs

# 특정 서비스 로그 확인
docker-compose -f docker-compose.local.yaml logs prometheus
docker-compose -f docker-compose.local.yaml logs grafana
```

---

## 📚 참고 자료

- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Micrometer Metrics](https://micrometer.io/docs/)
