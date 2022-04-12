package springboot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;
import springboot.model.Person;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Integer> {
    //@Query("SELECT p FROM person p WHERE lastName LIKE ?1%")
    Page<Person> findAllByLastNameStartingWith(String lastName, Pageable pageable);



}
