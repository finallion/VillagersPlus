package com.lion.villagersplus.client.screen;

import com.lion.villagersplus.VillagersPlus;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class AlchemistTableScreen extends HandledScreen<AlchemistTableScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(VillagersPlus.MOD_ID, "textures/gui/container/alchemist_table.png");
    private static final int[] BUBBLE_PROGRESS = new int[]{29, 24, 20, 16, 11, 6, 0};

    public AlchemistTableScreen(AlchemistTableScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    protected void init() {
        super.init();
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        int k = ((AlchemistTableScreenHandler)this.handler).getFuel();
        int l = MathHelper.clamp((18 * k + 20 - 1) / 20, 0, 18);
        if (l > 0) {
            context.drawTexture(TEXTURE, i + 60, j + 44, 176, 29, l, 4);
        }

        int m = ((AlchemistTableScreenHandler)this.handler).getBrewTime();
        if (m > 0) {
            int n = (int)(28.0F * (1.0F - (float)m / 400.0F));
            if (n > 0) {
                context.drawTexture(TEXTURE, i + 97, j + 16, 176, 0, 9, n);
            }

            n = BUBBLE_PROGRESS[m / 2 % 7];
            if (n > 0) {
                context.drawTexture(TEXTURE, i + 63, j + 14 + 29 - n, 185, 29 - n, 12, n);
            }
        }

    }
}
