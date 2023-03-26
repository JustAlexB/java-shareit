package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Entity
@Table(name = "comments", schema = "public")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    @Column(nullable = false, length = 250)
    String text;
    @Column(name = "author_name")
    String authorName;
    LocalDateTime created;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "item_id")
    Item item;

    public Comment() {
    }
}