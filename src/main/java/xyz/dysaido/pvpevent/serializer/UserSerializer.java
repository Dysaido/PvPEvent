package xyz.dysaido.pvpevent.serializer;

import com.google.gson.JsonElement;
import xyz.dysaido.pvpevent.model.User;
import xyz.dysaido.pvpevent.model.manager.UserManager;

import java.io.File;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

public class UserSerializer extends GsonSerializer<User> {

    private final UserManager userManager;
    public UserSerializer(UserManager userManager, File file) {
        super(file);
        this.userManager = userManager;
    }

    @Override
    protected JsonElement setObject(User obj) {
        return null;
    }

    @Override
    protected Optional<User> readObject(JsonElement element) {
        return Optional.empty();
    }

    @Override
    protected Stream<User> stream() {
        return userManager.getAll().values().stream().sorted(Comparator.comparing(User::getName));
    }
}
