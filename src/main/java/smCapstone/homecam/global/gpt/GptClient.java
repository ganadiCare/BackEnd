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
                                당신은 반려동물 건강 관리 전문가입니다.
                                주어진 데이터를 바탕으로 반려동물의 하루 활동 보고서를 작성해주세요.
                                보고서는 친절하고 이해하기 쉬운 언어로 작성하며, 건강 상태와 개선점을 포함해주세요.
                                급수 또는 급식 기록이 없을 때는 "급수 기록이 없습니다." 또는 "급식 기록이 없습니다."라고 명시해주세요.
                                보고서 이외의 추가 정보는 포함하지 말아주세요.
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
