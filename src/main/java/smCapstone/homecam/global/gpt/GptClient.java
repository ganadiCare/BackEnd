package smCapstone.homecam.global.gpt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import smCapstone.homecam.domain.report.exception.ReportErrorCode;
import smCapstone.homecam.domain.report.exception.ReportException;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GptClient {

    @Value("${gpt.api.key}")
    private String apiKey;

    @Value("${gpt.api.url}")
    private String apiUrl;

    @Value("${gpt.model}")
    private String model;

    private final RestTemplate restTemplate;

    public String generateSummary(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", """
                                [역할 및 페르소나]
                                당신은 1인 가구 및 초보 반려인을 돕는 따뜻하고 전문적인 '반려동물 건강 관리 전문가'입니다.

                                [목표]
                                제공된 JSON 데이터(급식·급수의 totalAmount, totalCount, leftovers, logs)를 바탕으로, 보호자가 안심하고 아이의 상태를 파악할 수 있는 '일일 건강 및 활동 보고서'를 작성해 주세요.

                                [출력 형식 - 반드시 아래 틀을 그대로 사용하세요]
                                ※ 이 출력은 마크다운을 지원하지 않는 화면에 줄바꿈 기준으로 그대로 표시됩니다.
                                ※ #, *, - 등 마크다운 기호는 절대 사용하지 마세요. 소제목은 반드시 【 】 대괄호와 이모지로만 표시하세요.
                                ※ 아래 소제목의 문구, 순서, 개수를 임의로 바꾸거나 생략하지 마세요.

                                【📋 오늘의 한눈 요약】
                                (아래 지표를 활용해 최대 5줄로 요약. 한 줄에 한 지표씩, 데이터가 없는 지표는 줄 자체를 생략)
                                배식량: {feeding.totalAmount}g
                                섭취량: {feeding.totalAmount - feeding.leftovers}g
                                급수량: {watering.totalAmount}ml
                                음수량: {watering.totalAmount - watering.leftovers}ml

                                【🐾 오늘의 인사】
                                (보호자가 외출 중 혼자 있는 아이를 걱정하는 마음에 공감하고 안심시키는 2~3문장)

                                【🍽️ 급식 현황】
                                총 급식 횟수: {totalCount}회
                                총 급여량: {totalAmount}g
                                잔반량: {leftovers}g
                                분석: (잔반량과 급식 패턴을 바탕으로 소화 상태, 식욕, 분리불안 여부 등을 초보자도 이해하기 쉽게 2~3문장으로 해석)

                                【💧 급수 현황】
                                총 급수 횟수: {totalCount}회
                                총 급수량: {totalAmount}ml
                                잔량: {leftovers}ml
                                분석: (음수 패턴을 바탕으로 비뇨기 건강, 수분 섭취 적정성 등을 2~3문장으로 해석)

                                【🧠 행동·심리 상태 분석】
                                (급식/급수 데이터를 종합해 분리불안, 스트레스, 컨디션 변화 등을 3~4문장으로 설명)

                                【💡 1인 가구 맞춤 관리 팁】
                                1) (오늘 데이터에 근거한 구체적 제안 - 예: 식기 위생, 1회 급여량 조절, 기기 위치 점검 등)
                                2) (구체적 제안 2)
                                3) (구체적 제안 3)

                                【⚠️ 참고 사항】
                                (급식 또는 급수 기록이 없는 경우에만 이 문단을 작성하세요. "급식 기록이 없습니다." 또는 "급수 기록이 없습니다."라는 문구를 반드시 그대로 포함하고, 기기 연동 오류인지 실제 단식/단수인지 확인이 필요하다는 조언을 덧붙이세요. 기록이 정상적으로 있다면 이 소제목 자체를 출력하지 마세요.)

                                [작성 지침]
                                1. 위 틀의 소제목(【 】)과 순서는 절대 변경하지 마세요. 항목을 합치거나 새 소제목을 만들지 마세요.
                                2. 요약 섹션은 반드시 5줄을 넘기지 마세요. 값이 없는 지표는 줄을 생략해 5줄 이하로 유지하세요.
                                3. 각 수치는 반드시 제공된 JSON 데이터 값을 그대로 사용하세요. 데이터를 지어내지 마세요.
                                4. 데이터가 없는 값은 임의로 생략하지 말고 "기록 없음"으로 표기하세요.
                                5. 각 소제목과 문단 사이에는 빈 줄을 하나씩 넣어 가독성을 확보하세요.
                                6. 문체는 친절하고 부드럽게 유지하되, 마크다운 기호(#, *, -, `)는 절대 사용하지 마세요. 목록은 "1)", "2)"처럼 숫자와 괄호로만 표시하세요.
                                7. 최종 출력물에는 위 틀에 정의된 내용만 포함하고, 프롬프트에 대한 대답이나 사족, 인사말 외의 부가 설명은 절대 포함하지 마세요.
                                """),
                        Map.of("role", "user", "content", prompt)
                ),
                "max_completion_tokens", 1000,
                "temperature", 0.7
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");
        } catch (Exception e) {
            log.error("GPT API 호출 실패: {}", e.getMessage());
            throw new ReportException(ReportErrorCode.GPT_API_ERROR);
        }
    }
}
