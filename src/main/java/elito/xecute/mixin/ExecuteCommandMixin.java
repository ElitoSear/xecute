package elito.xecute.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import elito.xecute.modules.raycast.Cast;
import elito.xecute.modules.raycast.Snap;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.SwizzleArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ExecuteCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static net.minecraft.server.command.CommandManager.*;


@Mixin(ExecuteCommand.class)
public class ExecuteCommandMixin {

    @Inject(at = @At("TAIL"), method = "register")
    private static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CallbackInfo ci){

        dispatcher
                .register(
                        literal("execute")
                                .then(
                                        literal("raycast")
                                                .then(
                                                        argument("ratio", DoubleArgumentType.doubleArg(0, 1))
                                                                .redirect(
                                                                        dispatcher.getRoot().getChild("execute"),
                                                                        context -> {

                                                                            ServerCommandSource source = context.getSource();

                                                                            ServerWorld world = source.getWorld();

                                                                            Vec3d origin = source.getPosition();

                                                                            Vec2f rotation = source.getRotation();

                                                                            BlockHitResult hit = Cast.cast(origin, rotation, world);
                                                                            Vec3d destination = hit.getPos();

                                                                            double distance = origin.distanceTo(destination);

                                                                            double limit = DoubleArgumentType.getDouble(context, "ratio");

                                                                            Vec3d result = Cast.forward(origin, rotation, distance * limit);

                                                                            return source.withPosition(result).withRotation(rotation);
                                                                        }
                                                                )
                                                )
                                )
                );

        dispatcher
                .register(
                        literal("execute")
                                .then(
                                        literal("upon")
                                                .redirect(
                                                        dispatcher.getRoot().getChild("execute"),
                                                        context -> {
                                                            ServerCommandSource source = context.getSource();

                                                            ServerWorld world = source.getWorld();

                                                            Vec3d origin = source.getPosition();

                                                            Vec2f rotation = source.getRotation();

                                                            BlockHitResult hit = Cast.cast(origin, rotation, world);

                                                            Vec3d destination = hit.getPos();

                                                            Vec2f rotationUpdate = Cast.getRotation(hit.getSide(), new Vec2f(rotation.y, rotation.x));

                                                            return source.withPosition(destination).withRotation(new Vec2f(rotationUpdate.y, rotationUpdate.x));
                                                        }
                                                )
                                )
                );

        dispatcher
                .register(
                        literal("execute")
                                .then(
                                        CommandManager.literal("snap")
                                                .then(
                                                        CommandManager.argument("axis", SwizzleArgumentType.swizzle())
                                                                .then(
                                                                        CommandManager.argument("step", IntegerArgumentType.integer())
                                                                                .redirect(
                                                                                        dispatcher.getRoot().getChild("execute"),
                                                                                        context -> {
                                                                                            int step = IntegerArgumentType.getInteger(context, "step");

                                                                                            ServerCommandSource source = context.getSource();

                                                                                            Vec2f currentRotation = new Vec2f(source.getRotation().y, source.getRotation().x);

                                                                                            Vec2f newRotation = Snap.snap(currentRotation, step, SwizzleArgumentType.getSwizzle(context, "axis"));

                                                                                            return context.getSource().withRotation(new Vec2f(newRotation.y, newRotation.x));
                                                                                        }
                                                                                )
                                                                )
                                                )
                                )
                );
    }
}
