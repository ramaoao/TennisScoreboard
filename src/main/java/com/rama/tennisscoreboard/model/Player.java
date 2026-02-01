package com.rama.tennisscoreboard.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue
    Long id;

    @Column(name = "name", unique = true, nullable = false)
    String name;

    public Player(String name) {
        this.name = name;
    }
}
