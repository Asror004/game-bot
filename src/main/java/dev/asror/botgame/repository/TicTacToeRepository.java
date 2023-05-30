package dev.asror.botgame.repository;

import dev.asror.botgame.domain.TicTacToe;
import dev.asror.botgame.domain.UserDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface TicTacToeRepository extends JpaRepository<TicTacToe, String> {

}
