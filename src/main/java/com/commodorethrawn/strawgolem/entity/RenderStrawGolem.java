package com.commodorethrawn.strawgolem.entity;

import com.commodorethrawn.strawgolem.Strawgolem;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderStrawGolem extends LivingRenderer {
	
	private ResourceLocation texture = new ResourceLocation(Strawgolem.MODID, "textures/entity/straw_golem.png");

	public RenderStrawGolem(EntityRendererManager rendermanagerIn) {
		super(rendermanagerIn, new ModelStrawGolem(), 0.5f);
	}


	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}
}
