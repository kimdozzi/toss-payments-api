package study.datajpa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.Payment;
import study.datajpa.dto.PaymentResponse;
import study.datajpa.repository.PaymentRepository;

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
