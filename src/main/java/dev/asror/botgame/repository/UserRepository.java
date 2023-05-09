package dev.asror.botgame.repository;

import dev.asror.botgame.domain.UserDomain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserDomain, Long> {

}
