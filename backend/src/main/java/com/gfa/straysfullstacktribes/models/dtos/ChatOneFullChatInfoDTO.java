package com.gfa.straysfullstacktribes.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatOneFullChatInfoDTO {

    private Long id;

    private String chatOwner;

    private String subject;

    private String lastMessageUploaded;

    private String lastViewed;

    private Boolean unread;

    List<ChatListOfMembersDTO> chatListOfMembersDTOS;

    List<ChatListOfMessagesDTO> chatListOfMessagesDTOS;
}
