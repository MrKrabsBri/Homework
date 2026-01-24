package com.krabs.Homework.exception;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.CLIENT)
public class DuplicateOrderDocumentException extends RuntimeException {
   // kaip pvz kuris fault message uzdeda :
private final String errorCode;

    public DuplicateOrderDocumentException(String status, String errorCode, String ErrorMessage) {
        super(ErrorMessage);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}