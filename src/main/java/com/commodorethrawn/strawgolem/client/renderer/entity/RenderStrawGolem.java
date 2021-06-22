package com.commodorethrawn.strawgolem.client.renderer.entity;

import com.commodorethrawn.strawgolem.Strawgolem;
import com.commodorethrawn.strawgolem.client.renderer.entity.model.ModelStrawGolem;
import com.commodorethrawn.strawgolem.config.StrawgolemConfig;
import com.commodorethrawn.strawgolem.entity.EntityStrawGolem;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class RenderStrawGolem extends MobEntityRenderer<EntityStrawGolem, ModelStrawGolem> {

    private static final Map<String, Identifier> TEXTURE_MAP;
    private static final Identifier TEXTURE_DEFAULT, TEXTURE_OLD, TEXTURE_DYING, TEXTURE_WINTER;
    private static final boolean IS_DECEMBER;

    static {
        TEXTURE_DEFAULT = new Identifier(Strawgolem.MODID, "textures/entity/golem.png");
        TEXTURE_OLD = new Identifier(Strawgolem.MODID, "textures/entity/old_golem.png");
        TEXTURE_DYING = new Identifier(Strawgolem.MODID, "textures/entity/dying_golem.png");
        TEXTURE_WINTER = new Identifier(Strawgolem.MODID, "textures/entity/winter_golem.png");
        TEXTURE_MAP = new HashMap<>();
        InputStream nameStream = Strawgolem.class.getResourceAsStream("/assets/strawgolem/textures/entity/customnames");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(nameStream, StandardCharsets.UTF_8));
            while (reader.ready()) {
                String name = reader.readLine();
                Identifier loc = new Identifier(Strawgolem.MODID, "textures/entity/" + name + ".png");
                TEXTURE_MAP.put(name, loc);
            }
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        IS_DECEMBER = GregorianCalendar.getInstance().get(Calendar.MONTH) == Calendar.DECEMBER;
    }

    public RenderStrawGolem(EntityRenderDispatcher rendermanagerIn) {
        super(rendermanagerIn, new ModelStrawGolem(), 0.35f);
        this.addFeature(new HeldItemFeatureRenderer<>(this));
    }

    @Override
    public void render(EntityStrawGolem mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
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
                        || mobEntity.isInRain()
                        || mobEntity.isWet())) {
            double offX = mobEntity.getRandom().nextDouble() / 32 - 1 / 64F;
            double offZ = mobEntity.getRandom().nextDouble() / 32 - 1 / 64F;
            matrixStack.translate(offX, 0, offZ);
        }
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(EntityStrawGolem golem) {
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
    protected void renderLabelIfPresent(EntityStrawGolem entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (!(entity.hasCustomName() && TEXTURE_MAP.containsKey(entity.getDisplayName().getString().toLowerCase()))) {
            super.renderLabelIfPresent(entity, text, matrices, vertexConsumers, light);
        }
    }

}
