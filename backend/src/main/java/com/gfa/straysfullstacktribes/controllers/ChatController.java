package com.gfa.straysfullstacktribes.controllers;

import com.gfa.straysfullstacktribes.models.dtos.*;
import com.gfa.straysfullstacktribes.models.ErrorMessage;
import com.gfa.straysfullstacktribes.models.SuccessMessage;
import com.gfa.straysfullstacktribes.services.ChatService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/chats")
public class ChatController {

    private final ChatService chatService;              //TODO: Add test for this controller and service!

    @PostMapping()
    public ResponseEntity createChat(@RequestBody ChatCreateRequestDTO chatCreateRequestDTO,
                                     @RequestHeader HttpHeaders headers) {


        if (chatService.getMembers(chatCreateRequestDTO) < 1) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Select more members for creating a chat!"));

        } else if(chatService.checkTheMember(chatCreateRequestDTO, headers) == false) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("You cannot add yourself as another member!"));

        }else if (chatService.confirmMember(chatCreateRequestDTO) == false) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Selected user doesn't exist"));

        } else if ((chatService.getText(chatCreateRequestDTO) == null || chatService.getText(chatCreateRequestDTO).isBlank()) &&
                (chatService.getSubject(chatCreateRequestDTO) == null || chatService.getSubject(chatCreateRequestDTO).isBlank())) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Provide first message and a subject of the chat!"));

        } else if (chatService.getSubject(chatCreateRequestDTO) == null || chatService.getSubject(chatCreateRequestDTO).isBlank()) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("The subject cannot be empty!"));

        } else if (chatService.getText(chatCreateRequestDTO) == null || chatService.getText(chatCreateRequestDTO).isBlank()) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("Provide first message of the chat!"));

        }

        chatService.createChat(chatCreateRequestDTO, headers);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessMessage("Chat successfully created!"));
    }

    @PutMapping("/{id}/members")
    public ResponseEntity AddingAdditionalMembers(
           @RequestBody ChatAddMemberRequestDTO chatAddMemberRequestDTO, @PathVariable(name = "id") Long id, @RequestHeader HttpHeaders headers){

        if (!chatService.ownsChat(headers, id)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("You can't add new members"));
        }

        chatService.addingAdditionalMembers(chatAddMemberRequestDTO, id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessMessage("New members added!"));
    }

    @PutMapping("/{id}/messages")
    public ResponseEntity AddingMessageIntoChat(@RequestBody ChatAddMessageRequestDTO chatAddMessageRequestDTO, @PathVariable(name = "id") Long id, @RequestHeader HttpHeaders headers){

        if (!chatService.isMember(headers, id)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("You are not a member of this chat"));
        }

        chatService.addingMessage(chatAddMessageRequestDTO, id, headers);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessMessage("New message added successfully!"));
    }

    @GetMapping()
    public ResponseEntity ListChats(@RequestHeader HttpHeaders headers) throws ParseException {

        if (!chatService.hasChat(headers)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessage("You are not a member in any chat."));
        }

        List<ChatOneFullChatInfoDTO> chatListOfChatsDTO = chatService.getAllChats(headers);

        return ResponseEntity.status(HttpStatus.OK)
                .body(chatListOfChatsDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity ListSpecificChat(@RequestHeader HttpHeaders headers, @PathVariable(name = "id") Long id){

        ChatOneFullChatInfoDTO chatOneFullChatInfoDTO = chatService.ListSpecificChat(headers, id);

        if (chatOneFullChatInfoDTO == null){

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("You are not member of this chat, or this chat doesn't even exists"));
        } else{

            return ResponseEntity.status(HttpStatus.OK).body(chatOneFullChatInfoDTO);
        }
    }
}