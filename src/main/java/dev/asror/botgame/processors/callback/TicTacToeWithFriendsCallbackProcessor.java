package dev.asror.botgame.processors.callback;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import dev.asror.botgame.config.TelegramBotConfiguration;
import dev.asror.botgame.domain.TicTacToe;
import dev.asror.botgame.domain.UserDomain;
import dev.asror.botgame.dtos.FinishGameDto;
import dev.asror.botgame.processors.Processor;
import dev.asror.botgame.repository.UserRepository;
import dev.asror.botgame.service.TicTacToeService;
import dev.asror.botgame.service.UserService;
import dev.asror.botgame.state.State;
import dev.asror.botgame.state.Status;
import dev.asror.botgame.state.TicTacToeState;
import dev.asror.botgame.utils.BaseUtils;
import dev.asror.botgame.utils.factory.InlineKeyboardFactory;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class TicTacToeWithFriendsCallbackProcessor implements Processor<TicTacToeState> {
    private final TelegramBot bot = TelegramBotConfiguration.get();
    private final UserService userService;
    private final Map<Long, State> userState;
    private final InlineKeyboardFactory inlineKeyboardFactory;
    private final Map<String, TicTacToe> ticTacToes;
    private final Map<String, Map<Long, TicTacToeState>> ticTacToeState;
    private final Map<String, Boolean> currentPlayer;
    private final TicTacToeService ticTacToeService;

    @Override
    public void process(Update update, TicTacToeState state) {
        CallbackQuery callbackQuery = update.callbackQuery();
        Message message = callbackQuery.message();
        String data = callbackQuery.data();
        String ticTacToeId = data.split("\\|")[1];
        String inlineMessageId = callbackQuery.inlineMessageId();

        User chat = callbackQuery.from();

        long chatId = Objects.nonNull(message) ? message.chat().id() : chat.id();

        if (data.startsWith("start")) {
            if (ticTacToeState.get(ticTacToeId) == null) {
                sendAlert("Bu o'yin tugagan yoki mavjud emas!", callbackQuery.id());
                return;
            }

            if (Objects.isNull(userState.get(chatId)))
                saveUser(chat, ticTacToeId);

            if (state == null) {
                CompletableFuture.runAsync(() -> startGame(inlineMessageId, ticTacToeId, chat));

                started(chat, ticTacToeId);
            } else if (state.equals(TicTacToeState.SEND)) {
                AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery(callbackQuery.id());
                answerCallbackQuery.text("Sherigingiz start bosishini kuting!");
                answerCallbackQuery.showAlert(true);
                bot.execute(answerCallbackQuery);
            }
        } else if (data.startsWith("save")) {
            TicTacToe ticTacToe = ticTacToes.get(ticTacToeId);
            if (!ticTacToe.getStatus().equals(Status.FINISH)) {
                CompletableFuture.runAsync(() -> sendAlert("O'yin tugamagan!", callbackQuery.id()));
                return;
            }
            if (Objects.nonNull(ticTacToeService.findById(ticTacToeId))) {
                sendAlert("O'yinni allaqachon o'zingiz yoki sherigingiz saqlagan!", callbackQuery.id());
                return;
            }
            sendAlert("Muvaffiqiyatli saqlandi!", callbackQuery.id());
            ticTacToeService.save(ticTacToe);
        } else if (data.startsWith("exit")) {
            finishGameStop(new FinishGameDto(ticTacToes.get(ticTacToeId), callbackQuery.id(),
                    callbackQuery.inlineMessageId(), chatId));
        } else {
            TicTacToe ticTacToe = ticTacToes.get(ticTacToeId);
            if (ticTacToe.getStatus().equals(Status.FINISH)) {
                CompletableFuture.runAsync(() ->
                        sendAlert("O'yin tugagan yoki to'xtatilgan!", callbackQuery.id()));
                return;
            }

            if (!(ticTacToe.getPlayer1() == chatId || ticTacToe.getPlayer2() == chatId)) {
                CompletableFuture.runAsync(() ->
                        sendAlert("Siz o'yinda emassiz!", callbackQuery.id()));
                return;
            }

            boolean current = currentPlayer.get(ticTacToeId);
            if (current && ticTacToe.getPlayer2() == chatId) {
                CompletableFuture.runAsync(() ->
                        sendAlert("Sizning navbatingiz emas!", callbackQuery.id()));
                return;
            } else if (!current && ticTacToe.getPlayer1() == chatId) {
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

            board[i][j] = (byte) (current ? 2 : 1);

            String player1Name = ticTacToe.getPlayer1Name();
            String player2Name = ticTacToe.getPlayer2Name();
            EditMessageText editMessageText =
                    new EditMessageText(inlineMessageId, BaseUtils.getGameString(
                            player1Name, player2Name, (current ? player2Name : player1Name)
                    ));

            editMessageText.replyMarkup(inlineKeyboardFactory
                    .ticTacToeButtons(board, ticTacToeId)
                    .addRow(inlineKeyboardFactory.getExitGameButton(ticTacToeId)));

            bot.execute(editMessageText);

            FinishGameDto dto = new FinishGameDto(ticTacToe, callbackQuery.id(), inlineMessageId, chatId);
            finishGameWin(dto, i, j);
            finishGameFull(dto);
        }

    }

    private void finishGameWin(FinishGameDto dto, int i, int j) {
        CompletableFuture.runAsync(() -> {
            TicTacToe ticTacToe = dto.ticTacToe();
            byte[][] board = ticTacToe.getBoard();
            String ticTacToeId = ticTacToe.getId();

            if (hasWon(board[i][j], ticTacToeId)) {
                if (ticTacToe.getStatus().equals(Status.FINISH)) {
                    return;
                }
                ticTacToe.setStatus(Status.FINISH);
                sendAlert("Siz yutdingiz!\nO'yinni saqlashga 10 daqiqa vaqtingiz bor!", dto.callBackQueryId());

                String winPlayer = ticTacToe.getPlayer1() == dto.chatId() ? ticTacToe.getPlayer1Name() : ticTacToe.getPlayer2Name();

                String text = """
                        O'yin tugadi!
                                                    
                        %s go'lib bo'ldi!
                                                    
                        Birinchi o'yinchi: %s
                        Ikkinchi o'yinchi: %s
                        """.formatted(winPlayer, ticTacToe.getPlayer1Name(), ticTacToe.getPlayer2Name());

                EditMessageText winMessageText = new EditMessageText(dto.inlineMessageId(), text);
                InlineKeyboardMarkup markup = inlineKeyboardFactory.ticTacToeButtons(ticTacToes.get(ticTacToeId).getBoard(),
                        ticTacToeId);
                winMessageText.replyMarkup(inlineKeyboardFactory.getFinishGameMarkup(markup, ticTacToeId));
                bot.execute(winMessageText);

                Map<Long, TicTacToeState> stateMap = ticTacToeState.get(ticTacToeId);
                stateMap.put(ticTacToe.getPlayer1(), null);
                stateMap.put(ticTacToe.getPlayer1(), null);

                ticTacToe.setFinishTime(LocalDateTime.now());
                ticTacToe.setWinPlayer(dto.chatId());
                currentPlayer.remove(ticTacToeId);
            }
        });
    }

    private void finishGameStop(FinishGameDto dto) {
        CompletableFuture.runAsync(() -> {

            TicTacToe ticTacToe = dto.ticTacToe();
            String ticTacToeId = ticTacToe.getId();

            if (!(ticTacToe.getPlayer1() == dto.chatId() || ticTacToe.getPlayer2() == dto.chatId())) {
                CompletableFuture.runAsync(() ->
                        sendAlert("Siz o'yinda emassiz!", dto.callBackQueryId()));
                return;
            }

            ticTacToe.setStatus(Status.FINISH);
            sendAlert("O'yin to'xtatildi!", dto.callBackQueryId());

            String text = """
                    O'yin to'xtatildi!
                                                                                                    
                    Birinchi o'yinchi: %s
                    Ikkinchi o'yinchi: %s
                    """.formatted(ticTacToe.getPlayer1Name(), ticTacToe.getPlayer2Name());

            EditMessageText finishMessageText = new EditMessageText(dto.inlineMessageId(), text);
            InlineKeyboardMarkup markup = inlineKeyboardFactory.ticTacToeButtons(ticTacToes.get(ticTacToeId).getBoard(),
                    ticTacToeId);
            finishMessageText.replyMarkup(markup);
            bot.execute(finishMessageText);

            Map<Long, TicTacToeState> stateMap = ticTacToeState.get(ticTacToeId);
            stateMap.put(ticTacToe.getPlayer1(), null);
            stateMap.put(ticTacToe.getPlayer1(), null);

            ticTacToe.setFinishTime(LocalDateTime.now());
            currentPlayer.remove(ticTacToeId);
        });
    }

    private void finishGameFull(FinishGameDto dto) {
        CompletableFuture.runAsync(() -> {
            TicTacToe ticTacToe = dto.ticTacToe();
            String ticTacToeId = ticTacToe.getId();

            if (isBoardFull(ticTacToeId)) {
                ticTacToe.setStatus(Status.FINISH);
                sendAlert("O'yin tugadi!\nO'yinni saqlashga 10 daqiqa vaqtingiz bor!", dto.callBackQueryId());

                String text = """
                        O'yin tugadi!
                                                    
                        Hech kim yutmadi!
                                                    
                        Birinchi o'yinchi: %s
                        Ikkinchi o'yinchi: %s
                        """.formatted(ticTacToe.getPlayer1Name(), ticTacToe.getPlayer2Name());

                EditMessageText finishMessageText = new EditMessageText(dto.inlineMessageId(), text);
                InlineKeyboardMarkup markup = inlineKeyboardFactory.ticTacToeButtons(ticTacToes.get(ticTacToeId).getBoard(),
                        ticTacToeId);
                finishMessageText.replyMarkup(inlineKeyboardFactory.getFinishGameMarkup(markup, ticTacToeId));
                bot.execute(finishMessageText);

                Map<Long, TicTacToeState> stateMap = ticTacToeState.get(ticTacToeId);
                stateMap.put(ticTacToe.getPlayer1(), null);
                stateMap.put(ticTacToe.getPlayer1(), null);

                ticTacToe.setFinishTime(LocalDateTime.now());
                currentPlayer.remove(ticTacToeId);
            }
        });
    }

    private void startGame(String inlineMessageId, String ticTacToeId, User chat) {
        long player1 = ticTacToeState.get(ticTacToeId).keySet()
                .stream().filter(ch -> !ch.equals(chat.id())).findFirst().orElseThrow();

        String user1 = userService.findById(player1).orElseThrow().getFullName();
        String user2 = chat.firstName();

        String text = """
                O'yin boshlandi!
                                
                Birinchi o'yinchi: %s
                Ikkinchi o'yinchi: %s
                                
                Yurish: %s da
                """.formatted(user1, user2, user1);

        EditMessageText editMessageText =
                new EditMessageText(inlineMessageId, text);
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
        return true;
    }

    private void started(User chat, String ticTacToeId) {
        Long chatId = chat.id();

        currentPlayer.put(ticTacToeId, true);

        long player1 = ticTacToeState.get(ticTacToeId).keySet()
                .stream().filter(ch -> !ch.equals(chatId)).findFirst().orElseThrow();

        Map<Long, TicTacToeState> ticTaToeUserState = ticTacToeState.get(ticTacToeId);
        ticTaToeUserState.put(chatId, TicTacToeState.PLAY);
        ticTaToeUserState.put(player1, TicTacToeState.PLAY);

        String fullName1 = userService.findById(player1).orElseThrow().getFullName();
        String fullName2 = chat.firstName();

        ticTacToes.put(ticTacToeId, TicTacToe.childBuilder()
                .id(ticTacToeId)
                .player1(player1)
                .player2(chatId)
                .player1Name(fullName1)
                .player2Name(fullName2)
                .createdAt(LocalDateTime.now())
                .status(Status.PLAY).build());
    }

    private void saveUser(User chat, String ticTacToeId) {
        Long chatId = chat.id();
        ticTacToeState.get(ticTacToeId).put(chatId, TicTacToeState.START);
        userService.save(chatId, BaseUtils.getFullName(chat.firstName(), chat.lastName()));
    }
}
