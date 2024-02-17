package study.toss.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
public class PaymentRequest {
    private PayType payType;
    private Long amount; // 가격 정보
    private String orderName; // 주문명

    private String yourSuccessUrl; // 성공 시 리다이렉트 될 URL
    private String yourFailUrl; // 실패 시 리다이렉트 될 URL

    @Builder
    public PaymentRequest(PayType payType, Long amount, String orderName, String yourSuccessUrl, String yourFailUrl) {
        this.payType = payType;
        this.amount = amount;
        this.orderName = orderName;
        this.yourSuccessUrl = yourSuccessUrl;
        this.yourFailUrl = yourFailUrl;
    }

    public Payment toEntity() {
        return Payment.builder()
                .payType(payType)
                .amount(amount)
                .orderName(orderName)
                .orderId(UUID.randomUUID().toString())
                .isSuccess(false)
                .build();
    }
}
