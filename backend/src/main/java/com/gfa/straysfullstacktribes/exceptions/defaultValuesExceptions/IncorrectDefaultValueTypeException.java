package com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions;

public class IncorrectDefaultValueTypeException extends Exception {

    public IncorrectDefaultValueTypeException(){
        super("the value have a different data type");
    }
}
