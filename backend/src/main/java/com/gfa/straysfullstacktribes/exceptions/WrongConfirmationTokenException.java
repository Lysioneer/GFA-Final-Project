package com.gfa.straysfullstacktribes.exceptions;

public class WrongConfirmationTokenException extends Exception {
    public WrongConfirmationTokenException() {
        super("Wrong confirmation token sent");
    }
}
