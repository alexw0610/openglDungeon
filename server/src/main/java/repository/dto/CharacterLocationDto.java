package repository.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class CharacterLocationDto {

    private int characterId;
    private double positionX;
    private double positionY;

}
