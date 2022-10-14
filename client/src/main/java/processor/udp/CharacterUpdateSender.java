package processor.udp;

import dto.udp.PlayerUpdateDto;
import engine.Engine;
import engine.component.PlayerTag;
import engine.component.TransformationComponent;
import engine.entity.Entity;
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
    private final Engine engine;
    private final int connectionId;
    private long sequenceId;
    private PlayerUpdateDto lastSentUpdateDto;
    private Instant lastSentTimestamp;

    public CharacterUpdateSender(int connectionId, UpdateSender updateSender, Engine engine) {
        this.updateSender = updateSender;
        this.connectionId = connectionId;
        this.sequenceId = 0L;
        this.engine = engine;
    }

    @Override
    public void run() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> updateWorkerFactory = scheduledExecutorService.scheduleAtFixedRate(() -> {
            Entity player = engine.getEntityHandler().getEntityWithComponent(PlayerTag.class);
            PlayerUpdateDto playerUpdateDto = PlayerUpdateDto.builder()
                    .positionX(player.getComponentOfType(TransformationComponent.class).getPositionX())
                    .positionY(player.getComponentOfType(TransformationComponent.class).getPositionY())
                    .realmId(1L)
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
        }, 10, 64, TimeUnit.MILLISECONDS);
        while (true) {

        }
    }
}
