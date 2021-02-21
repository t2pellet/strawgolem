package com.commodorethrawn.strawgolem.client.renderer.entity;

import com.commodorethrawn.strawgolem.client.renderer.entity.layers.IronGolemCracksLayer;
import com.commodorethrawn.strawgolem.client.renderer.entity.layers.IronGolemFlowerLayer;
import com.commodorethrawn.strawgolem.client.renderer.entity.model.ModelIronGolem;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.Identifier;

public class RenderIronGolem extends MobEntityRenderer<IronGolemEntity, ModelIronGolem<IronGolemEntity>> {

    private static final Identifier IRON_GOLEM_TEXTURES = new Identifier("textures/entity/iron_golem/iron_golem.png");

    public RenderIronGolem(EntityRenderDispatcher rendermanagerIn) {
        super(rendermanagerIn, new ModelIronGolem<>(), 0.5f);
        this.addFeature(new IronGolemCracksLayer(this));
        this.addFeature(new IronGolemFlowerLayer(this));
    }

    @Override
    public Identifier getTexture(IronGolemEntity entity) {
        return IRON_GOLEM_TEXTURES;
    }
}
