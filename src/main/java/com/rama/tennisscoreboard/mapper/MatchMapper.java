package com.rama.tennisscoreboard.mapper;

import com.rama.tennisscoreboard.dto.FinishedMatchDto;
import com.rama.tennisscoreboard.dto.MatchScoreViewDto;
import com.rama.tennisscoreboard.model.Match;
import com.rama.tennisscoreboard.model.MatchScore;
import com.rama.tennisscoreboard.model.PlayerScore;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "default")
public interface MatchMapper {
    @Mapping(target = "uuid", source = "uuid")
    @Mapping(target = "player1Id", source = "player1.id")
    @Mapping(target = "player2Id", source = "player2.id")
    @Mapping(target = "player1Name", source = "player1.name")
    @Mapping(target = "player2Name", source = "player2.name")

    @Mapping(target = "player1Sets", source = "playerScore.player1Sets")
    @Mapping(target = "player2Sets", source = "playerScore.player2Sets")
    @Mapping(target = "player1Games", source = "playerScore.player1Games")
    @Mapping(target = "player2Games", source = "playerScore.player2Games")
    @Mapping(target = "player1Points", source = "playerScore", qualifiedByName = "mapPlayer1Points")
    @Mapping(target = "player2Points", source = "playerScore", qualifiedByName = "mapPlayer2Points")
    MatchScoreViewDto toScoreDto(MatchScore matchScore);

    @Named("mapPlayer1Points")
    default String mapPlayer1Points(PlayerScore playerScore) {
        return playerScore.isTieBreak()
                ? String.valueOf(playerScore.getPlayer1TieBreakPoints())
                : playerScore.getPlayer1Points().getDisplayed();
    }

    @Named("mapPlayer2Points")
    default String mapPlayer2Points(PlayerScore playerScore) {
        return playerScore.isTieBreak()
                ? String.valueOf(playerScore.getPlayer2TieBreakPoints())
                : playerScore.getPlayer2Points().getDisplayed();
    }

    @Mapping(target = "player1Name", source = "player1.name")
    @Mapping(target = "player2Name", source = "player2.name")
    @Mapping(target = "winnerName", source = "winner.name")
    FinishedMatchDto toFinishedMatchDto(Match match);

    List<FinishedMatchDto> toFinishedMatchDtoList(List<Match> matches);
}
