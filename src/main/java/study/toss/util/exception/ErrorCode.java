package study.toss.util.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    FAILED_POINT_PAYMENT(HttpStatus.BAD_REQUEST, "최소 충전 금액은 100원 이상입니다."),
    INVALID_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "최초 결제 요청 금액과 일치하지 않습니다."),
    FAILED_FINAL_PAYMENT(HttpStatus.BAD_REQUEST, "최종 결제가 승인되지 않았습니다"),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."),
    DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, "이미 존재하는 닉네임입니다"),
    INVALID_PROGRESS(HttpStatus.BAD_REQUEST, "존재하지 않는 정보입니다.");

    private final HttpStatus status;
    private final String message;
}
