package com.lion.villagersplus.client.screen;

import com.lion.villagersplus.VillagersPlus;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class OreGrinderScreen extends HandledScreen<OreGrinderScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of(VillagersPlus.MOD_ID, "textures/gui/container/ore_grinder.png");

    public OreGrinderScreen(OreGrinderScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    protected void init() {
        super.init();
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, (float) (0), (float) (0), this.backgroundWidth, this.backgroundHeight, 256, 256);

        if (this.handler.isGrinding()) {
            int k = this.handler.getFuelProgress();
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i + 56, j + 36 + 12 - k, (float) (176), (float) (12 - k), 14, k + 1, 256, 256);
        }

        int l = this.handler.getGrindProgress();
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i + 79, j + 34, (float) (176), (float) (14), l + 1, 16, 256, 256);
    }
}
