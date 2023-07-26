package dev.asror.botgame.processors.callback;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.EditMessageText;
import dev.asror.botgame.config.TelegramBotConfiguration;
import dev.asror.botgame.domain.TicTacToe;
import dev.asror.botgame.dtos.FinishGameDto;
import dev.asror.botgame.dtos.FinishGameDtoWithAI;
import dev.asror.botgame.processors.Processor;
import dev.asror.botgame.service.AIService;
import dev.asror.botgame.service.TicTacToeService;
import dev.asror.botgame.service.UserService;
import dev.asror.botgame.state.State;
import dev.asror.botgame.state.Status;
import dev.asror.botgame.state.TicTacToeState;
import dev.asror.botgame.state.TicTacToeVsAI;
import dev.asror.botgame.utils.BaseUtils;
import dev.asror.botgame.utils.factory.InlineKeyboardFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class TicTacToeWithAICallbackProcessor implements Processor<TicTacToeVsAI> {
    private final TelegramBot bot = TelegramBotConfiguration.get();
    private final UserService userService;
    private final Map<Long, State> userState;
    private final InlineKeyboardFactory inlineKeyboardFactory;
    private final Map<String, TicTacToe> ticTacToes;
    private final Map<String, TicTacToeVsAI> ticTacToeWithAIState;
    private final Map<String, Boolean> currentPlayer;
    private final TicTacToeService ticTacToeService;
    private final AIService AIService;

    @Override
    public void process(Update update, TicTacToeVsAI state) {
        CallbackQuery callbackQuery = update.callbackQuery();
        Message message = callbackQuery.message();
        String data = callbackQuery.data();
        String ticTacToeId = data.split("\\|")[1];

        User chat = callbackQuery.from();

        long chatId = Objects.nonNull(message) ? message.chat().id() : chat.id();

        if (data.startsWith("start")) {
            if (ticTacToeWithAIState.get(ticTacToeId) == null) {
                sendAlert("Bu o'yin tugagan yoki mavjud emas!", callbackQuery.id());
                return;
            }

           if (state.equals(TicTacToeVsAI.START)) {
                startGame(ticTacToeId, message);
           }
        }
        else if (data.startsWith("save")) {
            TicTacToe ticTacToe = ticTacToes.get(ticTacToeId);
            if (!ticTacToe.getStatus().equals(Status.FINISH)) {
                CompletableFuture.runAsync(() -> sendAlert("O'yin tugamagan!", callbackQuery.id()));
                return;
            }
            if (Objects.nonNull(ticTacToeService.findById(ticTacToeId))) {
                sendAlert("O'yinni allaqachon saqlagansiz!", callbackQuery.id());
                return;
            }
            sendAlert("Muvaffiqiyatli saqlandi!", callbackQuery.id());
            ticTacToeService.save(ticTacToe);
        }
        else if (data.startsWith("exit")) {
            finishGameStop(new FinishGameDtoWithAI(ticTacToes.get(ticTacToeId), callbackQuery,
                    chatId));
        }
        else {
            TicTacToe ticTacToe = ticTacToes.get(ticTacToeId);
            if (ticTacToe.getStatus().equals(Status.FINISH)) {
                CompletableFuture.runAsync(() ->
                        sendAlert("O'yin tugagan yoki to'xtatilgan!", callbackQuery.id()));
                return;
            }

            boolean current = currentPlayer.get(ticTacToeId);
            if (!current && ticTacToe.getPlayer1() == chatId) {
                CompletableFuture.runAsync(() ->
                        sendAlert("Sizning navbatingiz emas!", callbackQuery.id()));
                return;
            }

            String[] split = data.split("\\|");

            byte[][] board = ticTacToes.get(ticTacToeId).getBoard();
            int i = Integer.parseInt(split[2]);
            int j = Integer.parseInt(split[3]);
            if (board[i][j] != 0) {
                CompletableFuture.runAsync(() ->
                        sendAlert("Xato yurish!", callbackQuery.id()));
                return;
            }

            currentPlayer.put(ticTacToeId, !current);

            board[i][j] = (byte) 2;

            String player1Name = ticTacToe.getPlayer1Name();
            EditMessageText editMessageText =
                    new EditMessageText(chatId, message.messageId(), BaseUtils.getGameString(
                            player1Name, "AI", "AI"
                    )+"\nAI o'ylamoqda...");

            editMessageText.replyMarkup(inlineKeyboardFactory
                    .ticTacToeButtons(board, ticTacToeId)
                    .addRow(inlineKeyboardFactory.getExitGameButton(ticTacToeId)));

            bot.execute(editMessageText);

            FinishGameDtoWithAI dto = new FinishGameDtoWithAI(ticTacToe, callbackQuery, chatId);
            finishGameWin(dto, 2);
            finishGameFull(dto);

            gameAI(ticTacToe, callbackQuery);
        }

    }

    private void gameAI(TicTacToe ticTacToe, CallbackQuery callbackQuery) {
        try {
            Thread.sleep(new Random().nextInt(1000, 4000));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        String ticTacToeId = ticTacToe.getId();
        Message message = callbackQuery.message();
        byte[][] bestWay = AIService.findBestWay(ticTacToeId);

        String player1Name = ticTacToe.getPlayer1Name();
        EditMessageText editMessageText =
                new EditMessageText(message.chat().id(), message.messageId(), BaseUtils.getGameString(
                         player1Name,"AI", player1Name
                ));
        editMessageText.replyMarkup(inlineKeyboardFactory
                .ticTacToeButtons(bestWay, ticTacToeId)
                .addRow(inlineKeyboardFactory.getExitGameButton(ticTacToeId)));

        if (!ticTacToe.getStatus().equals(Status.FINISH)) {
            bot.execute(editMessageText);

            boolean current = currentPlayer.get(ticTacToeId);
            currentPlayer.put(ticTacToeId, !current);

            FinishGameDtoWithAI dto = new FinishGameDtoWithAI(ticTacToe, callbackQuery, 0);
            finishGameWin(dto, 1);
        }
    }

    private void finishGameWin(FinishGameDtoWithAI dto, int i) {
        CompletableFuture.runAsync(() -> {
            TicTacToe ticTacToe = dto.ticTacToe();
            byte[][] board = ticTacToe.getBoard();
            String ticTacToeId = ticTacToe.getId();

            if (hasWon(i, ticTacToeId)) {

                if (ticTacToe.getStatus().equals(Status.FINISH)) {
                    return;
                }

                CallbackQuery callbackQuery = dto.callbackQuery();
                long chatId = dto.chatId();

                ticTacToe.setStatus(Status.FINISH);
                sendAlert("Siz %s!\nO'yinni saqlashga 10 daqiqa vaqtingiz bor!".formatted(
                        (chatId != 0) ? "yutdingiz" : "yutqazdingiz"
                ), callbackQuery.id());

                String text = """
                        O'yin tugadi!

                        %s go'lib bo'ldi!

                        Birinchi o'yinchi: %s
                        Ikkinchi o'yinchi: AI
                        """.formatted((chatId != 0) ? ticTacToe.getPlayer1Name() : "AI",
                        ticTacToe.getPlayer1Name());

                EditMessageText winMessageText = new EditMessageText(callbackQuery.message().chat().id(),
                        callbackQuery.message().messageId(), text);
                InlineKeyboardMarkup markup = inlineKeyboardFactory.ticTacToeButtons(ticTacToes.get(ticTacToeId).getBoard(),
                        ticTacToeId);

                winMessageText.replyMarkup(inlineKeyboardFactory.getFinishGameMarkup(markup, ticTacToeId));
                bot.execute(winMessageText);

                ticTacToe.setFinishTime(LocalDateTime.now());
                ticTacToe.setWinPlayer(dto.chatId());
                currentPlayer.remove(ticTacToeId);
            }
        });
    }

    private void finishGameFull(FinishGameDtoWithAI dto) {
        CompletableFuture.runAsync(() -> {
            TicTacToe ticTacToe = dto.ticTacToe();
            String ticTacToeId = ticTacToe.getId();

            if (isBoardFull(ticTacToeId)) {
                ticTacToe.setStatus(Status.FINISH);
                CallbackQuery callbackQuery = dto.callbackQuery();
                sendAlert("O'yin tugadi!\nO'yinni saqlashga 10 daqiqa vaqtingiz bor!", callbackQuery.id());

                String text = """
                        O'yin tugadi!

                        Hech kim yutmadi!

                        Birinchi o'yinchi: %s
                        Ikkinchi o'yinchi: %s
                        """.formatted(ticTacToe.getPlayer1Name(), ticTacToe.getPlayer2Name());

                EditMessageText finishMessageText = new EditMessageText(callbackQuery.message().chat().id(),
                        callbackQuery.message().messageId(), text);
                InlineKeyboardMarkup markup = inlineKeyboardFactory.ticTacToeButtons(ticTacToes.get(ticTacToeId).getBoard(),
                        ticTacToeId);
                finishMessageText.replyMarkup(inlineKeyboardFactory.getFinishGameMarkup(markup, ticTacToeId));
                bot.execute(finishMessageText);

                ticTacToe.setFinishTime(LocalDateTime.now());
                currentPlayer.remove(ticTacToeId);
            }
        });
    }
//
    private void finishGameStop(FinishGameDtoWithAI dto) {
        CompletableFuture.runAsync(() -> {

            TicTacToe ticTacToe = dto.ticTacToe();
            String ticTacToeId = ticTacToe.getId();

            CallbackQuery callbackQuery = dto.callbackQuery();

            if (!(ticTacToe.getPlayer1() == dto.chatId() || ticTacToe.getPlayer2() == dto.chatId())) {
                CompletableFuture.runAsync(() ->
                {
                    sendAlert("Siz o'yinda emassiz!", callbackQuery.id());
                });
                return;
            }

            ticTacToe.setStatus(Status.FINISH);
            sendAlert("O'yin to'xtatildi!", callbackQuery.id());

            String text = """
                    O'yin to'xtatildi!

                    Birinchi o'yinchi: %s
                    Ikkinchi o'yinchi: %s
                    """.formatted(ticTacToe.getPlayer1Name(), ticTacToe.getPlayer2Name());

            EditMessageText finishMessageText = new EditMessageText(callbackQuery.message().chat().id(),
                    callbackQuery.message().messageId(), text);
            InlineKeyboardMarkup markup = inlineKeyboardFactory.ticTacToeButtons(ticTacToes.get(ticTacToeId).getBoard(),
                    ticTacToeId);
            finishMessageText.replyMarkup(markup);
            bot.execute(finishMessageText);

            ticTacToe.setFinishTime(LocalDateTime.now());
            currentPlayer.remove(ticTacToeId);
        });
    }

    private void startGame(String ticTacToeId, Message message) {
        CompletableFuture.runAsync(() -> started(message, ticTacToeId));

        String user1 = message.chat().firstName();

        String text = """
                O'yin boshlandi!
                                
                Birinchi o'yinchi: %s
                Ikkinchi o'yinchi: AI
                                
                Yurish: %s da
                """.formatted(user1, user1);

        EditMessageText editMessageText =
                new EditMessageText(message.chat().id(), message.messageId(), text);
        editMessageText.replyMarkup(inlineKeyboardFactory
                .ticTacToeStartButtons(ticTacToeId)
                .addRow(inlineKeyboardFactory.getExitGameButton(ticTacToeId)));

        bot.execute(editMessageText);


    }

    private boolean hasWon(int player, String ticTacToeId) {
        byte[][] board = ticTacToes.get(ticTacToeId).getBoard();

        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player)
                return true;
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player)
                return true;
        }

        if (board[0][0] == player && board[1][1] == player && board[2][2] == player)
            return true;
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player)
            return true;

        return false;
    }

    public void sendAlert(String text, String callBackQueryId) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery(callBackQueryId);
        answerCallbackQuery.text(text);
        answerCallbackQuery.showAlert(true);
        bot.execute(answerCallbackQuery);
    }

    private boolean isBoardFull(String ticTacToeId) {
        byte[][] board = ticTacToes.get(ticTacToeId).getBoard();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    return false;
                }
            }
        }
//        System.out.println(Arrays.deepToString(board) +"  full");
        return true;
    }

    private void started(Message message, String ticTacToeId) {
        currentPlayer.put(ticTacToeId, true);

        long player = message.chat().id();

        ticTacToeWithAIState.put(ticTacToeId, TicTacToeVsAI.PLAY);

        String fullName = message.chat().firstName();

        ticTacToes.put(ticTacToeId, TicTacToe.childBuilder()
                .id(ticTacToeId)
                .player1(player)
                .player2(0)
                .player1Name(fullName)
                .player2Name("AI")
                .createdAt(LocalDateTime.now())
                .status(Status.PLAY).build());
    }
//
//    private void saveUser(User chat, String ticTacToeId) {
//        Long chatId = chat.id();
//        ticTacToeState.get(ticTacToeId).put(chatId, TicTacToeState.START);
//        userService.save(chatId, BaseUtils.getFullName(chat.firstName(), chat.lastName()));
//    }
}
