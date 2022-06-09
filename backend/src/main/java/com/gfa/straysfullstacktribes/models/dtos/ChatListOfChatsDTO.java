package com.gfa.straysfullstacktribes.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatListOfChatsDTO {

    private List<ChatOneFullChatInfoDTO> allChats;
}
