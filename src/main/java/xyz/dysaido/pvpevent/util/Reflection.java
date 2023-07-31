package xyz.dysaido.pvpevent.util;

import java.lang.reflect.*;

public class Reflection {

    public static <T> T fetch(Object object, Field field) {
        try {
            //noinspection unchecked
            return (T) ensureAccessible(field).get(object);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Field findField(Class<?> searchClass, Class<?> fieldType) {
        Field field = null;
        do {
            for (Field entry : searchClass.getDeclaredFields()) {
                if (fieldType.isAssignableFrom(entry.getType())) {
                    field = entry;
                }
            }
            searchClass = searchClass.getSuperclass();
        } while (field == null && searchClass != Object.class);
        return field;
    }

    public static Field findField(Class<?> searchClass, String fieldName) {
        Field field = null;
        do {
            try {
                field = searchClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                searchClass = searchClass.getSuperclass();
            }
        } while (field == null && searchClass != Object.class);
        return field;
    }

    public static <T extends AccessibleObject> T ensureAccessible(T object) {
        object.setAccessible(true);
        return object;
    }

    public static void ensureStatic(Member member) {
        if (!Modifier.isStatic(member.getModifiers())) {
            throw new IllegalArgumentException();
        }
    }
}
