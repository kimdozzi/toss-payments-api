package study.toss.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import study.toss.config.TossPaymentConfig;
import study.toss.domain.Payment;
import study.toss.dto.PaymentResponse;
import study.toss.dto.PaymentSuccessResponse;
import study.toss.repository.PaymentRepository;
import study.toss.util.exception.BusinessException;
import study.toss.util.exception.ErrorCode;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TossPaymentConfig tossPaymentConfig;

    @Transactional
    public PaymentResponse requestTossPayment(Payment payment) {
//        if (payment.getAmount() < 1000) {
//            // TODO 처리
//        }
        paymentRepository.save(payment);
        return payment.paymentResponse();
    }

    @Transactional
    public PaymentSuccessResponse tossPaymentSuccess(String orderId, String paymentKey, Long amount) {
        Payment payment = verifyPayment(orderId, amount);
        PaymentSuccessResponse result = requestPaymentAccept(paymentKey, orderId, amount);
        payment.setPaymentSuccessStatus(paymentKey, true);
        return result;
    }

    @Transactional
    public PaymentSuccessResponse requestPaymentAccept(String orderId, String paymentKey, Long amount) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = getHeaders();
        JSONObject params = new JSONObject();
        params.put("orderId", orderId);
        params.put("amount", amount);

        PaymentSuccessResponse successResponse;
        try {
            successResponse = restTemplate.postForObject(TossPaymentConfig.URL + paymentKey,
                    // 최종 결제 승인 요청을 보내는데, 요청 URL은 Config에 작성한 url + paymentKey 이다.
                    new HttpEntity<>(params, headers),
                    PaymentSuccessResponse.class
            );
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }
        return successResponse;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String encodedAuthKey = new String(
                Base64.getEncoder().encode((tossPaymentConfig.getTestSecretKey() + ":").getBytes(StandardCharsets.UTF_8)));
        // basic authorization 인가 코드를 보낼 때 시크릿 키를 base64를 이용하여 인코딩하여 보내게 되는데
        // 이 때, {시크릿키 + ":"} 조합으로 인코딩 필수

        headers.setBasicAuth(encodedAuthKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    public Payment verifyPayment(String orderId, Long amount) {
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(() -> new BusinessException(
                ErrorCode.MEMBER_NOT_FOUND));

        if (payment.getAmount().equals(amount)) {
            Long pointAmount = payment.getPointAmount();
            if (pointAmount == (amount / 10) && (pointAmount * 10) == amount) {
                return payment;
            }
        }
        throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
    }

    public void tossPaymentFail(String message, String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(() -> {
           throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        });
        payment.setPaymentFailStatus(message, false);
    }
}
