package xyz.dysaido.pvpevent.model.manager;

import xyz.dysaido.pvpevent.PvPEventPlugin;
import xyz.dysaido.pvpevent.model.User;
import xyz.dysaido.pvpevent.serializer.UserSerializer;

import java.io.File;
import java.util.UUID;

public class UserManager extends AbstractManager<UUID, User> {
    private final PvPEventPlugin pvpEvent;
    private final UserSerializer serializer;

    public UserManager(PvPEventPlugin pvpEvent) {
        this.pvpEvent = pvpEvent;
        this.serializer = new UserSerializer(this, new File(pvpEvent.getPlugin().getDataFolder(), "users.json"));
    }

    @Override
    public User remove(UUID id) {
        return null;
    }

    @Override
    public void load() {

    }

    @Override
    public User apply(UUID uuid) {
        return new User(uuid);
    }
}
