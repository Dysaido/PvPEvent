package xyz.dysaido.onevsonegame.util;

import sun.misc.Unsafe;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

public class Reflection {

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

    public static Method getMethod(Class<?> clazz, String name, int params) {
        do {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(name) && (method.getParameterTypes().length == params)) {
                    return accessible(method);
                }
            }
        } while ((clazz = clazz.getSuperclass()) != null);
        throw new RuntimeException("Can't find method " + name + " with params length " + params + " in class " + clazz + " or it's superclasses");
    }

    public static Field getField(Class<?> clazz, String name) {
        do {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getName().equals(name)) {
                    return accessible(field);
                }
            }
        } while ((clazz = clazz.getSuperclass()) != null);
        throw new RuntimeException("Can't find field " + name + " in class " + clazz + " or it's superclasses");
    }

    public static <T> T getObject(Object object, Field field, Class<T> type) {
        try {
            return type.cast(accessible(field).get(object));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setField(Object object, String fieldName, Class<?> clazz, Object value) {
        try {
            Field field = accessible(clazz.getDeclaredField(fieldName));
            if (Modifier.isFinal(field.getModifiers())) {
                FFS.set(object, field, value);
            } else {
                field.set(object, value);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static <T extends AccessibleObject> T accessible(T object) {
        object.setAccessible(true);
        return object;
    }

    @FunctionalInterface
    public static interface FinalFieldSetter {

        void set(Object holder, Field field, Object value);

    }

    private static class FinalFieldSetterBitwiseImpl implements FinalFieldSetter {

        @Override
        public void set(Object holder, Field field, Object value) {
            try {
                Field modifiers = Field.class.getDeclaredField("modifiers");
                accessible(modifiers).setInt(field, field.getModifiers() & ~Modifier.FINAL);
                field.set(holder, value);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class FinalFieldSetterUnsafeImpl implements FinalFieldSetter {
        private final Unsafe unsafe;

        private FinalFieldSetterUnsafeImpl() throws ReflectiveOperationException {
            Field field = accessible(Unsafe.class.getDeclaredField("theUnsafe"));
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
