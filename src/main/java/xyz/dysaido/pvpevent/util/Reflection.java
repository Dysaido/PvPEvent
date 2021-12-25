package xyz.dysaido.onevsonegame.util;

import org.bukkit.Bukkit;
import sun.misc.Unsafe;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;

public class Reflection {
    public static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    public static final FinalFieldSetter FFS;

    static {
        FinalFieldSetter inner;
        try {
            inner = new FinalFieldSetterUnsafeImpl();
        } catch (ReflectiveOperationException e) {
            inner = new FinalFieldSetterBitwiseImpl();
        }
        FFS = inner;
    }

    public static Method getMethod(Class<?> clazz, String name, Class<?> returnType, Class<?>... params) {
        do {
            for (final Method method : clazz.getDeclaredMethods()) {
                if ((name == null || method.getName().equals(name))
                        && (returnType == null || method.getReturnType().equals(returnType))
                        && Arrays.equals(method.getParameterTypes(), params)) {
                    return setAccessible(method);
                }
            }
        } while ((clazz = clazz.getSuperclass()) != null);
        throw new RuntimeException("Can't find method " + name + " with params " + Arrays.toString(params) + " in class " + clazz + " or it's superclasses");
    }

    public static Field getField(Class<?> clazz, String name) {
        do {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getName().equals(name)) {
                    return setAccessible(field);
                }
            }
        } while ((clazz = clazz.getSuperclass()) != null);
        throw new RuntimeException("Can't find field " + name + " in class " + clazz + " or it's superclasses");
    }

    public static Field getField(Class<?> target, Class<?> fieldType) {
        return getField(target, null, fieldType);
    }

    public static Field getField(Class<?> clazz, String name, Class<?> fieldType) {
        do {
            for (Field field : clazz.getDeclaredFields()) {
                if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType())) {
                    return setAccessible(field);
                }
            }
        } while ((clazz = clazz.getSuperclass()) != null);
        throw new RuntimeException("Can't find field " + name + " in class " + clazz + " or it's superclasses");
    }

    @SuppressWarnings("unchecked")
    public static <T> T fetch(Object object, Field field) {
        try {
            return (T) setAccessible(field).get(object);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void setField(Object holder, String name, Class<?> parent, Object value) {
        try {
            Field field = setAccessible(parent.getDeclaredField(name));
            if (Modifier.isFinal(field.getModifiers())) {
                FFS.set(holder, field, value);
            } else {
                field.set(holder, value);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void setField(Object holder, Field field, Object value) {
        try {
            setAccessible(field);
            if (Modifier.isFinal(field.getModifiers())) {
                FFS.set(holder, field, value);
            } else {
                field.set(holder, value);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static <T extends AccessibleObject> T setAccessible(T object) {
        object.setAccessible(true);
        return object;
    }

    @FunctionalInterface
    public interface FinalFieldSetter {

        void set(Object holder, Field field, Object value);

    }

    private static class FinalFieldSetterBitwiseImpl implements FinalFieldSetter {

        @Override
        public void set(Object holder, Field field, Object value) {
            try {
                Field modifiers = Field.class.getDeclaredField("modifiers");
                setAccessible(modifiers).setInt(field, field.getModifiers() & ~Modifier.FINAL);
                field.set(holder, value);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class FinalFieldSetterUnsafeImpl implements FinalFieldSetter {
        private final Unsafe unsafe;

        private FinalFieldSetterUnsafeImpl() throws ReflectiveOperationException {
            Field field = setAccessible(Unsafe.class.getDeclaredField("theUnsafe"));
            unsafe = (Unsafe) field.get(null);
        }

        @Override
        public void set(Object holder, Field field, Object value) {
            Objects.requireNonNull(field, "field must not be null");

            Object ufo = holder != null ? holder : this.unsafe.staticFieldBase(field);
            long offset = holder != null ? this.unsafe.objectFieldOffset(field) : this.unsafe.staticFieldOffset(field);

            this.unsafe.putObject(ufo, offset, value);
        }
    }

}
