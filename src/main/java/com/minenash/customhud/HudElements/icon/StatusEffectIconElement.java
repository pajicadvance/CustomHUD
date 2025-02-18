package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.HudElements.list.ListProvider;
import com.minenash.customhud.data.Flags;
import com.minenash.customhud.render.RenderPiece;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.UUID;

public class StatusEffectIconElement extends IconElement {
    private static final Identifier EFFECT_BACKGROUND_AMBIENT_TEXTURE = new Identifier("hud/effect_background_ambient");
    private static final Identifier EFFECT_BACKGROUND_TEXTURE = new Identifier("hud/effect_background");

    private final boolean background;
    private final int effectOffset;

    public StatusEffectIconElement(UUID providerID, Flags flags, boolean background) {
        super(flags, flags.scale == 1 ? 11 : 12);
        this.background = background;
        this.effectOffset = scale == 1 ? 1 : Math.round(3F/2*scale);
        this.providerID = providerID;
    }

    @Override
    public void render(DrawContext context, RenderPiece piece) {
        context.getMatrices().push();
        StatusEffectInstance effect = (StatusEffectInstance) piece.value;


        int y= piece.y - 2;
        if (!referenceCorner && scale != 1)
           y-= (width-12)/2;

        Sprite sprite = MinecraftClient.getInstance().getStatusEffectSpriteManager().getSprite(effect.getEffectType());
        int m = effect.getDuration();
        float f = !effect.isDurationBelow(200) ? 1.0f :
            MathHelper.clamp((float)m / 10.0f / 5.0f * 0.5f, 0.0f, 0.5f) + MathHelper.cos((float)m * (float)Math.PI / 5.0f) * MathHelper.clamp((float)(10 - m / 20) / 10.0f * 0.25f, 0.0f, 0.25f);

        context.getMatrices().translate(piece.x + shiftX, y + shiftY, 0);
        rotate(context.getMatrices(), width, width);

        RenderSystem.enableBlend();
        if (background)
            context.drawGuiTexture(effect.isAmbient() ? EFFECT_BACKGROUND_AMBIENT_TEXTURE : EFFECT_BACKGROUND_TEXTURE, 0, 0, width, width);
        context.setShaderColor(1.0f, 1.0f, 1.0f, f);
        context.drawSprite(effectOffset, effectOffset, 0, (int)(9*scale), (int)(9*scale), sprite);
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        context.getMatrices().pop();

    }

}
