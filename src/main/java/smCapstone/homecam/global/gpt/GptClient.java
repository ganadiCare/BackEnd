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
                                [역할]
                                1인 가구·초보 반려인을 돕는 반려동물 건강 관리 전문가입니다.

                                [목표]
                                JSON 급식·급수 데이터로 일일 보고서를 작성하세요.

                                [출력 규칙]
                                전체 출력은 공백 포함 약 1000자로 작성하세요.
                                아래 소제목·순서·개수는 반드시 그대로 유지하며 생성·병합하지 마세요.
                                마크다운(#, *, -, `) 금지. 소제목은 대괄호 사용.
                                소제목과 내용 사이 빈 줄 1개.
                                모든 수치는 JSON 값을 그대로 사용, 추정 금지. 값 없으면 "기록 없음".
                                친절하고 부드러운 문체를 사용하세요. 아래 형식 외 내용 포함 금지.
                                급식 또는 급수 기록이 없을 때는 기기 점검을 안내하는 문구를 포함하세요. 질병 단정 금지.

                                [오늘의 한눈 요약]
                                전체 보고서 내용(급식·급수 상태, 특이사항)을 종합해 약 150자 서술형 문장으로 요약하세요.

                                [급식 현황]
                                총 급식 횟수: {feeding.totalCount}회 / 총 급여량: {feeding.totalAmount}g / 잔반량: {feeding.leftovers}g

                                [급수 현황]
                                총 급수 횟수: {watering.totalCount}회 / 총 급수량: {watering.totalAmount}ml / 잔량: {watering.leftovers}ml

                                [급식·급수 종합 분석]
                                급식·급수 데이터를 종합해 식욕·소화·수분 섭취·비뇨기 건강·분리불안 가능성 등을 3~4문장으로 설명하세요. 질병 단정 금지. 
                                """),
                        Map.of("role", "user", "content", prompt)
                ),
                "max_completion_tokens", 3000,
                "reasoning_effort", "minimal"
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, Map.class);
            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null) {
                throw new ReportException(ReportErrorCode.GPT_API_ERROR);
            }

            List<Map<String, Object>> choices =
                    (List<Map<String, Object>>) responseBody.get("choices");
            if (choices == null || choices.isEmpty()) {
                log.warn("GPT API 응답에 choices가 없습니다. model={}, usage={}",
                        model, responseBody.get("usage"));
                throw new ReportException(ReportErrorCode.GPT_API_ERROR);
            }

            Map<String, Object> choice = choices.get(0);
            Map<String, Object> message = (Map<String, Object>) choice.get("message");
            String content = message != null ? (String) message.get("content") : null;

            log.info("GPT API 응답 완료: model={}, finishReason={}, usage={}",
                    model, choice.get("finish_reason"), responseBody.get("usage"));

            if (content == null || content.isBlank()) {
                log.warn("GPT API가 빈 응답을 반환했습니다. model={}, finishReason={}, usage={}",
                        model, choice.get("finish_reason"), responseBody.get("usage"));
                throw new ReportException(ReportErrorCode.GPT_API_ERROR);
            }
            return content;
        } catch (ReportException e) {
            throw e;
        } catch (Exception e) {
            log.error("GPT API 호출 실패: {}", e.getMessage());
            throw new ReportException(ReportErrorCode.GPT_API_ERROR);
        }
    }
}
