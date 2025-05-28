package elito.xecute.modules.raycast;

import net.minecraft.block.ShapeContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class Cast {

    public static double maximumDistance = 128;

    public static BlockHitResult cast(Vec3d origin, Vec2f rotation, ServerWorld world) {

        Vec3d rotationVector = Vec3d.fromPolar(rotation);
        Vec3d rotationVectorExtended = rotationVector.multiply(Cast.maximumDistance);

        Vec3d destination = origin.add(rotationVectorExtended);

        return world.raycast(new RaycastContext(origin, destination, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, ShapeContext.absent()));
    }

    public static Vec2f getRotation(Direction direction, Vec2f rotation) {

        return switch (direction) {
            case SOUTH -> new Vec2f(0, 0);
            case EAST -> new Vec2f(-90, 0);
            case WEST -> new Vec2f(90, 0);
            case NORTH -> new Vec2f(180, 0);
            case UP -> new Vec2f(rotation.x, -90);
            case DOWN -> new Vec2f(rotation.x, 90);
        };
    }

    public static Vec3d forward(Vec3d position, Vec2f rotation, double distance) {

        Vec3d view = Vec3d.fromPolar(rotation);

        return position.add(view.multiply(distance));

    }
}