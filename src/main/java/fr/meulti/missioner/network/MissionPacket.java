package fr.meulti.missioner.network;

import fr.meulti.missioner.renderer.MissionRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class MissionPacket {
    private final String missionName;
    private final boolean fade;
    private final int displayDuration;
    private final int fadeDuration;
    private final String backgroundName;
    private final String itemOrBlockName;

    public MissionPacket(String missionName, boolean fade, int displayDuration, int fadeDuration, String backgroundName, String itemOrBlockName) {
        this.missionName = missionName;
        this.fade = fade;
        this.displayDuration = displayDuration;
        this.fadeDuration = fadeDuration;
        this.backgroundName = backgroundName;
        this.itemOrBlockName = itemOrBlockName;
    }

    public static void sendToPlayer(ServerPlayer player, String missionName, boolean fade, int displayDuration, int fadeDuration, String backgroundName, String itemOrBlockName) {
        PacketsHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new MissionPacket(missionName, fade, displayDuration, fadeDuration, backgroundName, itemOrBlockName));
    }

    public static void encode(MissionPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.missionName);
        buf.writeBoolean(msg.fade);
        buf.writeInt(msg.displayDuration);
        buf.writeInt(msg.fadeDuration);
        buf.writeUtf(msg.backgroundName);
        buf.writeUtf(msg.itemOrBlockName);
    }

    public static MissionPacket decode(FriendlyByteBuf buf) {
        return new MissionPacket(buf.readUtf(), buf.readBoolean(), buf.readInt(), buf.readInt(), buf.readUtf(), buf.readUtf());
    }

    public static class MissionPacketHandler {
        public static void handle(MissionPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                    MissionRenderer.displayMission(msg.missionName, msg.fade, msg.displayDuration, msg.fadeDuration, msg.backgroundName, msg.itemOrBlockName);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
