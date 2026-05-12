package com.escuela.abc_Escuelita.repository;

import com.escuela.abc_Escuelita.model.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long> {
}
