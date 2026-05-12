package com.escuela.abc_Escuelita.repository;

import com.escuela.abc_Escuelita.model.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TutorRepository extends JpaRepository<Tutor, Long> {
}
