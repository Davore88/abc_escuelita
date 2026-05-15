package com.escuela.abc_Escuelita.repository;

import com.escuela.abc_Escuelita.model.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
    List<AccessLog> findByInstitutionId(Long institutionId);
    List<AccessLog> findByInstitutionIdAndTimestampBetween(Long institutionId, LocalDateTime start, LocalDateTime end);
    List<AccessLog> findByInstitutionIdAndStudentId(Long institutionId, Long studentId);
    AccessLog findFirstByStudentIdAndTimestampBetweenOrderByTimestampDesc(Long studentId, LocalDateTime start, LocalDateTime end);
    List<AccessLog> findByStudentIdAndTimestampBetweenOrderByTimestampAsc(Long studentId, LocalDateTime start, LocalDateTime end);
    List<AccessLog> findTop20ByInstitutionIdOrderByTimestampDesc(Long institutionId);
}
