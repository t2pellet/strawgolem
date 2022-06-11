package com.t2pellet.strawgolem.client.compat;

import com.t2pellet.strawgolem.StrawgolemCommon;
import com.t2pellet.strawgolem.config.StrawgolemConfig;
import com.t2pellet.strawgolem.util.io.ConfigHelper;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.*;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class ClothConfigCompat {


    public static void registerConfigMenu() {
        StrawgolemCommon.LOG.info("Registering cloth config compat");
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, screen) -> {
                    try {
                        return createConfigBuilder().setParentScreen(screen).build();
                    } catch (IllegalAccessException e) {
                        StrawgolemCommon.LOG.error("Failed to create config screen", e);
                    }
                    return screen;
                }));
    }

    /* Disgusting code */
    private static ConfigBuilder createConfigBuilder() throws IllegalAccessException {
        ConfigBuilder builder = ConfigBuilder.create().setTitle(Component.translatable("title.strawgolem.config"));
        for (Class<?> declaredClass : StrawgolemConfig.class.getDeclaredClasses()) {
            String categoryName = declaredClass.getSimpleName();
            ConfigCategory category = builder.getOrCreateCategory(Component.literal(categoryName));
            ConfigEntryBuilder entryBuilder = ConfigEntryBuilder.create();
            for (Field declaredField : declaredClass.getDeclaredFields()) {
                declaredField.setAccessible(true);
                ConfigHelper.Section.Comment comment = null;
                if (declaredField.isAnnotationPresent(ConfigHelper.Section.Comment.class)) {
                    comment = declaredField.getAnnotation(ConfigHelper.Section.Comment.class);
                }
                if (String.class.isAssignableFrom(declaredField.getType())) {
                    StringFieldBuilder fieldBuilder = entryBuilder.startStrField(Component.literal(declaredField.getName()), (String) declaredField.get(null))
                            .setDefaultValue((String) declaredField.get(null))
                            .setSaveConsumer(s -> setField(declaredField, s));
                    if (comment != null) {
                        fieldBuilder.setTooltip(Component.literal(comment.value()));
                    }
                    category.addEntry(fieldBuilder.build());
                } else if (Integer.TYPE.isAssignableFrom(declaredField.getType())) {
                    IntFieldBuilder fieldBuilder = entryBuilder.startIntField(Component.literal(declaredField.getName()), (int) declaredField.get(null))
                            .setDefaultValue((int) declaredField.get(null))
                            .setSaveConsumer(i -> setField(declaredField, i));
                    if (comment != null) {
                        fieldBuilder.setTooltip(Component.literal(comment.value()));
                    }
                    category.addEntry(fieldBuilder.build());
                } else if (Boolean.TYPE.isAssignableFrom(declaredField.getType())) {
                    BooleanToggleBuilder toggleBuilder = entryBuilder.startBooleanToggle(Component.literal(declaredField.getName()), (boolean) declaredField.get(null))
                            .setDefaultValue((boolean) declaredField.get(null))
                            .setSaveConsumer(b -> setField(declaredField, b));
                    if (comment != null) {
                        toggleBuilder.setTooltip(Component.literal(comment.value()));
                    }
                    category.addEntry(toggleBuilder.build());
                } else if (List.class.isAssignableFrom(declaredField.getType())) {
                    Type listType = declaredField.getGenericType();
                    if (listType instanceof ParameterizedType parameterizedType) {
                        Class<?> type = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                        if (String.class.isAssignableFrom(type)) {
                            StringListBuilder stringListBuilder = entryBuilder.startStrList(Component.literal(declaredField.getName()), (List<String>) declaredField.get(null))
                                    .setDefaultValue((List<String>) declaredField.get(null))
                                    .setSaveConsumer(l -> setField(declaredField, l));
                            if (comment != null) {
                                stringListBuilder.setTooltip(Component.literal(comment.value()));
                            }
                            category.addEntry(stringListBuilder.build());
                        }
                    }
                } else if (Double.TYPE.isAssignableFrom(declaredField.getType())) {
                    DoubleFieldBuilder fieldBuilder = entryBuilder.startDoubleField(Component.literal(declaredField.getName()), (double) declaredField.get(null))
                            .setDefaultValue((double) declaredField.get(null))
                            .setSaveConsumer(d -> setField(declaredField, d));
                    if (comment != null) {
                        fieldBuilder.setTooltip(Component.literal(comment.value()));
                    }
                    category.addEntry(fieldBuilder.build());
                }
            }
        }
        return builder;
    }

    private static void setField(Field field, Object value) {
        try {
            field.set(null, value);
            ConfigHelper.INSTANCE.save(StrawgolemConfig.class);
        } catch (IllegalAccessException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}
