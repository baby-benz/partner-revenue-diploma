package ru.itmo.registrationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.itmo.registrationservice.domain.entity.Partner;

public interface PartnerProfileRepository extends JpaRepository<Partner, String>, JpaSpecificationExecutor<Partner> {
}
