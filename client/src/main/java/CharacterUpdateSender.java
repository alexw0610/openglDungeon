import engine.object.Player;
import protocol.dto.update.PlayerUpdateDto;
import udp.UpdateSender;

public class CharacterUpdateSender implements Runnable {

    private final Player player;
    private final UpdateSender updateSender;
    private final int connectionId;

    public CharacterUpdateSender(Player player, int connectionId, UpdateSender updateSender) {
        this.player = player;
        this.updateSender = updateSender;
        this.connectionId = connectionId;
    }

    @Override
    public void run() {
        while (true) {
            PlayerUpdateDto playerUpdateDto = PlayerUpdateDto.builder()
                    .positionX(this.player.getPosition().x())
                    .positionY(this.player.getPosition().y())
                    .realmId(1)
                    .zoneId(1)
                    .characterId(1)
                    .build();
            this.updateSender.sendUpdate(playerUpdateDto, this.connectionId);
            System.out.println("Sent update for ConnectionId: " + this.connectionId);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
