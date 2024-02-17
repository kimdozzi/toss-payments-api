package study.toss.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import study.toss.config.TossPaymentConfig;
import study.toss.dto.PaymentRequest;
import study.toss.dto.PaymentResponse;
import study.toss.service.PaymentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {
    /*
    *    success_url: "http://localhost:8080/api/payment/toss/success"
    *    fail_url: "http://localhost:8080/api/payment/toss/fail"
    */

    private final PaymentService paymentService;
    private final TossPaymentConfig tossPaymentConfig;

    // 1. 프론트가 사용자가 입력한 정보를 [결제하기] 버튼을 통해 /toss 로 결제요청 api 호출
    // 현재 컨트롤러에서 사용자 정보를 저장할 것
    // Request : 사용자 정보
    // Response : DB에 정보 저장 후 사용자 정보 반환
    @PostMapping("/toss")
    public ResponseEntity requestTossPayment(@RequestBody PaymentRequest paymentRequest) {
        PaymentResponse paymentResponseDto = paymentService.requestTossPayment(paymentRequest.toEntity());
        paymentResponseDto.setSuccessUrl(paymentRequest.getSuccessUrl() == null ? tossPaymentConfig.getSuccessUrl()
                : paymentRequest.getSuccessUrl());
        paymentResponseDto.setFailUrl(paymentRequest.getFailUrl() == null ? tossPaymentConfig.getFailUrl()
                : paymentRequest.getFailUrl());
        return ResponseEntity.ok().body(paymentResponseDto);
    }

    @GetMapping("/toss/success")
    public ResponseEntity tossPaymentSuccess(@RequestParam String orderId,
                                             @RequestParam String paymentKey,
                                             @RequestParam Long amount) {
        System.out.println("orderId = " + orderId);
        System.out.println("paymentKey = " + paymentKey);
        System.out.println("amount = " + amount);

        // return ResponseEntity.ok().body(paymentService.tossPaymentSuccess(orderId, paymentKey, amount));
        return null;

    }

    @GetMapping("/toss/fail")
    public ResponseEntity tossPaymentFail() {

        return null;
    }

}
