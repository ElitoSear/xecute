package elito.xecute.modules.raycast;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;

import java.util.EnumSet;

public class Snap {

    public static Vec2f snap(Vec2f rotation, float step, EnumSet<Direction.Axis> axes) {
        float yaw = rotation.x;
        float pitch = rotation.y;

        if (axes.contains(Direction.Axis.X)) yaw = Math.round(yaw / step) * step;

        if (axes.contains(Direction.Axis.Y)) pitch = Math.round(pitch / step) * step;

        pitch = Math.max(-90, Math.min(90, pitch));

        return new Vec2f(yaw, pitch);
    }

}
