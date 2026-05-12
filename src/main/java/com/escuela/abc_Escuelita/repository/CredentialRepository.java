package com.escuela.abc_Escuelita.repository;

import com.escuela.abc_Escuelita.model.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CredentialRepository extends JpaRepository<Credential, Long> {
    Optional<Credential> findByQrCodeData(String qrCodeData);
}
