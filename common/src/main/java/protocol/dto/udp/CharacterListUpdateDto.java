package protocol.dto.udp;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CharacterListUpdateDto implements Serializable {
    private static final long serialVersionUID = -646395075823510354L;

    private List<CharacterUpdateDto> characterUpdateDtos;

    @Builder
    @Getter
    @Setter
    @ToString
    public static class CharacterUpdateDto implements Serializable {
        private static final long serialVersionUID = 4319241896788403774L;
        private int characterId;
        private double positionX;
        private double positionY;
    }
}



