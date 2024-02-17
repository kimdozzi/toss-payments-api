package study.toss.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class PaymentResponse {
    private Long amount; // 가격 정보
    private Long pointAmount; // 포인트 정보
    private String orderName; // 주문명
    private String orderId; // 주문 Id
    private boolean isSuccess; // 성공여부
    private String failReason; // 실패 이유
    private String successUrl; // 성공 시 리다이렉트 될 URL
    private String failUrl; // 실패 시 리다이렉트 될 URL
    private String createdAt; // 결제가 이루어진 시간

    @Builder
    public PaymentResponse(Long amount, Long pointAmount, String orderName, String orderId, boolean isSuccess,
                           String failReason, String successUrl, String failUrl, String createdAt) {
        this.amount = amount;
        this.pointAmount = pointAmount;
        this.orderName = orderName;
        this.orderId = orderId;
        this.isSuccess = isSuccess;
        this.failReason = failReason;
        this.successUrl = successUrl;
        this.failUrl = failUrl;
        this.createdAt = createdAt;
    }

    public void setSuccessUrl(String url) {
        this.successUrl = url;
    }

    public void setFailUrl(String url) {
        this.failUrl = url;
    }
}
