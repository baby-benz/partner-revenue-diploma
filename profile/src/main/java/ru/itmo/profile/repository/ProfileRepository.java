package ru.itmo.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.itmo.profile.domain.entity.Profile;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, String>, JpaSpecificationExecutor<Profile> {
}
