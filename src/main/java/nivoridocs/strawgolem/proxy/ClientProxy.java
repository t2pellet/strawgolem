package nivoridocs.strawgolem.proxy;

import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import nivoridocs.strawgolem.entity.EntityStrawGolem;
import nivoridocs.strawgolem.entity.RenderStrawGolem;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		RenderingRegistry.registerEntityRenderingHandler(EntityStrawGolem.class, RenderStrawGolem::new);
	}

}
