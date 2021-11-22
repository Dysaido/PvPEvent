package xyz.dysaido.inventory;

import java.util.Optional;
import java.util.function.Consumer;

public class CustomAction<Material, Entity> {
    private final Material material;
    private Consumer<Entity> action;

    public CustomAction(Material material) {
        this.material = material;
    }

    public void addAction(Consumer<Entity> action) {
        this.action = action;
    }

    public Optional<Consumer<Entity>> getAction() {
        return Optional.ofNullable(action);
    }

    public Material getMaterial() {
        return material;
    }
}
