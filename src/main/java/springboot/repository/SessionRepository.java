package springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot.model.Session;

public interface SessionRepository extends JpaRepository<Session, String> {
}