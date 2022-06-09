package com.gfa.straysfullstacktribes.repositories;

import com.gfa.straysfullstacktribes.models.AppUser;
import com.gfa.straysfullstacktribes.models.Chat;
import com.gfa.straysfullstacktribes.models.ChatMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMembersRepository extends JpaRepository<ChatMember, Long> {

    Boolean existsByChatIdAndMemberId(Chat chat, AppUser appUser);

    Boolean existsById(AppUser appUser);

    List<ChatMember> findAllByMemberId(AppUser appUser);

    List<ChatMember> findAllByChatId(Chat chat);

    ChatMember findByChatIdAndMemberId(Chat chat, AppUser appUser);

}
