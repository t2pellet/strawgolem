package com.commodorethrawn.strawgolem.registry;

import com.commodorethrawn.strawgolem.Strawgolem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class StrawgolemSounds {

    public static class Sound {
        private SoundEvent soundEvent;
        private Identifier id;
        
        public Sound(SoundEvent soundEvent, Identifier id) {
            this.soundEvent = soundEvent;
            this.id = id;
        }
        
        public SoundEvent getSoundEvent() {
            return soundEvent;
        }
        
        public Identifier getId() {
            return id;
        }

        public void register() {
            Registry.register(Registry.SOUND_EVENT, id, soundEvent);
        }
    }
    
    private static final Identifier GOLEM_AMBIENT_ID = new Identifier(Strawgolem.MODID, "golem_ambient");
    private static final Identifier GOLEM_STRAINED_ID = new Identifier(Strawgolem.MODID, "golem_strained");
    private static final Identifier GOLEM_HURT_ID = new Identifier(Strawgolem.MODID, "golem_hurt");
    private static final Identifier GOLEM_DEATH_ID = new Identifier(Strawgolem.MODID, "golem_death");
    private static final Identifier GOLEM_HEAL_ID = new Identifier(Strawgolem.MODID, "golem_heal");
    private static final Identifier GOLEM_SCARED_ID = new Identifier(Strawgolem.MODID, "golem_scared");
    private static final Identifier GOLEM_INTERESTED_ID = new Identifier(Strawgolem.MODID, "golem_interested");

    public static final Sound GOLEM_AMBIENT = new Sound(new SoundEvent(GOLEM_AMBIENT_ID), GOLEM_AMBIENT_ID);
    public static final Sound GOLEM_STRAINED = new Sound(new SoundEvent(GOLEM_STRAINED_ID), GOLEM_STRAINED_ID);
    public static final Sound GOLEM_HURT = new Sound(new SoundEvent(GOLEM_HURT_ID), GOLEM_HURT_ID);
    public static final Sound GOLEM_DEATH = new Sound(new SoundEvent(GOLEM_DEATH_ID), GOLEM_DEATH_ID);
    public static final Sound GOLEM_HEAL = new Sound(new SoundEvent(GOLEM_HEAL_ID), GOLEM_HEAL_ID);
    public static final Sound GOLEM_SCARED = new Sound(new SoundEvent(GOLEM_SCARED_ID), GOLEM_SCARED_ID);
    public static final Sound GOLEM_INTERESTED = new Sound(new SoundEvent(GOLEM_INTERESTED_ID), GOLEM_INTERESTED_ID);
}
