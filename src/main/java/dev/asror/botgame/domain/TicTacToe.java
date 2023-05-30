package dev.asror.botgame.domain;

import dev.asror.botgame.state.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class TicTacToe extends Auditable{
    @Id
    private String id;
    @Transient
    private byte[][] board;
    private String boardDefinition;
    private long player1;
    private long player2;
    private long winPlayer;
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
    public TicTacToe(LocalDateTime createdAt, LocalDateTime updatedAt, boolean deleted, String id, long player1, long player2, long winPlayer, String player1Name, String player2Name, Status status, LocalDateTime finishTime) {
        super(createdAt, updatedAt, deleted);
        this.id = id;
        this.board = new byte[3][3];
        this.player1 = player1;
        this.player2 = player2;
        this.winPlayer = winPlayer;
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.status = status;
        this.finishTime = finishTime;
    }
}
