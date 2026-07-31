# HomeCam MQTT 연동

## 동작 흐름

1. 사용자가 웹에서 급식 시간과 목표 그릇 무게(g)를 저장한다.
2. Spring 서버가 `homecam/config`에 최신 설정을 retained 메시지로 발행한다.
3. Wemos는 NTP로 한국 시간을 맞춘 뒤 예약 시각에 로드셀을 읽는다.
4. `공급 요청량 = max(목표 무게 - 현재 잔량, 0)`으로 계산하고 목표 무게까지만 배식한다.
5. 급수는 현재 물이 `minWater`보다 적을 때 `maxWater`까지 채운다.
6. Wemos가 `homecam/events`로 결과를 발행한다.
7. 서버는 MQTT `eventId`로 중복을 제거하고 급식/급수 로그를 저장한 뒤 SSE 알림을 보낸다.

`amount`에는 사용자가 지정한 목표와 공급 전 잔량의 차이가 저장되고,
`leftovers`에는 공급 완료 후 측정값이 저장된다.

## 서버 환경변수

Azure 서버의 HomeCam `.env`에 다음 값을 추가한다.

```dotenv
MQTT_BROKER_URL=tcp://20.189.241.58:1883
MQTT_USERNAME=homecam-server
MQTT_PASSWORD=CHANGE_ME
MQTT_CLIENT_ID=homecam-server
```

`docker compose up -d --build` 또는 사용하는 배포 방식으로 서버를 다시 시작한다.
MySQL 테이블에는 `mqtt_event_id` 열이 자동 추가된다(`ddl-auto: update` 기준).

## Wemos 설정

`SM_cap_sensor/Sensor/wemos/include/secrets.h`에서 다음 값을 실제 환경에 맞게 바꾼다.

- Wi-Fi SSID와 비밀번호
- MQTT 사용자와 비밀번호
- 두 로드셀의 교정값

그 후 PlatformIO에서 `d1_mini` 환경으로 빌드/업로드한다.

로드셀의 영점은 Wemos가 켜질 때 잡힌다. 그릇 무게를 제외하고 내용물만 재도록
사료와 물을 비운 그릇은 로드셀 위에 올려둔 상태에서 전원을 켠다.

## Mosquitto ACL 예시

서버 계정:

```text
user homecam-server
topic write homecam/config
topic read homecam/events
topic read homecam/status
```

Wemos 계정:

```text
user homecam-device
topic read homecam/config
topic write homecam/events
topic write homecam/status
```

현재는 단일 Wemos만 연결하는 구조입니다. 여러 기기로 확장할 때는 기기 코드별 토픽과 ACL을 다시 적용해야 합니다.

## 웹 실시간 알림

로그인 JWT를 Authorization 헤더로 보내면서 다음 SSE 주소를 구독한다.

```text
GET /api/v1/notifications/stream
Accept: text/event-stream
Authorization: Bearer {accessToken}
```

이벤트 이름은 `feeding-completed`, `watering-completed`이고 본문에는
`amount`, `leftovers`, `occurredAt`이 포함된다.
