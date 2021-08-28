package repository.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class CharacterDto {

    private int characterId;
    private int userAccountId;
    private String characterName;
    private int characterLevel;

}
