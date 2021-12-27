package processor;

import dto.udp.PlayerUpdateDto;
import engine.component.PlayerComponent;
import engine.component.TransformationComponent;
import engine.entity.Entity;
import engine.handler.EntityHandler;
import engine.handler.NavHandler;
import exception.UDPServerException;
import udp.UpdateSender;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CharacterUpdateSender implements Runnable {

    private final UpdateSender updateSender;
    private final int connectionId;
    private long sequenceId;

    public CharacterUpdateSender(int connectionId, UpdateSender updateSender) {
        this.updateSender = updateSender;
        this.connectionId = connectionId;
        this.sequenceId = 0L;
    }

    @Override
    public void run() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            Entity player = EntityHandler.getInstance().getEntityWithComponent(PlayerComponent.class);
            long seed = NavHandler.getInstance().getNavMap().getSeed();
            PlayerUpdateDto playerUpdateDto = PlayerUpdateDto.builder()
                    .positionX(player.getComponentOfType(TransformationComponent.class).getPositionX())
                    .positionY(player.getComponentOfType(TransformationComponent.class).getPositionY())
                    .realmId(1L)
                    .zoneId(seed)
                    .characterId(1)
                    .build();
            try {
                this.updateSender.sendUpdate(playerUpdateDto, this.connectionId, 2, this.sequenceId++);
            } catch (UDPServerException e) {
                System.err.println(e.getMessage());
            }
        }, 100, 100, TimeUnit.MILLISECONDS);
    }
}
