package xyz.dysaido.pvpevent.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import xyz.dysaido.pvpevent.model.Arena;
import xyz.dysaido.pvpevent.model.manager.ArenaManager;

import java.io.File;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

public class ArenaSerializer extends GsonSerializer<Arena> {

    private final ArenaManager arenaManager;
    public ArenaSerializer(ArenaManager arenaManager, File file) {
        super(file);
        this.arenaManager = arenaManager;
    }


    @Override
    protected JsonElement setObject(Arena arena) {
        JsonObject arenaObj = new JsonObject();

        arenaObj.addProperty("name", arena.getIdentifier());
        arenaObj.addProperty("capacity", arena.getCapacity());
        arenaObj.addProperty("queue-countdown", arena.getQueueCountdown());
        arenaObj.addProperty("fight-countdown", arena.getFightCountdown());
        if (arena.getKitName() != null) {
            arenaObj.addProperty("kitname", arena.getKitName());
        }
        if (arena.getLobby() != null) {
            Location lobby = arena.getLobby();
            JsonElement lobbyObj = setPosition(lobby);
            arenaObj.add("lobby", lobbyObj);
        }
        if (arena.getPos1() != null) {
            Location pos1 = arena.getPos1();
            JsonElement pos1Obj = setPosition(pos1);
            arenaObj.add("pos1", pos1Obj);
        }
        if (arena.getPos2() != null) {
            Location pos2 = arena.getPos2();
            JsonElement pos1Obj = setPosition(pos2);
            arenaObj.add("pos2", pos1Obj);
        }


        return arenaObj;
    }

    protected JsonElement setPosition(Location location) {
        JsonObject object = new JsonObject();

        object.addProperty("world", location.getWorld().getName());
        object.addProperty("x", location.getX());
        object.addProperty("y", location.getY());
        object.addProperty("z", location.getZ());
        object.addProperty("yaw", location.getYaw());
        object.addProperty("pitch", location.getPitch());

        return object;
    }

    protected Location getPosition(JsonObject object) {
        JsonElement worldElement = object.get("world");
        JsonElement xElement = object.get("x");
        JsonElement yElement = object.get("y");
        JsonElement zElement = object.get("z");
        JsonElement yawElement = object.get("yaw");
        JsonElement pitchElement = object.get("pitch");
        if (worldElement != null && xElement != null && yElement != null && zElement != null) {
            World world = Bukkit.getServer().getWorld(worldElement.getAsString());
            double x = xElement.getAsDouble();
            double y = yElement.getAsDouble();
            double z = zElement.getAsDouble();
            if (yawElement != null && pitchElement != null) {
                float yaw = yawElement.getAsFloat();
                float pitch = pitchElement.getAsFloat();
                return new Location(world, x, y, z, yaw, pitch);
            } else {
                return new Location(world, x, y, z);
            }
        }
        return null;
    }

    @Override
    protected Optional<Arena> readObject(JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject arenaObj = element.getAsJsonObject();
            JsonElement nameElement = arenaObj.get("name");
            JsonElement capacityElement = arenaObj.get("capacity");
            JsonElement queueElement = arenaObj.get("queue-countdown");
            JsonElement fightElement = arenaObj.get("fight-countdown");

            if (nameElement != null && capacityElement != null && queueElement != null && fightElement != null) {
                String name = nameElement.getAsString();
                int capacity = capacityElement.getAsInt();
                int queueCountdown = queueElement.getAsInt();
                int fightCountdown = fightElement.getAsInt();

                Arena arena = new Arena(name);
                JsonElement lobbyElement = arenaObj.get("lobby");
                JsonElement pos1Element = arenaObj.get("pos1");
                JsonElement pos2Element = arenaObj.get("pos2");
                if (lobbyElement != null && pos1Element != null && pos2Element != null) {
                    Location lobby = getPosition(lobbyElement.getAsJsonObject());
                    Location pos1 = getPosition(pos1Element.getAsJsonObject());
                    Location pos2 = getPosition(pos2Element.getAsJsonObject());
                    arena.setLobby(lobby);
                    arena.setPos1(pos1);
                    arena.setPos2(pos2);
                }
                JsonElement kitElement = arenaObj.get("kitname");
                if (kitElement != null) {
                    arena.setKit(kitElement.getAsString());
                }
                arena.setCapacity(capacity);
                arena.setQueueCountdown(queueCountdown);
                arena.setFightCountdown(fightCountdown);

                return Optional.of(arena);
            }
        }
        return Optional.empty();
    }

    @Override
    protected Stream<Arena> stream() {
        return arenaManager.getAll().values().stream().sorted(Comparator.comparing(Arena::getIdentifier));
    }
}
