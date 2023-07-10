package dev.asror.botgame.utils.factory;

import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import dev.asror.botgame.utils.BaseUtils;
import org.springframework.stereotype.Component;

@Component
public class ReplyKeyboardMarkupFactory {
    public ReplyKeyboardMarkup mainMenu() {
        return getReplyMarkup(
                new KeyboardButton(BaseUtils.TIC_TAC_TOE),
                new KeyboardButton(BaseUtils.VS_AI)
        );
    }

    public ReplyKeyboardMarkup phoneNumberAndSkip() {
        KeyboardButton[] row1 = {new KeyboardButton(BaseUtils.PHONE_NUMBER).requestContact(true)};
        KeyboardButton[] row2 = {new KeyboardButton(BaseUtils.SKIP)};
        return getReplyMarkup(row1, row2);
    }

    private ReplyKeyboardMarkup getReplyMarkup(KeyboardButton ... buttons) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(buttons);
        replyKeyboardMarkup.selective(true);
        replyKeyboardMarkup.resizeKeyboard(true);
        return replyKeyboardMarkup;
    }
    private ReplyKeyboardMarkup getReplyMarkup(KeyboardButton[] ... rows) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(rows);
        replyKeyboardMarkup.selective(true);
        replyKeyboardMarkup.resizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup locationButton(String text) {
        KeyboardButton location = new KeyboardButton(text);
        location.requestLocation(true);

        return getReplyMarkup(location, new KeyboardButton(BaseUtils.MENU));
    }

    public ReplyKeyboardMarkup menuButton() {
        return getReplyMarkup(new KeyboardButton(BaseUtils.MENU));
    }
}
