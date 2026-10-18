package com.lion.villagersplus.client.screen;

import com.lion.villagersplus.VillagersPlus;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class OreGrinderScreen extends AbstractContainerScreen<OreGrinderScreenHandler> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(VillagersPlus.MOD_ID, "textures/gui/container/ore_grinder.png");

    public OreGrinderScreen(OreGrinderScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractBackground(context, mouseX, mouseY, delta);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, (float) (0), (float) (0), this.imageWidth, this.imageHeight, 256, 256);

        if (this.menu.isGrinding()) {
            int k = this.menu.getFuelProgress();
            context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, i + 56, j + 36 + 12 - k, (float) (176), (float) (12 - k), 14, k + 1, 256, 256);
        }

        int l = this.menu.getGrindProgress();
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, i + 79, j + 34, (float) (176), (float) (14), l + 1, 16, 256, 256);
    }
}
