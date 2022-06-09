package com.gfa.straysfullstacktribes.repositories;

import com.gfa.straysfullstacktribes.models.AppUser;
import com.gfa.straysfullstacktribes.models.Kingdom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KingdomRepository extends JpaRepository<Kingdom, Long> {

    Optional<Kingdom> findByCorXAndCorY(Long x, Long Y);

    Kingdom findKingdomById(Long id);

    Boolean existsKingdomById(Long id);

    Boolean existsKingdomByName(String kingdomName);

    Optional<Kingdom> findKingdomByIdAndAppUserId(Long kingdomId, Long appUserId);

    List<Kingdom> findByAppUser(AppUser user);
}