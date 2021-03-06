package com.bedtimes.sargegmbot.callback.controller;

import com.bedtimes.sargegmbot.callback.CallbackData;
import com.bedtimes.sargegmbot.messenger.service.MessageParserService;
import com.bedtimes.sargegmbot.messenger.service.MessageSenderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CallbackController {
    @Value("${groupme.bot.name}")
    private String BOT_NAME;

    final MessageSenderService messageSenderService;
    final MessageParserService messageParserService;

    public CallbackController(MessageSenderService messageSenderService, MessageParserService messageParserService) {
        this.messageSenderService = messageSenderService;
        this.messageParserService = messageParserService;
    }

    @PostMapping("/callback")
    public void callback(@RequestBody CallbackData callbackData) {
        String senderName = callbackData.getName();

        // Check that whoever sent the message isn't us (the bot)
        if (!senderName.equals(BOT_NAME)) {
            // For debugging purposes
            System.out.println("From " + senderName + " to " + BOT_NAME);

            String sentMessageText = callbackData.getText();
            String msg = messageParserService.parseMessage(sentMessageText);

            ResponseEntity<String> response = messageSenderService.sendTextMessage(callbackData.getText() /* msg will go in here later */);

            if (response.getStatusCode() != HttpStatus.ACCEPTED) {
                System.out.println("Following message failed to send: " + callbackData.getText());
            }
        }
    }
}
