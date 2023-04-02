package ru.practicum.shareit.storage;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.IncorrectParameterException;
import ru.practicum.shareit.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

@Primary
@Component
public class DbUserStorage implements Storage<User> {
    protected final UserRepository userStorage;

    public DbUserStorage(UserRepository userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Collection<User> getAll() {
        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        return userStorage.findAll(sortById);
    }

    @Override
    public User create(User user) {
        validation(user);
        return userStorage.save(user);
    }

    @Override
    public User update(User user) {
        if (user.getEmail() == null && user.getName() != null) {
            userStorage.updateName(user.getName(), user.getId());
        } else if (user.getEmail() != null && user.getName() == null) {
            userStorage.updateEmail(user.getEmail(), user.getId());
        } else {
            validation(user);
            return userStorage.save(user);
        }
        return userStorage.findById(user.getId()).get();
    }

    @Override
    public Optional<User> getByID(Long userID) {
        return userStorage.findById(userID);
    }

    @Override
    public Optional<User> delByID(Long userID) {
        userStorage.deleteById(userID);
        return Optional.empty();
    }

    @Override
    public void validation(User user) {
        if (user.getEmail() == null) {
            throw new IncorrectParameterException("email");
        }
    }
}
