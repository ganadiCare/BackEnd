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
                                제공된 JSON 데이터(급식, 급수 로그 및 잔반량 등)를 바탕으로, 보호자가 안심하고 아이의 상태를 파악할 수 있는 '일일 건강 및 활동 보고서'를 작성해 주세요.

                                [작성 지침 및 조건]

                                1. 상황 공감 및 안심: 도입부에는 보호자가 외출 중 혼자 있는 반려동물을 걱정하는 마음을 공감하고 안심시켜주는 따뜻한 인사말을 포함하세요.

                                2. 상세한 데이터 해석: 단순히 횟수나 양을 나열하지 마세요. 제공된 데이터(totalCount, amount, leftovers)를 바탕으로 아이의 심리 상태(예: 분리불안 유무)와 신체 상태(소화, 비뇨기 건강 등)를 초보자도 이해하기 쉬운 언어로 연결하여 분석하세요.

                                3. 1인 가구 맞춤 솔루션: 잔반량(leftovers)이나 급여 패턴을 분석하여, 1인 가구 라이프스타일(장시간 외출 후 귀가)에 맞는 실질적인 개선점(식기 위생 관리, 1회 토출량 미세 조절, 급식기/급수기 및 홈캠 위치 점검 등)을 구체적으로 제안하세요.

                                4. 예외 상황 처리 (매우 중요): 데이터 상 급수 또는 급식 기록이 없을 때(totalCount가 0이거나 로그가 비어있는 경우)는 반드시 "급수 기록이 없습니다." 또는 **"급식 기록이 없습니다."**라는 문구를 명확히 출력하세요. 또한, 기기 연동 오류인지 실제 단식/단수인지 파악하기 위한 조언을 추가하세요.

                                5. 출력 형식: 친절하고 부드러운 말투를 사용하되, 가독성을 위해 소제목과 글머리 기호를 적절히 활용하여 구조화해 주세요. 최종 출력물에는 리포트 내용만 포함하고, 프롬프트에 대한 대답이나 사족은 절대 포함하지 마세요.
                                """),
                        Map.of("role", "user", "content", prompt)
                ),
                "max_completion_tokens", 3000
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
