package dev.asror.botgame.service;

import dev.asror.botgame.domain.TicTacToe;
import dev.asror.botgame.utils.ai.AiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIService {


    public final Map<String, Boolean> currentPlayer;
    public final Map<String, TicTacToe> ticTacToes;

    public byte[][] findBestWay(String boardId) {
        TicTacToe ticTacToe = ticTacToes.get(boardId);
        byte[][] board = ticTacToe.getBoard();
        AiUtil.Move bestMove = AiUtil.findBestMove(board);
        board[bestMove.row][bestMove.col] = 2;
        return board;
    }


}



