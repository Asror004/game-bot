package dev.asror.botgame.utils.factory;

import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

@Component
public class SendMessageFactory {
    private final ReplyKeyboardMarkupFactory replyKeyboardMarkupFactory;

    public SendMessageFactory(ReplyKeyboardMarkupFactory replyKeyboardMarkupFactory) {
        this.replyKeyboardMarkupFactory = replyKeyboardMarkupFactory;
    }

    public EditMessageText getEditMessageTextForPassword(Long chatID, int messageID, String messageText) {
        return new EditMessageText(chatID, messageID, messageText);
    }

    public SendMessage sendMessageWithMainMenu(Long chatID, String messageText) {
        SendMessage sendMessage = new SendMessage(chatID, messageText);
        sendMessage.replyMarkup(replyKeyboardMarkupFactory.mainMenu());
        return sendMessage;
    }

    public SendMessage getSendMessageWithFileTypeKeyboard(Long chatID, String key, String language) {
        return new SendMessage(chatID, key);
    }


}
