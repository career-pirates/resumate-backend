<div align="center">

# **ResuMate API 서버**

<!-- 로고 이미지 -->
<img width="400" alt="로고" src="https://github.com/user-attachments/assets/69c15409-42ba-44e9-ae05-6075f2c19878" />


<br/>
<br/>

    개인의 경험을 쉽게 기록·관리하는 프로젝트 회고 작성 서비스

</div>
<br>

## 프로젝트 개요

**프로젝트 목표 (MVP)**
- 취업 준비생이 일일 회고를 통해 자신의 경험을 기록·구조화할 수 있게 한다.
- 기록된 경험을 지속적으로 축적·관리함으로써, 자기소개서 작성의 기초 데이터를 확보하게 한다.

<br/>

## 주요 기능

- ✍️ 경험 입력 : 고정 질문 항목(4LS) 기반 입력으로 경험 기록 표준화
- 🗂️ 경험 관리 : 기록된 회고를 사용자 지정 폴더 단위로 체계적 관리
- 🧠 자기소개서 요소 추출 : LLM 활용 선택한 회고에서 자동으로 자기소개서 활용 요소 도출
- 🔐 OAuth2 소셜 로그인
- 📊 실시간 모니터링 : Prometheus + Grafana 기반 분석 처리 현황 모니터링
- ⏰ 웹푸시 알림
- ☁️ AWS 배포
  
  <br/>

## 시스템 구성도

<!-- 시스템 구성도 이미지 -->

<br/>
<br/>

# 기술 스택

- **Backend**: Java 17, Spring Boot, Spring Data JPA
- **Authentication** : OAuth2, JWT, Spring Security
- **Database & Cache**: PostgreSQL(Amazon Aurora), Redis
- **Monitoring**: Prometheus, Grafana, Micrometer
- **CI/CD**: Github Actions
- **Infra & Deployment**: AWS(ECS, ECR, RDS), Docker
- **External API**: OpenAI API

# 링크

### 프론트엔드

[resumate-frontend](https://github.com/career-pirates/resumate-frontend)

### Backend API Docs

`http://localhost:8080/swagger-ui/index.html`

### 모니터링

📊 **실시간 분석 모니터링**: [MONITORING.md](MONITORING.md)

- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)

# Backend 팀원

김영렬 [@EndlessMilkyway](https://github.com/EndlessMilkyway)

김태훈 [@Altair5869](https://github.com/Altair5869)

이우창 [@changi1122](https://github.com/changi1122)

