package com.lion.villagersplus.client.screen;

import com.lion.villagersplus.VillagersPlus;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class AlchemistTableScreen extends AbstractContainerScreen<AlchemistTableScreenHandler> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(VillagersPlus.MOD_ID, "textures/gui/container/alchemist_table.png");
    private static final int[] BUBBLE_PROGRESS = new int[]{29, 24, 20, 16, 11, 6, 0};

    public AlchemistTableScreen(AlchemistTableScreenHandler handler, Inventory inventory, Component title) {
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
        int k = ((AlchemistTableScreenHandler)this.menu).getFuel();
        int l = Mth.clamp((18 * k + 20 - 1) / 20, 0, 18);
        if (l > 0) {
            context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, i + 60, j + 44, (float) (176), (float) (29), l, 4, 256, 256);
        }

        int m = ((AlchemistTableScreenHandler)this.menu).getBrewTime();
        if (m > 0) {
            int n = (int)(28.0F * (1.0F - (float)m / 400.0F));
            if (n > 0) {
                context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, i + 97, j + 16, (float) (176), (float) (0), 9, n, 256, 256);
            }

            n = BUBBLE_PROGRESS[m / 2 % 7];
            if (n > 0) {
                context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, i + 63, j + 14 + 29 - n, (float) (185), (float) (29 - n), 12, n, 256, 256);
            }
        }

    }
}
