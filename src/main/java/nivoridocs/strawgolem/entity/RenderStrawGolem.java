package nivoridocs.strawgolem.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelVillager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderVillager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import nivoridocs.strawgolem.Strawgolem;

public class RenderStrawGolem extends RenderLiving<EntityStrawGolem> {
	
	private ResourceLocation texture = new ResourceLocation("textures/entity/villager/farmer.png");

	public RenderStrawGolem(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelVillager(1.0f), 0.5f);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityStrawGolem entity) {
		return texture;
	}
	
}
