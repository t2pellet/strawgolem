package nivoridocs.strawgolem.entity.capability.lifespan;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import nivoridocs.strawgolem.Strawgolem;
import nivoridocs.strawgolem.entity.EntityStrawGolem;

@EventBusSubscriber
public class LifespanHandler {
	
	public static final ResourceLocation LIFESPAN_RES = new ResourceLocation(Strawgolem.MODID, "lifespan");
	
	@SubscribeEvent
	public static void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof EntityStrawGolem)
			event.addCapability(LIFESPAN_RES, new LifespanProvider());
	}
	
	private LifespanHandler() {}

}
