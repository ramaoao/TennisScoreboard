package com.rama.tennisscoreboard.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "matches")
public class Match {
    @Id
    @GeneratedValue
    Long id;

    @ManyToOne
    @JoinColumn(name = "player1", referencedColumnName = "id")
    Player player1;

    @ManyToOne
    @JoinColumn(name = "player2", referencedColumnName = "id")
    Player player2;

    @ManyToOne
    @JoinColumn(name = "winner", referencedColumnName = "id")
    Player winner;

    public Match(Player player1, Player player2, Player winner) {
        this.player1 = player1;
        this.player2 = player2;
        this.winner = winner;
    }
}