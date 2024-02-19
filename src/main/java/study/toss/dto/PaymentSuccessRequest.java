package study.toss.dto;

import lombok.Data;

@Data
public class PaymentSuccessRequest {
    private String orderId;
    private String paymentKey;
    private Long amount;
}
