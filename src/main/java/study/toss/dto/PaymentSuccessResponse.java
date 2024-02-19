package study.toss.dto;

import lombok.Data;

@Data
public class PaymentSuccessResponse {
    private String orderId;
    private String paymentKey;
    private Long amount;
    private Long pointAmount;
    private String orderName;
    private boolean isSuccess;
    private String failReason;
}
