package study.toss.util.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import study.toss.util.response.dto.CommonResponse;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class BusinessExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<CommonResponse> globalBusinessExceptionHandler(BusinessException e) {
        log.error("[ERROR]" + e.getMessage(), e);

        return ResponseEntity.badRequest().body(
                new CommonResponse(e.getStatus(), e.getMessage())
        );
    }
}
