package io.github.archipelagomw.events;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * tag a method with this who's only parameter is a class that extends {@link Event}
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ArchipelagoEventListener {
}
