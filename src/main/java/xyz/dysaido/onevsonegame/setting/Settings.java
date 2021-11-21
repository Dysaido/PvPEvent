package xyz.dysaido.onevsonegame.setting;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.dysaido.onevsonegame.util.Logger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Settings {
    private static final String TAG = "Settings";
    private final JavaPlugin plugin;

    public Settings(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void initialAnnotatedClass(Class<?> clazz) {
        Objects.requireNonNull(clazz, "Class cannot be null");
        List<Field> fields = Arrays.stream(clazz.getFields()).parallel().filter(field -> field.isAnnotationPresent(Options.class)).sorted(Comparator.comparing(Field::getName)).collect(Collectors.toList());
        fields.forEach(field -> {
            Options setting = field.getAnnotation(Options.class);
            String name = setting.name().length() > 0 ? setting.name() : field.getName();
            try {
                if (plugin.getConfig().get(setting.path() + "." + name) == null) {
                    plugin.getConfig().set(setting.path() + "." + name, field.get(null));
                    plugin.saveConfig();
                } else {
                    Object configObj = plugin.getConfig().get(setting.path() + "." + name);
                    field.set(null, configObj);
                }
                Logger.information(plugin.getName() + TAG, "SUCCESS CONFIG SETTING, IT'S NAME : " + name);
            } catch (Exception e) {
                Logger.error(plugin.getName() + TAG, "FAILED CONFIG SETTING, IT'S NAME : " + name);
            }
        });

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.TYPE})
    public @interface Options {
        String path();
        String name();
        String comment() default "";
    }
}
