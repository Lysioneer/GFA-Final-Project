package com.gfa.straysfullstacktribes.models;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Chat chatId;

    @NotNull
    @Column(nullable = false)
    private String text;

    @ManyToOne
    private AppUser authorId;

    private Long submitted;

    public Message(Chat chatId,
                   String text,
                   AppUser authorId,
                   Long submitted) {
        this.chatId = chatId;
        this.text = text;
        this.authorId = authorId;
        this.submitted = submitted;
    }

    public void setSubmitted(Long submitted) {
        this.submitted = submitted;
    }

    public void setChatId(Chat chatId) {
        this.chatId = chatId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setAuthorId(AppUser authorId) {
        this.authorId = authorId;
    }
}