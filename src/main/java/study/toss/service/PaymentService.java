package study.toss.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.toss.dto.Payment;
import study.toss.dto.PaymentResponse;
import study.toss.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentResponse requestTossPayment(Payment payment) {
        return paymentRepository.save(payment).paymentResponse();
    }

//    public PaymentSuccessResponse tossPaymentSuccess(String orderId, String paymentKey, Long amount) {
//        return null;
//    }
}
