package server.repository.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class CharacterLocationDto {

    private int characterId;
    private double positionX;
    private double positionY;

}
