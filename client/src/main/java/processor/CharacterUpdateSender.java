package processor;

import dto.udp.PlayerUpdateDto;
import engine.component.TransformationComponent;
import engine.entity.Entity;
import exception.UDPServerException;
import udp.UpdateSender;

public class CharacterUpdateSender implements Runnable {

    private final Entity player;
    private final UpdateSender updateSender;
    private final int connectionId;

    public CharacterUpdateSender(Entity player, int connectionId, UpdateSender updateSender) {
        this.player = player;
        this.updateSender = updateSender;
        this.connectionId = connectionId;
    }

    @Override
    public void run() {
        //TODO Scheduled task executor
        while (true) {
            PlayerUpdateDto playerUpdateDto = PlayerUpdateDto.builder()
                    .positionX(this.player.getComponentOfType(TransformationComponent.class).getPositionX())
                    .positionY(this.player.getComponentOfType(TransformationComponent.class).getPositionY())
                    .realmId(1)
                    .zoneId(1)
                    .characterId(1)
                    .build();
            try {
                this.updateSender.sendUpdate(playerUpdateDto, this.connectionId);
            } catch (UDPServerException e) {
                System.err.println(e.getMessage());
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
