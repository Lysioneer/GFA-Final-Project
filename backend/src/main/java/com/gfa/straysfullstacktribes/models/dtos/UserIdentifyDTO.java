package com.gfa.straysfullstacktribes.models.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserIdentifyDTO {

    private String username;
    private List<KingdomIdentifyDTO> kingdoms;

    public UserIdentifyDTO(String username, List<KingdomIdentifyDTO> kingdoms) {
        this.username = username;
        this.kingdoms = new ArrayList<>();
    }

    public UserIdentifyDTO() {
        this.kingdoms = new ArrayList<>();
    }

    public void addKingdom(KingdomIdentifyDTO dto) {
        kingdoms.add(dto);
    }
}