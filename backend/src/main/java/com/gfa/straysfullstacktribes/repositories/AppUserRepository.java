package com.gfa.straysfullstacktribes.repositories;

import com.gfa.straysfullstacktribes.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsernameAndPassword(String username, String password);

    AppUser findByUsername(String username);

    Optional<AppUser> findAppUserByUsername(String username);

    Boolean existsByUsername(String username);

    AppUser findByRegistrationToken(String registrationToken);

    Optional<AppUser> findAppUserByRegistrationToken(String registrationToken);

    Boolean existsByEmail(String email);

    Optional<AppUser> findByUsernameAndEmailAndPassword(String username, String email, String password);

    Optional<AppUser> findAppUserByEmail(String email);
}
