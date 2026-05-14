package com.escuela.abc_Escuelita.repository;

import com.escuela.abc_Escuelita.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByInstitutionId(Long institutionId);
    List<Student> findByStudentGroupNameAndInstitutionId(String groupName, Long institutionId);

    @org.springframework.data.jpa.repository.Query("SELECT s FROM Student s WHERE " +
           "(LOWER(s.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.studentGroup.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "s.institution.id = :institutionId")
    List<Student> searchStudents(@org.springframework.data.repository.query.Param("searchTerm") String searchTerm, @org.springframework.data.repository.query.Param("institutionId") Long institutionId);
}
