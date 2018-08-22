package nivoridocs.strawgolem.proxy;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import nivoridocs.strawgolem.Strawgolem;
import nivoridocs.strawgolem.entity.EntityStrawGolem;

@Mod.EventBusSubscriber
public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		ResourceLocation registryName = new ResourceLocation("");
		EntityRegistry.registerModEntity(registryName, EntityStrawGolem.class,
				"StrawGolem", 1, Strawgolem.instance, 64, 3, false, 0x996600, 0x00ff00);
	}

	public void init(FMLInitializationEvent event) {
		//
	}

	public void postInit(FMLPostInitializationEvent event) {
		//	
	}
	
}
