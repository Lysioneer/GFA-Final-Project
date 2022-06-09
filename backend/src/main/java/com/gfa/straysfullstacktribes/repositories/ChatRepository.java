package com.gfa.straysfullstacktribes.repositories;

import com.gfa.straysfullstacktribes.models.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
}
