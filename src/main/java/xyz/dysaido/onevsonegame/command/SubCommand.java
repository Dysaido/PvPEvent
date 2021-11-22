package xyz.dysaido.onevsonegame.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SubCommand {
    String name();

    String usage();

    String[] aliases() default "";

    String description() default "Another command";

    String permission() default "";

    boolean onlyPlayer() default false;

    boolean onlyConsole() default false;

    boolean onlyOp() default false;

}
