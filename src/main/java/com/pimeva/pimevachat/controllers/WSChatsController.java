package com.pimeva.pimevachat.controllers;

import com.pimeva.pimevachat.modelos.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class WSChatsController {

    @MessageMapping("/chat1")
    public void getMessage(Message message){
        System.out.println("El mensaje recibido es: "+message);
    }
}
