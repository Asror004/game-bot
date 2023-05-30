package dev.asror.botgame.utils.factory;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import dev.asror.botgame.utils.BaseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Component
@RequiredArgsConstructor
public class InlineKeyboardFactory {

    public InlineKeyboardMarkup rateButtons() {
        return new InlineKeyboardMarkup(
                createButton("⭐", "1"),
                createButton("⭐", "2"),
                createButton("⭐", "3"),
                createButton("⭐", "4"),
                createButton("⭐", "5")
        );
    }

    public InlineKeyboardMarkup ticTacToeButtons(byte[][] board, String id){
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> row;
        StringJoiner sj;

        for (int i = 0; i < 3; i++) {
            row = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                sj = new StringJoiner("|");
                sj.add(String.valueOf(board[i][j]));
                sj.add(id);
                sj.add(String.valueOf(i));
                sj.add(String.valueOf(j));

                row.add(createButton(getChar(board[i][j]),
                        sj.toString()));
            }

            markup.addRow(row.get(0), row.get(1), row.get(2));
        }

        return markup;
    }

    public InlineKeyboardMarkup getFinishGameMarkup(InlineKeyboardMarkup markup, String id){
        return markup.addRow(createButton("O'yinni saqlash!", "save|"+id));
    }
    public InlineKeyboardButton getExitGameButton(String id){
        return createButton("O'yinni tugatish!", "exit|"+id);
    }

    private String getChar(byte n) {
        return switch (n){
            case 1 -> BaseUtils.BUTTON_0;
            case 2 -> BaseUtils.BUTTON_X;
            default -> "ㅤㅤㅤㅤ";
        };
    }

    public InlineKeyboardMarkup ticTacToeStartButtons(String id) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InlineKeyboardButton[] row;
        StringJoiner sj;

        for (int i = 0; i < 3; i++) {
            row = new InlineKeyboardButton[3];
            for (int j = 0; j < 3; j++) {
                sj = new StringJoiner("|");
                sj.add("0");
                sj.add(id);
                sj.add(String.valueOf(i));
                sj.add(String.valueOf(j));
                row[j] = createButton("ㅤㅤㅤㅤ", sj.toString());
            }

            markup.addRow(row);
        }

        return markup;
    }

    public InlineKeyboardMarkup startButton(String id) {
        return new InlineKeyboardMarkup(createButton("Boshlash", "start|"+id));
    }
    public InlineKeyboardMarkup send(String uuid) {
        InlineKeyboardButton button = new InlineKeyboardButton("Ulashish")
                .switchInlineQuery(uuid);
        return new InlineKeyboardMarkup(button);
    }
    private InlineKeyboardButton createButton(String text, String callback){
        InlineKeyboardButton button = new InlineKeyboardButton(text);
        button.callbackData(callback);
        return button;
    }
}
