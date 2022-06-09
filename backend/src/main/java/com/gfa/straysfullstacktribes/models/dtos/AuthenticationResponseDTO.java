package com.gfa.straysfullstacktribes.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
@Getter
@AllArgsConstructor
public class AuthenticationResponseDTO implements Serializable {
    private final String token;
}