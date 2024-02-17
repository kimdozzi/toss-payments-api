package study.toss.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import study.toss.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("select p from Payment p order by p.orderId desc")
    Payment findByOrderId(String orderId);

    @Query("select p from Payment p order by p.paymentKey desc")
    Payment findByPaymentKey(String paymentKey);
}
