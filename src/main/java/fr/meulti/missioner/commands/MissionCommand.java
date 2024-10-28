package fr.meulti.missioner.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import fr.meulti.missioner.network.MissionPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.Collection;

// ----------------------------------------------------------------------------------------- //
//                                                                                           //
//  Exemple Command : /mission @a "Mission Name" true 5000 1000 "basic" "minecraft:diamond"  //
//                                                                                           //
// ----------------------------------------------------------------------------------------- //

public class MissionCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("mission")
                        .then(Commands.argument("players", EntityArgument.entities())
                                .then(Commands.argument("missionName", StringArgumentType.string())
                                        .then(Commands.argument("fade", BoolArgumentType.bool())
                                                .then(Commands.argument("displayDuration", IntegerArgumentType.integer())
                                                        .then(Commands.argument("fadeDuration", IntegerArgumentType.integer())
                                                                .then(Commands.argument("backgroundName", StringArgumentType.string())
                                                                        .then(Commands.argument("itemOrBlockName", StringArgumentType.string())
                                                                                .executes(context -> {
                                                                                    CommandSourceStack source = context.getSource();
                                                                                    Collection<? extends Entity> players = EntityArgument.getEntities(context, "players");
                                                                                    String missionName = StringArgumentType.getString(context, "missionName");
                                                                                    boolean fade = BoolArgumentType.getBool(context, "fade");
                                                                                    int displayDuration = IntegerArgumentType.getInteger(context, "displayDuration");
                                                                                    int fadeDuration = IntegerArgumentType.getInteger(context, "fadeDuration");
                                                                                    String backgroundName = StringArgumentType.getString(context, "backgroundName");
                                                                                    String itemOrBlockName = StringArgumentType.getString(context, "itemOrBlockName");

                                                                                    for (Entity player : players) {
                                                                                        if (player instanceof ServerPlayer) {
                                                                                            MissionPacket.sendToPlayer((ServerPlayer) player, missionName, fade, displayDuration, fadeDuration, backgroundName, itemOrBlockName);
                                                                                        }
                                                                                    }
                                                                                    return Command.SINGLE_SUCCESS;
                                                                                })
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
        );
    }
}
