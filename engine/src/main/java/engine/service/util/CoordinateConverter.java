package engine.service.util;

import engine.service.RenderService;
import org.joml.Vector2d;

public class CoordinateConverter {

    public static Vector2d transformWorldSpaceToClipSpace(Vector2d worldSpacePosition) {
        return worldSpacePosition
                .sub(new Vector2d(RenderService.cameraPosX, RenderService.cameraPosY));
    }
}
