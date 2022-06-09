package com.gfa.straysfullstacktribes.models;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ChatMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Chat chatId;

    @ManyToOne
    private AppUser memberId;

    private Long lastViewed;

    public ChatMember(Chat chatId,
                      AppUser memberId,
                      Long lastViewed) {
        this.chatId = chatId;
        this.memberId = memberId;
        this.lastViewed = lastViewed;
    }

    public void setLastViewed(Long lastViewed) {
        this.lastViewed = lastViewed;
    }

    public void setChatId(Chat chatId) {
        this.chatId = chatId;
    }

    public void setAppUserId(AppUser memberId) {
        this.memberId = memberId;
    }
}