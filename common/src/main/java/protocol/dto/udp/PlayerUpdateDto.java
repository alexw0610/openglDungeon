package protocol.dto.udp;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Builder
@Getter
@Setter
@ToString
public class PlayerUpdateDto implements Serializable {

    private static final long serialVersionUID = 2562015778387365953L;
    private int userId;
    private int characterId;
    private double positionX;
    private double positionY;
    private int realmId;
    private int zoneId;

}
