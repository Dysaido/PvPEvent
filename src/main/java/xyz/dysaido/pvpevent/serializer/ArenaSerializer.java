package xyz.dysaido.pvpevent.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import xyz.dysaido.pvpevent.model.Arena;
import xyz.dysaido.pvpevent.model.manager.ArenaManager;
import xyz.dysaido.pvpevent.util.CustomLocation;

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
        arenaObj.addProperty("min-capacity", arena.getMinCapacity());
        arenaObj.addProperty("capacity", arena.getCapacity());
        arenaObj.addProperty("queue-countdown", arena.getQueueCountdown());
        arenaObj.addProperty("fight-countdown", arena.getFightCountdown());
        arenaObj.addProperty("toggle-inventory", arena.isToggleInventory());
        arenaObj.addProperty("combo-mode", arena.isComboMode());

        if (arena.getKitName() != null) {
            arenaObj.addProperty("kitname", arena.getKitName());
        }
        if (arena.getLobby() != null) {
            arenaObj.add("lobby", arena.getLobby().serialize());
        }
        if (arena.getPos1() != null) {
            arenaObj.add("pos1", arena.getPos1().serialize());
        }
        if (arena.getPos2() != null) {
            arenaObj.add("pos2", arena.getPos2().serialize());
        }


        return arenaObj;
    }

    @Override
    protected Optional<Arena> readObject(JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject arenaObj = element.getAsJsonObject();
            JsonElement arenaElement = arenaObj.get("name");
            if (arenaElement != null) {
                Arena arena = new Arena(arenaElement.getAsString());

                arenaElement = arenaObj.get("min-capacity");
                if (arenaElement != null) {
                    arena.setMinCapacity(arenaElement.getAsInt());
                }

                arenaElement = arenaObj.get("capacity");
                if (arenaElement != null) {
                    arena.setCapacity(arenaElement.getAsInt());
                }

                arenaElement = arenaObj.get("queue-countdown");
                if (arenaElement != null) {
                    arena.setQueueCountdown(arenaElement.getAsInt());
                }

                arenaElement = arenaObj.get("fight-countdown");
                if (arenaElement != null) {
                    arena.setFightCountdown(arenaElement.getAsInt());
                }

                arenaElement = arenaObj.get("toggle-inventory");
                if (arenaElement != null) {
                    arena.setToggleInventory(arenaElement.getAsBoolean());
                }

                arenaElement = arenaObj.get("combo-mode");
                if (arenaElement != null) {
                    arena.setComboMode(arenaElement.getAsBoolean());
                }

                arenaElement = arenaObj.get("kitname");
                if (arenaElement != null) {
                    arena.setKitName(arenaElement.getAsString());
                }

                arenaElement = arenaObj.get("lobby");
                if (arenaElement != null) {
                    arena.setLobby(CustomLocation.of(arenaElement.getAsJsonObject()));
                }

                arenaElement = arenaObj.get("pos1");
                if (arenaElement != null) {
                    arena.setPos1(CustomLocation.of(arenaElement.getAsJsonObject()));
                }

                arenaElement = arenaObj.get("pos2");
                if (arenaElement != null) {
                    arena.setPos2(CustomLocation.of(arenaElement.getAsJsonObject()));
                }

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
