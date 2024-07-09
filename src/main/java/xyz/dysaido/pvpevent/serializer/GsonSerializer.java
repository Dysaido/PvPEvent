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

package xyz.dysaido.pvpevent.serializer;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class GsonSerializer<T> {

    private static final Gson PRETTY_PRINTING = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    protected final File file;

    public GsonSerializer(File file) {
        this.file = file;
    }

    protected abstract JsonElement setObject(T obj);

    protected abstract Optional<T> readObject(JsonElement element);

    public List<T> read() {
        List<T> list = new ArrayList<>();

        try {
            BufferedReader reader = Files.newReader(this.file, StandardCharsets.UTF_8);

            try {
                JsonArray array = PRETTY_PRINTING.fromJson(reader, JsonArray.class);
                if (array != null) {
                    array.forEach(element -> readObject(element).ifPresent(list::add));
                }
            } catch (Throwable throwable) {
                if (throwable != null) {
                    try {
                        reader.close();
                    } catch (Throwable inner) {
                        throwable.addSuppressed(inner);
                    }
                }
                throw throwable;
            }

            if (reader != null) {
                reader.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void write() {
        JsonArray array = new JsonArray();
        stream().forEach(obj -> array.add(setObject(obj)));
        String json = PRETTY_PRINTING.toJson(array);

        try {
            BufferedWriter writer = Files.newWriter(this.file, StandardCharsets.UTF_8);

            try {
                writer.write(json);
                writer.close();
            } catch (Throwable throwable) {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (Throwable inner) {
                        throwable.addSuppressed(inner);
                    }
                }

                throw throwable;
            }

            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract Stream<T> stream();

    public static Gson getPrettyPrinting() {
        return PRETTY_PRINTING;
    }
}
