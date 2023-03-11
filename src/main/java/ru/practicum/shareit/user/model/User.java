package ru.practicum.shareit.user.model;

import lombok.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class User {
    int id;
    String name;
    String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != user.id && email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email);
    }
}
