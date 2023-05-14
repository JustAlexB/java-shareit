package ru.practicum.shareit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Transactional
    @Modifying
    @Query("update User u set u.email = ?1 where u.id = ?2")
    void updateEmail(String email, Long id);

    @Transactional
    @Modifying
    @Query("update User u set u.name = ?1 where u.id = ?2")
    void updateName(String name, Long id);

    @Query("select u from User u " +
            "where upper(u.email) like upper(concat('%', ?1, '%'))")
    List<User> findUserByEmail(String text);
}
