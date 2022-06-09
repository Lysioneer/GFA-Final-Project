package com.gfa.straysfullstacktribes.models;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String subject;

    @ManyToOne
    private AppUser ownerId;

    private Long lastMessageUploaded;

    private Long lastViewed;

    public Chat(String subject,
                AppUser ownerId, Long lastMessageUploaded,
                Long lastViewed) {
        this.subject = subject;
        this.ownerId = ownerId;
        this.lastMessageUploaded = lastMessageUploaded;
        this.lastViewed = lastViewed;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setOwner(AppUser ownerId) {
        this.ownerId = ownerId;
    }

    public void setLastMessageUploaded(Long lastMessageUploaded) {
        this.lastMessageUploaded = lastMessageUploaded;
    }

    public void setLastViewed(Long lastViewed) {
        this.lastViewed = lastViewed;
    }
}