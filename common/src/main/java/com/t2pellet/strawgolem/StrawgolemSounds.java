package com.t2pellet.strawgolem;

import com.t2pellet.tlib.common.registry.IModSounds;
import net.minecraft.sounds.SoundEvent;

public class StrawgolemSounds implements IModSounds {

    @ISound("golem_ambient")
    public static SoundEvent GOLEM_AMBIENT = null;
    @ISound("golem_strained")
    public static SoundEvent GOLEM_STRAINED = null;
    @ISound("golem_hurt")
    public static SoundEvent GOLEM_HURT = null;
    @ISound("golem_death")
    public static SoundEvent GOLEM_DEATH = null;
    @ISound("golem_heal")
    public static SoundEvent GOLEM_HEAL = null;
    @ISound("golem_scared")
    public static SoundEvent GOLEM_SCARED = null;
    @ISound("golem_interested")
    public static SoundEvent GOLEM_INTERESTED = null;
    @ISound("golem_disgusted")
    public static SoundEvent GOLEM_DISGUSTED = null;
}