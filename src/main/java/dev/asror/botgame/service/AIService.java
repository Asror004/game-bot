package dev.asror.botgame.service;

import dev.asror.botgame.domain.TicTacToe;
import dev.asror.botgame.utils.ai.AiUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIService {


    private final Map<String, Boolean> currentPlayer;
    private final Map<String, TicTacToe> ticTacToes;

    public byte[][] findBestWay(String boardId) {
        TicTacToe ticTacToe = ticTacToes.get(boardId);
        byte[][] board = ticTacToe.getBoard();

//        System.out.println(Arrays.deepToString(board) +" kirdi");

        AiUtil.Move bestMove = AiUtil.findBestMove(board);

        if (bestMove.row == -1){
            return board;
        }

        board[bestMove.row][bestMove.col] = 1;

        return board;
    }


}



