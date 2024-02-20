package study.toss.dto;

import lombok.Data;

@Data
public class PaymentFailRequest {
    private String message;
    private String orderId;
}
