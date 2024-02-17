package study.toss.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import study.toss.dto.PaymentResponse;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Table(name = "payment")
public class Payment extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    private String orderId;

    private String paymentKey;

    private Long amount;

    private Long pointAmount;

    private String orderName;

    private boolean isSuccess;

    private String failReason;

    private boolean isCancel;

    private String CancelReason;



    @Builder
    public Payment(String orderId, String paymentKey, Long amount, Long pointAmount, String orderName,
                   boolean isSuccess, String failReason, boolean isCancel, String cancelReason) {
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
        this.pointAmount = pointAmount;
        this.orderName = orderName;
        this.isSuccess = isSuccess;
        this.failReason = failReason;
        this.isCancel = isCancel;
        CancelReason = cancelReason;
    }


    public PaymentResponse paymentResponse() { // DB에 저장하게 될 결제 관련 정보들
        return PaymentResponse.builder()
                .amount(amount)
                .pointAmount(pointAmount)
                .orderName(orderName)
                .orderId(orderId)
                .isSuccess(isSuccess)
                .failReason(failReason)
                .createdAt(String.valueOf(getCreatedDate()))
                .build();
    }
}
