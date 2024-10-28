package fr.meulti.missioner.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import fr.meulti.missioner.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Mod.EventBusSubscriber(modid = Main.MODID, value = Dist.CLIENT)
public class MissionRenderer {

    private static ResourceLocation backgroundTexture = new ResourceLocation(Main.MODID, "textures/gui/default_background.png");
    private static DynamicTexture dynamicTexture;
    private static boolean shouldDisplay = false;
    private static long displayStartTime = 0;
    private static int displayDuration = 5000;
    private static int fadeDuration = 1000;
    private static boolean fadeEnabled = true;
    private static String currentMission = "";
    private static ItemStack displayedItem = ItemStack.EMPTY;

    public static void displayMission(String mission, boolean fade, int duration, int fadeTime, String backgroundName, String itemOrBlockName) {
        currentMission = mission;
        shouldDisplay = true;
        displayStartTime = System.currentTimeMillis();
        fadeEnabled = fade;
        displayDuration = duration;
        fadeDuration = fadeTime;

        Path texturePath = Path.of("config", "Missioner", "backgrounds", backgroundName + ".png");

        if (Files.exists(texturePath)) {
            try {
                BufferedImage image = ImageIO.read(texturePath.toFile());
                int width = image.getWidth();
                int height = image.getHeight();
                NativeImage nativeImage = new NativeImage(width, height, false);

                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        int rgb = image.getRGB(x, y);
                        int alpha = (rgb >> 24) & 0xFF;
                        int red = (rgb >> 16) & 0xFF;
                        int green = (rgb >> 8) & 0xFF;
                        int blue = rgb & 0xFF;

                        nativeImage.setPixelRGBA(x, y, (red << 16) | (green << 8) | (blue) | (alpha << 24));
                    }
                }

                dynamicTexture = new DynamicTexture(nativeImage);
                backgroundTexture = Minecraft.getInstance().getTextureManager().register("dynamic_background", dynamicTexture);
            } catch (IOException e) {
                LogUtils.getLogger().error("Failed to load custom background image", e);
                backgroundTexture = new ResourceLocation(Main.MODID, "textures/gui/default_background.png");
            }
        } else {
            backgroundTexture = new ResourceLocation(Main.MODID, "textures/gui/default_background.png");
        }

        displayedItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemOrBlockName)) != null ?
                new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemOrBlockName))) :
                new ItemStack(Items.AIR);
    }

    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        GuiGraphics guiGraphics = event.getGuiGraphics();

        if (!shouldDisplay) return;

        long currentTime = System.currentTimeMillis();
        long endTime = displayStartTime + displayDuration;
        long fadeEndTime = endTime - fadeDuration;

        if (currentTime > endTime) {
            shouldDisplay = false;
            return;
        }

        int x = minecraft.getWindow().getGuiScaledWidth() / 2 - 93;
        int y = 10;
        int width = 187;
        int height = 32;

        float fadeFactor = 1.0f;
        if (fadeEnabled) {
            if (currentTime < displayStartTime + fadeDuration) {
                fadeFactor = (float) (currentTime - displayStartTime) / fadeDuration;
            } else if (currentTime > fadeEndTime) {
                fadeFactor = (float) (endTime - currentTime) / fadeDuration;
            }
            fadeFactor = Math.max(0.0f, Math.min(fadeFactor, 1.0f));
        }

        RenderSystem.setShaderTexture(0, backgroundTexture);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, fadeFactor);
        guiGraphics.blit(backgroundTexture, x, y, 0, 0, width, height, width, height);

        guiGraphics.drawString(minecraft.font, currentMission, x + 15, y + 17, 0xFFFFFF);

        if (!displayedItem.isEmpty()) {
            guiGraphics.renderItem(displayedItem, x + width - 20, y + 8);
        }

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
