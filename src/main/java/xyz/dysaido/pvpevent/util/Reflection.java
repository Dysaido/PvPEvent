/*
 * The MIT License.
 *
 * Copyright (c) Dysaido <tonyyoni@gmail.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package xyz.dysaido.pvpevent.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

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
