package processor;

import dto.udp.PlayerUpdateDto;
import engine.component.PlayerTag;
import engine.component.TransformationComponent;
import engine.entity.Entity;
import engine.handler.EntityHandler;
import engine.handler.NavHandler;
import engine.object.NavMap;
import exception.UDPServerException;
import udp.UpdateSender;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CharacterUpdateSender implements Runnable {

    private final UpdateSender updateSender;
    private final int connectionId;
    private long sequenceId;
    private PlayerUpdateDto lastSentUpdateDto;
    private Instant lastSentTimestamp;

    public CharacterUpdateSender(int connectionId, UpdateSender updateSender) {
        this.updateSender = updateSender;
        this.connectionId = connectionId;
        this.sequenceId = 0L;
    }

    @Override
    public void run() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> updateWorkerFactory = scheduledExecutorService.scheduleAtFixedRate(() -> {
            Entity player = EntityHandler.getInstance().getEntityWithComponent(PlayerTag.class);
            NavMap navMap = NavHandler.getInstance().getNavMap();
            if (navMap != null) {
                long seed = navMap.getSeed();
                PlayerUpdateDto playerUpdateDto = PlayerUpdateDto.builder()
                        .positionX(player.getComponentOfType(TransformationComponent.class).getPositionX())
                        .positionY(player.getComponentOfType(TransformationComponent.class).getPositionY())
                        .realmId(1L)
                        .zoneId(seed)
                        .characterId(1)
                        .build();
                if (lastSentUpdateDto == null || lastSentUpdateDto.hashCode() != playerUpdateDto.hashCode() || lastSentTimestamp.isBefore(Instant.now().minus(3, ChronoUnit.SECONDS))) {
                    try {
                        this.updateSender.sendUpdate(playerUpdateDto, this.connectionId, 2, this.sequenceId++);
                        this.lastSentTimestamp = Instant.now();
                        this.lastSentUpdateDto = playerUpdateDto;
                    } catch (UDPServerException e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
        }, 10, 10, TimeUnit.MILLISECONDS);
        while (true) {

        }
    }
}
