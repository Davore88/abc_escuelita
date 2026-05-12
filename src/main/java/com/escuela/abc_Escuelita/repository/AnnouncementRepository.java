package com.escuela.abc_Escuelita.repository;

import com.escuela.abc_Escuelita.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByInstitutionId(Long institutionId);
}
