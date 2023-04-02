package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Entity
@Table(name = "items", schema = "public")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false, length = 60)
    String name;
    @Column(nullable = false, length = 200)
    String description;
    boolean available;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    User owner;
    @Column(name = "request_id")
    Long requestID;

    public Item() {
    }
}
