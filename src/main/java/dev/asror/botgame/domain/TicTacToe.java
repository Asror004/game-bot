package dev.asror.botgame.domain;

import dev.asror.botgame.state.Status;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    private long player1;
    private long player2;
    @Column(nullable = false)
    private String player1Name;
    @Column(nullable = false)
    private String player2Name;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    @Column(nullable = false)
    private LocalDateTime finishTime;

    @Builder(builderMethodName = "childBuilder")
    public TicTacToe(LocalDateTime createdAt, LocalDateTime updatedAt, boolean deleted, String id, long player1, long player2, String player1Name, String player2Name, Status status, LocalDateTime finishTime) {
        super(createdAt, updatedAt, deleted);
        this.id = id;
        this.board = new byte[3][3];
        this.player1 = player1;
        this.player2 = player2;
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.status = status;
        this.finishTime = finishTime;
    }
}
