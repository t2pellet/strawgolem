package nivoridocs.strawgolem.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import nivoridocs.strawgolem.Strawgolem;

public class RenderStrawGolem extends RenderLiving<EntityStrawGolem> {
	
	private ResourceLocation texture = new ResourceLocation(Strawgolem.MODID, "textures/entity/straw_golem.png");

	public RenderStrawGolem(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelStrawGolem(), 0.5f);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityStrawGolem entity) {
		return texture;
	}
	
}
