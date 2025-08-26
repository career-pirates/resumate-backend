FROM postgres:latest

# 한국어 로케일 설치 (타임존은 UTC 사용)
RUN apt-get update && \
    apt-get install -y locales tzdata && \
    echo "ko_KR.UTF-8 UTF-8" >> /etc/locale.gen && \
    locale-gen ko_KR.UTF-8 && \
    ln -sf /usr/share/zoneinfo/UTC /etc/localtime && \
    echo "UTC" > /etc/timezone && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# 로케일 및 타임존 환경변수 설정 (한국어 로케일 + UTC 시간)
ENV LANG=ko_KR.UTF-8
ENV LANGUAGE=ko_KR:ko
ENV LC_ALL=ko_KR.UTF-8
ENV TZ=UTC
