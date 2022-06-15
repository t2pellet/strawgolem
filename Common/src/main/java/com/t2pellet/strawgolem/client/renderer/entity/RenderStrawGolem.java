package com.t2pellet.strawgolem.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.t2pellet.strawgolem.StrawgolemCommon;
import com.t2pellet.strawgolem.client.renderer.entity.layers.StrawGolemHatLayer;
import com.t2pellet.strawgolem.client.renderer.entity.model.ModelStrawGolem;
import com.t2pellet.strawgolem.config.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.EntityStrawGolem;
import com.t2pellet.strawgolem.registry.ClientRegistry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class RenderStrawGolem extends MobRenderer<EntityStrawGolem, ModelStrawGolem> {

    private static final Map<String, ResourceLocation> TEXTURE_MAP;
    private static final ResourceLocation TEXTURE_DEFAULT, TEXTURE_OLD, TEXTURE_DYING, TEXTURE_WINTER;
    private static final boolean IS_DECEMBER;

    static {
        TEXTURE_DEFAULT = new ResourceLocation(StrawgolemCommon.MODID, "textures/entity/golem.png");
        TEXTURE_OLD = new ResourceLocation(StrawgolemCommon.MODID, "textures/entity/old_golem.png");
        TEXTURE_DYING = new ResourceLocation(StrawgolemCommon.MODID, "textures/entity/dying_golem.png");
        TEXTURE_WINTER = new ResourceLocation(StrawgolemCommon.MODID, "textures/entity/winter_golem.png");
        TEXTURE_MAP = new HashMap<>();
        InputStream nameStream = StrawgolemCommon.class.getResourceAsStream("/assets/strawgolem/textures/entity/customnames");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(nameStream, StandardCharsets.UTF_8));
            while (reader.ready()) {
                String name = reader.readLine();
                ResourceLocation loc = new ResourceLocation(StrawgolemCommon.MODID, "textures/entity/" + name + ".png");
                TEXTURE_MAP.put(name, loc);
            }
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        IS_DECEMBER = GregorianCalendar.getInstance().get(Calendar.MONTH) == Calendar.DECEMBER;
    }

    public RenderStrawGolem(EntityRendererProvider.Context context) {
        super(context, new ModelStrawGolem(context.bakeLayer(ClientRegistry.Entities.getStrawGolemModel())), 0.35f);
        this.addLayer(new ItemInHandLayer<>(this));
        this.addLayer(new StrawGolemHatLayer(this));
    }

    @Override
    public void render(EntityStrawGolem mobEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
        this.getModel().setHoldingBlock(mobEntity.holdingFullBlock());
        this.getModel().setHoldingItem(!mobEntity.isHandEmpty());
        this.getModel().setHungry(mobEntity.getHunger().isHungry());
        this.getModel().setTempted(mobEntity.isTempted());
        // Lower position for sitting
        if (mobEntity.getHunger().isHungry()) {
            matrixStack.translate(0, -0.2F, 0);
        }
        // Shivering movement
        if (StrawgolemConfig.Miscellaneous.isShiverEnabled() &&
                (mobEntity.isInCold()
                        || mobEntity.isInWaterOrBubble()
                        || (mobEntity.isInWaterOrRain() && !mobEntity.getAccessory().hasHat()))) {
            double offX = mobEntity.getRandom().nextDouble() / 32 - 1 / 64F;
            double offZ = mobEntity.getRandom().nextDouble() / 32 - 1 / 64F;
            matrixStack.translate(offX, 0, offZ);
        }
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityStrawGolem golem) {
        int lifespan = golem.getLifespan().get();
        int maxLifespan = StrawgolemConfig.Health.getLifespan();
        if (lifespan * 4 < maxLifespan && lifespan >= 0) return TEXTURE_DYING;
        if (golem.hasCustomName()) {
            String name = golem.getDisplayName().getString().toLowerCase();
            if (TEXTURE_MAP.containsKey(name)) return TEXTURE_MAP.get(name);
        }
        if (IS_DECEMBER) return TEXTURE_WINTER;
        if (lifespan < 0) return TEXTURE_DEFAULT;
        return lifespan * 2 < maxLifespan ? TEXTURE_OLD : TEXTURE_DEFAULT;
    }

    @Override
    protected void renderNameTag(EntityStrawGolem entity, Component text, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        if (!(entity.hasCustomName() && TEXTURE_MAP.containsKey(entity.getDisplayName().getString().toLowerCase()))) {
            super.renderNameTag(entity, text, matrices, vertexConsumers, light);
        }
    }
}
