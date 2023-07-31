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
