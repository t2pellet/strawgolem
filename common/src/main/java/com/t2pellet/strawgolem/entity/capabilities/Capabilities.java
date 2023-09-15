package com.t2pellet.strawgolem.entity.capabilities;

import com.t2pellet.strawgolem.entity.StrawGolem;
import com.t2pellet.strawgolem.entity.capabilities.decay.Decay;
import com.t2pellet.strawgolem.entity.capabilities.deliverer.Deliverer;
import com.t2pellet.strawgolem.entity.capabilities.harvester.Harvester;
import com.t2pellet.strawgolem.entity.capabilities.held_item.HeldItem;
import com.t2pellet.strawgolem.entity.capabilities.tether.Tether;
import com.t2pellet.tlib.common.entity.capability.ICapabilityHaver;
import com.t2pellet.tlib.common.entity.capability.IModCapabilities;
import net.minecraft.world.entity.LivingEntity;

public class Capabilities implements IModCapabilities {

    @ICapability(Decay.class)
    public static final TLibCapability<Decay, StrawGolem> decay = new TLibCapability<>(Decay::getInstance);
    @ICapability(HeldItem.class)
    public static final TLibCapability<HeldItem, StrawGolem> heldItem = new TLibCapability<>(HeldItem::getInstance);
    @ICapability(Harvester.class)
    public static final TLibCapability<Harvester, StrawGolem> harvester = new TLibCapability<>(Harvester::getInstance);
    @ICapability(Deliverer.class)
    public static final TLibCapability<Deliverer, StrawGolem> deliverer = new TLibCapability<>(Deliverer::getInstance);
    @ICapability(Tether.class)
    public static final TLibCapability<Tether, StrawGolem> tether = new TLibCapability<>(Tether::getInstance);
}
