package com.teamacronymcoders.base.registrysystem.pieces;

import net.minecraftforge.eventbus.api.EventPriority;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static net.minecraftforge.eventbus.api.EventPriority.NORMAL;


@Retention(RetentionPolicy.RUNTIME)
public @interface RegistryPiece {
    RegistrySide value() default RegistrySide.BOTH;

    String modid() default "";

    EventPriority priority() default NORMAL;
}
