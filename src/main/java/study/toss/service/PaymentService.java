package study.toss.service;

import static java.lang.Long.valueOf;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.toss.config.TossPaymentConfig;
import study.toss.domain.Payment;
import study.toss.dto.PaymentResponse;
import study.toss.dto.PaymentSuccessRequest;
import study.toss.dto.PaymentSuccessResponse;
import study.toss.repository.PaymentRepository;
import study.toss.util.exception.BusinessException;
import study.toss.util.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
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
    public PaymentSuccessResponse tossPaymentSuccess(PaymentSuccessRequest paymentSuccessRequest) throws Exception {
        Payment payment = verifyPayment(paymentSuccessRequest.getOrderId(),
                valueOf(paymentSuccessRequest.getAmount()));
        PaymentSuccessResponse result = requestPaymentAccept(paymentSuccessRequest);
        payment.setPaymentSuccessStatus(paymentSuccessRequest.getPaymentKey(), true);
        return result;
    }

    @Transactional
    public PaymentSuccessResponse requestPaymentAccept(PaymentSuccessRequest paymentSuccessRequest) throws Exception {
        JSONParser parser = new JSONParser();
        String orderId;
        String amount;
        String paymentKey;

        // 클라이언트에서 받은 JSON 요청 바디입니다.
        paymentKey = paymentSuccessRequest.getPaymentKey();
        orderId = paymentSuccessRequest.getOrderId();
        amount = paymentSuccessRequest.getAmount();

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("paymentKey", paymentKey);
        hashMap.put("orderId", orderId);
        hashMap.put("amount", String.valueOf(amount));

        JSONObject obj = new JSONObject(hashMap);

        // 토스페이먼츠 API는 시크릿 키를 사용자 ID로 사용하고, 비밀번호는 사용하지 않습니다.
        // 비밀번호가 없다는 것을 알리기 위해 시크릿 키 뒤에 콜론을 추가합니다.
        String widgetSecretKey = "test_sk_Poxy1XQL8RgjajjBQvAN37nO5Wml";
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        // 결제를 승인하면 결제수단에서 금액이 차감돼요.
        URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(obj.toString().getBytes(StandardCharsets.UTF_8));

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200;

        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

        // 결제 성공 및 실패 비즈니스 로직을 구현하세요.
        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        JSONObject jsonObject = (JSONObject) parser.parse(reader);
        responseStream.close();

        PaymentSuccessResponse successResponse = PaymentSuccessResponse.builder()
                .paymentKey(String.valueOf(jsonObject.get("paymentKey")))
                .amount((Long) jsonObject.get("amount"))
                .orderName("카드")
                .pointAmount(10L)
                .orderId(String.valueOf(jsonObject.get("orderId")))
                .build();

        System.out.println("jsonObject = " + jsonObject.toJSONString());

        return successResponse;
    }

    public Payment verifyPayment(String orderId, Long amount) {
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(() -> new BusinessException(
                ErrorCode.MEMBER_NOT_FOUND));

        if (amount < 100) {
            throw new BusinessException(ErrorCode.FAILED_POINT_PAYMENT);
        }
        if (payment.getAmount().equals(amount)) {
            Long pointAmount = payment.getPointAmount();
            if (pointAmount == (amount / 10) && (pointAmount * 10) == amount) {
                return payment;
            }
        }
        throw new BusinessException(ErrorCode.INVALID_PAYMENT_AMOUNT);
    }

    public void tossPaymentFail(String message, String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(() -> {
            throw new BusinessException(ErrorCode.FAILED_FINAL_PAYMENT);
        });
        payment.setPaymentFailStatus(message, false);
    }
}
