package com.gfa.straysfullstacktribes.services;

import com.gfa.straysfullstacktribes.exceptions.WrongConfirmationTokenException;
import com.gfa.straysfullstacktribes.models.dtos.RegistrationRequestDTO;

public interface RegistrationService {

    String register(RegistrationRequestDTO request);

    String confirmToken(String token) throws WrongConfirmationTokenException;

    String buildEmail(String name, String link);

}
