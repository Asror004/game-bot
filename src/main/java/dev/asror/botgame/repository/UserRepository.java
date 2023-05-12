package dev.asror.botgame.repository;

import dev.asror.botgame.domain.UserDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserDomain, Long> {
    @Query("select u.code from UserDomain u where u.chatId = ?1")
    Optional<String> findByChatId(Long chatId);

    @Query("select (count(u) > 0) from UserDomain u where u.code = ?1")
    boolean hasCode(String code);

}
