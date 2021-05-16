package com.commodorethrawn.strawgolem.registry;

import com.commodorethrawn.strawgolem.Strawgolem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.lang.reflect.Field;

public class StrawgolemSounds {
    
    public static final Identifier GOLEM_AMBIENT_ID = new Identifier(Strawgolem.MODID, "golem_ambient");
    public static final Identifier GOLEM_STRAINED_ID = new Identifier(Strawgolem.MODID, "golem_strained");
    public static final Identifier GOLEM_HURT_ID = new Identifier(Strawgolem.MODID, "golem_hurt");
    public static final Identifier GOLEM_DEATH_ID = new Identifier(Strawgolem.MODID, "golem_death");
    public static final Identifier GOLEM_HEAL_ID = new Identifier(Strawgolem.MODID, "golem_heal");
    public static final Identifier GOLEM_SCARED_ID = new Identifier(Strawgolem.MODID, "golem_scared");
    public static final Identifier GOLEM_INTERESTED_ID = new Identifier(Strawgolem.MODID, "golem_interested");
    public static final Identifier GOLEM_DISGUSTED_ID = new Identifier(Strawgolem.MODID, "golem_disgusted");

    public static void register() {
        registerSounds(GOLEM_AMBIENT_ID,
                GOLEM_STRAINED_ID,
                GOLEM_HURT_ID,
                GOLEM_DEATH_ID,
                GOLEM_HEAL_ID,
                GOLEM_SCARED_ID,
                GOLEM_INTERESTED_ID,
                GOLEM_DISGUSTED_ID);
    }

    private static void registerSounds(Identifier... ids) {
        for (Identifier id : ids) {
            registerSound(id);
        }
    }

    private static void registerSound(Identifier id) {
        Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
    }
}
