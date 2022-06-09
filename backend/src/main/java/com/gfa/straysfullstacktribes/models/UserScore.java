package com.gfa.straysfullstacktribes.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserScore {

    private String userName;
    private int KingdomAmount;
    private int score;
}
