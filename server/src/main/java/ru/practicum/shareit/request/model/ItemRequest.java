package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests", schema = "public")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = true, length = 60)
    String name;
    @Column(nullable = false, length = 200)
    String description;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "requestor_id", nullable = false)
    User requestor;
    @Column(nullable = false)
    LocalDateTime created;
}
