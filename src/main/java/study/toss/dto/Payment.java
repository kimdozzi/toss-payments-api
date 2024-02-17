package study.toss.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id; // 결제 번호

    private String paymentKey;

    private Long amount;

    private String orderId;

    private String orderName;

    @Enumerated(EnumType.STRING)
    private PayType payType;

    private boolean isSuccess;

    private String failReason;

    @Builder
    public Payment(PayType payType, Long amount, String orderName, String orderId, boolean isSuccess, String paymentKey,
                   String failReason) {
        this.payType = payType;
        this.amount = amount;
        this.orderName = orderName;
        this.orderId = orderId;
        this.isSuccess = isSuccess;
        this.paymentKey = paymentKey;
        this.failReason = failReason;
    }

    public PaymentResponse paymentResponse() { // DB에 저장하게 될 결제 관련 정보들
        return PaymentResponse.builder()
                .payType(payType.name())
                .amount(amount)
                .orderName(orderName)
                .orderId(orderId)
                //.customerEmail(customer.getEmail())
                //.customerName(customer.getName())
                //.createdAt(String.valueOf(getCreatedAt()))
                //.cancelYN(cancelYN)
                .failReason(failReason)
                .build();
    }
}
