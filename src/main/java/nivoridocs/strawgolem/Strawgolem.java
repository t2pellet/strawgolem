package nivoridocs.strawgolem;

import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import nivoridocs.strawgolem.entity.EntityRegistry;
import nivoridocs.strawgolem.entity.RenderStrawGolem;
import nivoridocs.strawgolem.entity.capability.lifespan.ILifespan;
import nivoridocs.strawgolem.entity.capability.lifespan.Lifespan;
import nivoridocs.strawgolem.entity.capability.lifespan.LifespanStorage;

@Mod(Strawgolem.MODID)
public class Strawgolem {
    public static final String MODID = "strawgolem";

    public static Strawgolem instance;

    public Strawgolem() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(instance::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(instance::clientSetup);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(ILifespan.class, new LifespanStorage(), Lifespan::new);
    }

    public void clientSetup(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityRegistry.STRAW_GOLEM_TYPE, RenderStrawGolem::new);
    }

}
