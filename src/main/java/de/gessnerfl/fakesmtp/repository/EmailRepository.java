package de.gessnerfl.fakesmtp.repository;

import de.gessnerfl.fakesmtp.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long>, JpaSpecificationExecutor<Email> {

    @Transactional
    @Modifying
    @Query(value = "DELETE email o WHERE o.id IN ( SELECT i.id FROM email i ORDER BY i.received_on DESC OFFSET ?1)", nativeQuery = true)
    int deleteEmailsExceedingDateRetentionLimit(int maxNumber);

    @Transactional
    @Query
    List<Email> findBySubject(String subject);

}
