package dev.asror.botgame.domain;

import dev.asror.botgame.state.Status;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TicTacToe extends Auditable{
    @Id
    private String id;
    private byte[][] board;
    private Long player1;
    private Long player2;
    private Status status;

    @Builder(builderMethodName = "childBuilder")
    public TicTacToe(LocalDateTime createdAt, LocalDateTime updatedAt, boolean deleted, String id, Long player1, Long player2, Status status) {
        super(createdAt, updatedAt, deleted);
        this.id = id;
        this.board = new byte[3][3];
        this.player1 = player1;
        this.player2 = player2;
        this.status = status;
    }
}
