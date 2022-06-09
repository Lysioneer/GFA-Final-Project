package com.gfa.straysfullstacktribes.repositories;

import com.gfa.straysfullstacktribes.models.AppUser;
import com.gfa.straysfullstacktribes.models.Chat;
import com.gfa.straysfullstacktribes.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessagesRepository extends JpaRepository<Message, Long> {

    List<Message> findAllByChatId(Chat chat);



}
