import http from "k6/http";
import { check, sleep } from "k6";

export let options = {
    vus: 10, // 동시 사용자 수
    duration: "10s", // 테스트 시간
};

export default function () {
    let res = http.get("http://host.docker.internal:8080/actuator/health");
    check(res, { "status was 200": (r) => r.status === 200 });
    sleep(1);
}
