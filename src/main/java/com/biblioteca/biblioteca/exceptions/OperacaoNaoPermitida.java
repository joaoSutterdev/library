package com.biblioteca.biblioteca.exceptions;

public class OperacaoNaoPermitida extends RuntimeException {
    public OperacaoNaoPermitida(String message){
        super(message);
    }
}
