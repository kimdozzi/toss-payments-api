package study.toss.service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

        Payment payment = verifyPayment(paymentSuccessRequest.getOrderId(), paymentSuccessRequest.getAmount());
        PaymentSuccessResponse result = requestPaymentAccept(paymentSuccessRequest);
        payment.setPaymentSuccessStatus(paymentSuccessRequest.getPaymentKey(), true);
        return result;
    }

    @Transactional
    public PaymentSuccessResponse requestPaymentAccept(PaymentSuccessRequest paymentSuccessRequest)
            throws Exception {
        JSONParser parser = new JSONParser();
        String orderId, amount, paymentKey;

        // 클라이언트에서 받은 JSON 요청 바디입니다.
        paymentKey = paymentSuccessRequest.getPaymentKey();
        orderId = paymentSuccessRequest.getOrderId();
        amount = String.valueOf(paymentSuccessRequest.getAmount());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("orderId", orderId);
        jsonObject.put("amount", amount);
        jsonObject.put("paymentKey", paymentKey);

        String widgetSecretKey = tossPaymentConfig.getTestSecretKey();
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes("UTF-8"));
        String authorizations = "Basic " + new String(encodedBytes, 0, encodedBytes.length);

        URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(jsonObject.toString().getBytes("UTF-8"));

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200 ? true : false;

        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        JSONObject jsonobj = (JSONObject) parser.parse(reader);
        responseStream.close();

        System.out.println("isSuccess = " + isSuccess);
        System.out.println("jsonobj.toJSONString() = " + jsonobj.toJSONString());

        return null;
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
