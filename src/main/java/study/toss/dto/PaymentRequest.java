package study.toss.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import study.toss.domain.Payment;

@Data
public class PaymentRequest {
    private Long amount; // 가격 정보
    private Long pointAmount; // 충전될 포인트 정보
    private String orderName; // 주문명
    private String successUrl; // 성공 시 리다이렉트 될 URL
    private String failUrl; // 실패 시 리다이렉트 될 URL


    @Builder
    public PaymentRequest(Long amount, Long pointAmount, String orderName, String successUrl, String failUrl) {
        this.amount = amount;
        this.pointAmount = pointAmount;
        this.orderName = orderName;
        this.successUrl = successUrl;
        this.failUrl = failUrl;
    }


    public Payment toEntity() {
        return Payment.builder()
                .amount(amount)
                .pointAmount(pointAmount)
                .orderName(orderName)
                .orderId(UUID.randomUUID().toString())
                .isSuccess(false)
                .build();
    }
}
