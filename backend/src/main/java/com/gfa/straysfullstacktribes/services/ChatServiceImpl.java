package com.gfa.straysfullstacktribes.services;

import com.gfa.straysfullstacktribes.models.*;
import com.gfa.straysfullstacktribes.models.dtos.*;
import com.gfa.straysfullstacktribes.repositories.AppUserRepository;
import com.gfa.straysfullstacktribes.repositories.ChatMembersRepository;
import com.gfa.straysfullstacktribes.repositories.ChatRepository;
import com.gfa.straysfullstacktribes.repositories.MessagesRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {

    private AppUserRepository appUserRepository;
    private ChatRepository chatRepository;
    private ChatMembersRepository chatMembersRepository;
    private MessagesRepository messagesRepository;

    @Override
    public String getSubject(ChatCreateRequestDTO chatCreateRequestDTO) {

        return chatCreateRequestDTO.getSubject();
    }

    @Override
    public String getText(ChatCreateRequestDTO chatCreateRequestDTO) {

        return chatCreateRequestDTO.getText();
    }

    @Override
    public Boolean confirmMember(ChatCreateRequestDTO chatCreateRequestDTO) {

        for (int i = 0; i < chatCreateRequestDTO.members.stream().count(); i++){

            if (appUserRepository.existsByUsername(chatCreateRequestDTO.members.get(i)) == false) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Boolean checkTheMember(ChatCreateRequestDTO chatCreateRequestDTO, HttpHeaders headers) {

        String username = usernameFromToken(headers);

        for (int i = 0; i < chatCreateRequestDTO.members.size(); i++){

            if (chatCreateRequestDTO.members.get(i).equals(username)) {

                return false;
            }
        }
        return true;
    }

    @Override
    public Long getMembers(ChatCreateRequestDTO chatCreateRequestDTO) {

        return chatCreateRequestDTO.members.stream().count();
    }

    @Override
    public String usernameFromToken(HttpHeaders headers) {      //Getting username from the token

        String token = headers.getFirst(HttpHeaders.AUTHORIZATION);

        String subToken = token.substring(7);           //this will take the token and removes "Bearer " from it
        String[] chunks = subToken.split("\\.");  //This will split the actual token to two parts, header and payload
        Base64.Decoder decoder = Base64.getDecoder();   //used for decoding jwt token
        String decoded = new String(decoder.decode(chunks[1]));//this will decode the payload
        String[] splitDecoded = decoded.split("\"");//splits the payload
        String appUserName = splitDecoded[3];           //finds appUserName from splitDecoded
        return appUserName;
    }

    @Override
    public Long appUserId(HttpHeaders headers) {    //Getting appUser Id from the token

        return appUserObject(headers).getId();
    }

    @Override
    public AppUser appUserObject(HttpHeaders headers) {     //Getting appUser object from the token

        return appUserRepository.findByUsername(usernameFromToken(headers));
    }

    public String unixToDateConverter(Long time){

        Date date = new java.util.Date(time*1000L);

        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

        return sdf.format(date);
    }

    public Long dateToUnixConverter(String date) throws ParseException {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

        Date date2 = dateFormat.parse(date );

        Long temp = date2.getTime()/1000;

        return temp;
    }

    public Boolean lastRead(Long lastViewed, Long lastMsg){

        if (lastMsg > lastViewed){

            return true;
        } else{

            return false;
        }
    }

    @Override
    public void createChat(ChatCreateRequestDTO chatCreateRequestDTO, HttpHeaders headers){

        AppUser appUser = appUserObject(headers);

        Long lastUploadedMessage = System.currentTimeMillis()/1000;

        Long lastViewedMessage = System.currentTimeMillis()/1000;

         Chat chat = new Chat(
                 chatCreateRequestDTO.getSubject(),
                 appUser,
                 lastUploadedMessage,
                 lastViewedMessage);
        chatRepository.save(chat);

        ChatMember chatOwner = new ChatMember(chat, appUser, System.currentTimeMillis()/1000);  //Chat owner is also chat member
        chatMembersRepository.save(chatOwner);
        for (int i = 0; i < chatCreateRequestDTO.members.stream().count(); i++){
            ChatMember chatMember = new ChatMember(
                    chat,
                    appUserRepository.findByUsername(chatCreateRequestDTO.members.get(i)),
                    System.currentTimeMillis()/1000);
            chatMembersRepository.save(chatMember);
        }

        Message message = new Message(chat, chatCreateRequestDTO.getText(), appUser, lastUploadedMessage);
        messagesRepository.save(message);
    }

    @Override
    public void addingAdditionalMembers(ChatAddMemberRequestDTO chatAddMemberRequestDTO, Long id) {

        Chat chat = chatRepository.findById(id).get();

        for (int i = 0; i < chatAddMemberRequestDTO.getAdditionalMembers().stream().count(); i++){
            ChatMember chatMember = new ChatMember(
                    chat,
                    appUserRepository.findByUsername(chatAddMemberRequestDTO.getAdditionalMembers().get(i)),
                    System.currentTimeMillis()/1000);
            chatMembersRepository.save(chatMember);
        }
    }

    @Override
    public Boolean ownsChat(HttpHeaders headers, Long id) {

        Long userId = appUserId(headers);

        Long ownerId = chatRepository.findById(id).get().getOwnerId().getId();

        if (ownerId == userId){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Boolean isMember(HttpHeaders headers, Long id) {

        Long userId = appUserId(headers);

        Chat chat = chatRepository.findById(id).get();

        AppUser appUser = appUserRepository.findById(userId).get();

        Boolean exists = chatMembersRepository.existsByChatIdAndMemberId(chat, appUser);

        if (!exists){
            return false;
        } else{
            return true;
        }
    }

    @Override
    public void addingMessage(ChatAddMessageRequestDTO chatAddMessageRequestDTO, Long id, HttpHeaders headers) {

        AppUser appUser = appUserObject(headers);

        Chat chat = chatRepository.findById(id).get();

        ChatMember chatMember = chatMembersRepository.findByChatIdAndMemberId(chat, appUser);

        Message message = new Message(
          chat,
          chatAddMessageRequestDTO.getText(),
          appUser,
                System.currentTimeMillis()/1000
        );

        chat.setLastMessageUploaded(System.currentTimeMillis()/1000);
        chatRepository.save(chat);

        chatMember.setLastViewed(System.currentTimeMillis()/1000);
        chatMembersRepository.save(chatMember);

        messagesRepository.save(message);
    }

    @Override
    public Boolean hasChat(HttpHeaders headers) {

        AppUser appUser = appUserObject(headers);

        if (chatMembersRepository.existsById(appUser.getId())){
            return true;
        } else{
            return false;
        }
    }

    @Override
    public List getAllChats(HttpHeaders headers) throws ParseException {

        List<ChatMember> listOfAllTheChats = chatMembersRepository
                .findAllByMemberId(appUserObject(headers));

        List<ChatOneFullChatInfoDTO> allChats = new ArrayList<>();

        for (int i = 0; i < listOfAllTheChats.stream().count(); i++){

            List<ChatMember> members = chatMembersRepository
                    .findAllByChatId(listOfAllTheChats.get(i).getChatId());

            List<ChatListOfMembersDTO> allMembers = new ArrayList<>();

            for (int k = 0; k < members.stream().count(); k++){
                ChatListOfMembersDTO chatListOfMembersDTO = new ChatListOfMembersDTO(
                        members.get(k).getMemberId().getId(),
                        members.get(k).getMemberId().getUsername()
                );
                allMembers.add(chatListOfMembersDTO);
            }

            List<Message> messages = messagesRepository
                    .findAllByChatId(listOfAllTheChats.get(i).getChatId());

            List<ChatListOfMessagesDTO> allMessages = new ArrayList<>();

            for (int j = 0; j < messages.stream().count(); j++){
                ChatListOfMessagesDTO chatListOfMessagesDTO = new ChatListOfMessagesDTO(
                        messages.get(j).getId(),
                        messages.get(j).getAuthorId().getUsername(),
                        messages.get(j).getText(),
                        unixToDateConverter(messages.get(j).getSubmitted())
                );
                allMessages.add(chatListOfMessagesDTO);
            }

            ChatOneFullChatInfoDTO chatOneFullChatInfoDTO = new ChatOneFullChatInfoDTO(
                    listOfAllTheChats.get(i).getChatId().getId(),
                    listOfAllTheChats.get(i).getChatId().getOwnerId().getUsername(),
                    listOfAllTheChats.get(i).getChatId().getSubject(),
                    unixToDateConverter(listOfAllTheChats.get(i).getChatId().getLastMessageUploaded()),
                    unixToDateConverter(listOfAllTheChats.get(i).getLastViewed()),
                    lastRead(listOfAllTheChats.get(i).getChatId().getLastViewed(),
                            listOfAllTheChats.get(i).getChatId().getLastMessageUploaded()),
                    allMembers,
                    allMessages
            );
            allChats.add(chatOneFullChatInfoDTO);
        }

        return allChats;
    }

    @Override
    public ChatOneFullChatInfoDTO ListSpecificChat(HttpHeaders headers, Long id) {

        AppUser appUser = appUserObject(headers);

        if (chatRepository.count() < id){
            return null;
        }

        Chat chat = chatRepository.findById(id).get();

        if (!chatMembersRepository.existsByChatIdAndMemberId(chat, appUser)){

            return null;
        }

        ChatMember chatMember = chatMembersRepository.findByChatIdAndMemberId(chat, appUser);

        chatMember.setLastViewed(System.currentTimeMillis()/1000);
        chatMembersRepository.save(chatMember);

        List<ChatMember> members = chatMembersRepository.findAllByChatId(chat);

        List<ChatListOfMembersDTO> allMembers = new ArrayList<>();

        for (int k = 0; k < members.stream().count(); k++){
            ChatListOfMembersDTO chatListOfMembersDTO = new ChatListOfMembersDTO(
                    members.get(k).getMemberId().getId(),
                    members.get(k).getMemberId().getUsername()
            );
            allMembers.add(chatListOfMembersDTO);
        }

        List<Message> messages = messagesRepository.findAllByChatId(chat);

        List<ChatListOfMessagesDTO> allMessages = new ArrayList<>();

        for (int j = 0; j < messages.stream().count(); j++){
            ChatListOfMessagesDTO chatListOfMessagesDTO = new ChatListOfMessagesDTO(
                    messages.get(j).getId(),
                    messages.get(j).getAuthorId().getUsername(),
                    messages.get(j).getText(),
                    unixToDateConverter(messages.get(j).getSubmitted())
            );
            allMessages.add(chatListOfMessagesDTO);
        }

        ChatOneFullChatInfoDTO chatOneFullChatInfoDTO = new ChatOneFullChatInfoDTO(

          chat.getId(),
          chat.getOwnerId().getUsername(),
          chat.getSubject(),
          unixToDateConverter(chat.getLastMessageUploaded()),
          unixToDateConverter(chatMember.getLastViewed()),
          lastRead(chatMember.getLastViewed(), chat.getLastMessageUploaded()),
          allMembers,
          allMessages
        );

        return chatOneFullChatInfoDTO;
    }
}
