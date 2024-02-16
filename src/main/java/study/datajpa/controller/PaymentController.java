package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.config.TossPaymentConfig;
import study.datajpa.dto.PaymentRequest;
import study.datajpa.dto.PaymentResponse;
import study.datajpa.service.PaymentService;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final TossPaymentConfig tossPaymentConfig;

    @GetMapping("/toss/request")
    public ResponseEntity requestTossPayment(@RequestBody PaymentRequest paymentRequest) {
        PaymentResponse paymentResponseDto = paymentService.requestTossPayment(paymentRequest.toEntity());
        paymentResponseDto.setSuccessUrl(paymentRequest.getYourSuccessUrl() == null ? tossPaymentConfig.getSuccessUrl()
                : paymentRequest.getYourSuccessUrl());
        paymentResponseDto.setFailUrl(paymentRequest.getYourFailUrl() == null ? tossPaymentConfig.getFailUrl()
                : paymentRequest.getYourFailUrl());
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

}
