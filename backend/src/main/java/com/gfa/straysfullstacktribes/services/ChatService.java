package com.gfa.straysfullstacktribes.services;

import com.gfa.straysfullstacktribes.models.AppUser;
import com.gfa.straysfullstacktribes.models.dtos.*;
import org.springframework.http.HttpHeaders;

import java.text.ParseException;
import java.util.List;

public interface ChatService {

    String getSubject(ChatCreateRequestDTO chatCreateRequestDTO);

    String getText(ChatCreateRequestDTO chatCreateRequestDTO);

    Long getMembers(ChatCreateRequestDTO chatCreateRequestDTO);

    void createChat(ChatCreateRequestDTO chatCreateRequestDTO, HttpHeaders headers);

    void addingAdditionalMembers(ChatAddMemberRequestDTO chatAddMemberRequestDTO, Long id);

    void addingMessage(ChatAddMessageRequestDTO chatAddMessageRequestDTO, Long id, HttpHeaders headers);

    Boolean ownsChat(HttpHeaders headers, Long id);

    Boolean isMember(HttpHeaders headers, Long id);

    String usernameFromToken(HttpHeaders headers);

    Long appUserId(HttpHeaders headers);

    AppUser appUserObject(HttpHeaders headers);

    Boolean hasChat(HttpHeaders headers);

    List getAllChats(HttpHeaders headers) throws ParseException;

    ChatOneFullChatInfoDTO ListSpecificChat(HttpHeaders headers, Long id);

    Boolean confirmMember(ChatCreateRequestDTO chatCreateRequestDTO);

    Boolean checkTheMember(ChatCreateRequestDTO chatCreateRequestDTO, HttpHeaders headers);

}
