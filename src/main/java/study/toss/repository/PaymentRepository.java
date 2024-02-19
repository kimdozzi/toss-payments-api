package study.toss.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import study.toss.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(String orderId);
}
