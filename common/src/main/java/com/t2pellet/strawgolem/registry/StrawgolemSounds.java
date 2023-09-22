package com.t2pellet.strawgolem.registry;

import com.t2pellet.tlib.registry.api.RegistryClass;
import com.t2pellet.tlib.registry.api.SoundEntryType;
import net.minecraft.sounds.SoundEvent;

@RegistryClass.IRegistryClass(SoundEvent.class)
public class StrawgolemSounds implements RegistryClass {

    @IRegistryEntry
    public static final SoundEntryType GOLEM_AMBIENT = new SoundEntryType("golem_ambient");
    @IRegistryEntry
    public static final SoundEntryType GOLEM_STRAINED = new SoundEntryType("golem_strained");
    @IRegistryEntry
    public static final SoundEntryType GOLEM_HURT = new SoundEntryType("golem_hurt");
    @IRegistryEntry
    public static final SoundEntryType GOLEM_DEATH = new SoundEntryType("golem_death");
    @IRegistryEntry
    public static final SoundEntryType GOLEM_HEAL = new SoundEntryType("golem_heal");
    @IRegistryEntry
    public static final SoundEntryType GOLEM_SCARED = new SoundEntryType("golem_scared");
    @IRegistryEntry
    public static final SoundEntryType GOLEM_INTERESTED = new SoundEntryType("golem_interested");
    @IRegistryEntry
    public static final SoundEntryType GOLEM_DISGUSTED = new SoundEntryType("golem_disgusted");
}