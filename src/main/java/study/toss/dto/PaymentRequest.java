package study.toss.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import study.toss.domain.Payment;

@Data
public class PaymentRequest {
    private Long amount;
    private String orderName;
    private Long pointAmount;


    @Builder
    public PaymentRequest(Long amount, String orderName, Long pointAmount) {
        this.amount = amount;
        this.orderName = orderName;
        this.pointAmount = pointAmount;
    }

    public Payment toEntity() {
        return Payment.builder()
                .orderId(UUID.randomUUID().toString())
                .amount(amount)
                .pointAmount(pointAmount)
                .orderName(orderName)
                .isSuccess(false)
                .failReason("")
                .build();
    }
}
