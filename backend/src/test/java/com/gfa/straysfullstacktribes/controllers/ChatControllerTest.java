package com.gfa.straysfullstacktribes.controllers;

import com.gfa.straysfullstacktribes.models.dtos.ChatListOfMembersDTO;
import com.gfa.straysfullstacktribes.models.dtos.ChatListOfMessagesDTO;
import com.gfa.straysfullstacktribes.models.dtos.ChatOneFullChatInfoDTO;
import com.gfa.straysfullstacktribes.models.dtos.RegistrationRequestDTO;
import com.gfa.straysfullstacktribes.repositories.AppUserRepository;
import com.gfa.straysfullstacktribes.repositories.KingdomRepository;
import com.gfa.straysfullstacktribes.repositories.TroopRepository;
import com.gfa.straysfullstacktribes.services.*;
import com.gfa.straysfullstacktribes.utilities.JwtUtilities;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ChatController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ChatControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    MapService mapService;
    @MockBean
    AppUserService appUserService;
    @MockBean
    KingdomService kingdomService;
    @MockBean
    AppUserRepository appUserRepository;
    @MockBean
    KingdomRepository kingdomRepository;
    @MockBean
    JwtUtilities jwtUtilities;
    @MockBean
    RegistrationService registrationService;
    @MockBean
    RegistrationRequestDTO registrationRequestDTO;
    @MockBean
    TroopRepository troopRepository;
    @MockBean
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @MockBean
    BuildingService buildingService;
    @MockBean
    UnitLevelService unitLevelService;

    @MockBean
    ChatService chatService;

    @Test
    void createChatNotEnoughMembersTest() throws Exception{

        Mockito.when(chatService.getMembers(any())).thenReturn(0L);

        String mockToken = "mockToken";

        mvc.perform(MockMvcRequestBuilders.post("/chats")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"members\": [\"Lucka\", \"Matej\"]," +
                                "    \"subject\": \"subject\"," +
                                "    \"text\": \"text\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Select more members for creating a chat!"));
    }

//    @Test
//    void createChatNoSubjectAndTextTest() throws Exception{
//
//        Mockito.when(chatService.getMembers(any())).thenReturn(2L);
//        Mockito.when(chatService.getSubject(any())).thenReturn(null);
//        Mockito.when(chatService.getText(any())).thenReturn(null);
//
//        String mockToken = "mockToken";
//
//        mvc.perform(MockMvcRequestBuilders.post("/chats")
//                        .header("Authorization", "Bearer " + mockToken)
//                        .content("{\"members\": [\"Lucka\", \"Matej\"]," +
//                                "    \"subject\": \"\"," +
//                                "    \"text\": \"\"}")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("error").value("Provide first message and a subject of the chat!"));
//    }

//    @Test
//    void createChatNoSubjectTest() throws Exception{
//
//        Mockito.when(chatService.getMembers(any())).thenReturn(2L);
//        Mockito.when(chatService.getSubject(any())).thenReturn(null);
//        Mockito.when(chatService.getText(any())).thenReturn("value");
//
//        String mockToken = "mockToken";
//
//        mvc.perform(MockMvcRequestBuilders.post("/chats")
//                        .header("Authorization", "Bearer " + mockToken)
//                        .content("{\"members\": [\"Lucka\", \"Matej\"]," +
//                                "    \"subject\": \"\"," +
//                                "    \"text\": \"something\"}")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("error").value("The subject cannot be empty!"));
//    }

//    @Test
//    void createChatNoTextTest() throws Exception{
//
//        Mockito.when(chatService.getMembers(any())).thenReturn(2L);
//        Mockito.when(chatService.getSubject(any())).thenReturn("value");
//        Mockito.when(chatService.getText(any())).thenReturn(null);
//
//        String mockToken = "mockToken";
//
//        mvc.perform(MockMvcRequestBuilders.post("/chats")
//                        .header("Authorization", "Bearer " + mockToken)
//                        .content("{\"members\": [\"Lucka\", \"Matej\"]," +
//                                "    \"subject\": \"something\"," +
//                                "    \"text\": \"\"}")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("error").value("Provide first message of the chat!"));
//    }

//    @Test
//    void createChatTest() throws Exception{
//
//        Mockito.when(chatService.getMembers(any())).thenReturn(2L);
//        Mockito.when(chatService.getSubject(any())).thenReturn("value");
//        Mockito.when(chatService.getText(any())).thenReturn("value2");
//
//        String mockToken = "mockToken";
//
//        mvc.perform(MockMvcRequestBuilders.post("/chats")
//                        .header("Authorization", "Bearer " + mockToken)
//                        .content("{\"members\": [\"Lucka\", \"Matej\"]," +
//                                "    \"subject\": \"something\"," +
//                                "    \"text\": \"somethingToo\"}")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("message").value("Chat successfully created!"));
//    }

    @Test
    void addingAdditionalMembersNoOwnerTest() throws Exception{

        Mockito.when(chatService.ownsChat(any(), any())).thenReturn(false);

        String mockToken = "mockToken";

        mvc.perform(MockMvcRequestBuilders.put("/chats/1/members")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"additionalMembers\": [\"Lucka\", \"Matej\"]}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("You can't add new members"));
    }

    @Test
    void addingAdditionalMembersTest() throws Exception{

        Mockito.when(chatService.ownsChat(any(), any())).thenReturn(true);

        String mockToken = "mockToken";

        mvc.perform(MockMvcRequestBuilders.put("/chats/1/members")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"additionalMembers\": [\"Lucka\", \"Matej\"]}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("New members added!"));
    }

    @Test
    void addingMessageIntoChatNoMemberTest() throws Exception{

        Mockito.when(chatService.isMember(any(), any())).thenReturn(false);

        String mockToken = "mockToken";

        mvc.perform(MockMvcRequestBuilders.put("/chats/1/messages")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"additionalMembers\": [\"Lucka\", \"Matej\"]}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("You are not a member of this chat"));
    }

    @Test
    void addingMessageIntoChatTest() throws Exception{

        Mockito.when(chatService.isMember(any(), any())).thenReturn(true);

        String mockToken = "mockToken";

        mvc.perform(MockMvcRequestBuilders.put("/chats/1/messages")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"additionalMembers\": [\"Lucka\", \"Matej\"]}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("New message added successfully!"));
    }

    @Test
    void listChatsNoMemberTest() throws Exception{

        Mockito.when(chatService.hasChat(any())).thenReturn(false);

        String mockToken = "mockToken";

        mvc.perform(MockMvcRequestBuilders.get("/chats")
                        .header("Authorization", "Bearer " + mockToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("You are not a member in any chat."));
    }

    @Test
    void listSpecificChatNoMemberOrNoChatTest() throws Exception{

        Mockito.when(chatService.ListSpecificChat(any(), any())).thenReturn(null);

        String mockToken = "mockToken";

        mvc.perform(MockMvcRequestBuilders.get("/chats/1")
                        .header("Authorization", "Bearer " + mockToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("You are not member of this chat, or this chat doesn't even exists"));
    }
}
