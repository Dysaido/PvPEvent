package xyz.dysaido.pvpevent.inventory.row;

import xyz.dysaido.pvpevent.inventory.BaseInventory;

import java.util.function.BiFunction;

public enum InventoryRow {
    ONE(1, BaseInventory::new),
    TWO(2, BaseInventory::new),
    THREE(3, BaseInventory::new),
    FOUR(4, BaseInventory::new),
    FIVE(5, BaseInventory::new),
    SIX(6, BaseInventory::new);

    private final BiFunction<String, Integer, BaseInventory> inventoryFactory;
    private final int row;

    InventoryRow(int row, BiFunction<String, Integer, BaseInventory> inventoryFactory) {
        this.row = row;
        this.inventoryFactory = inventoryFactory;
    }

    public BaseInventory createInventory(String title) {
        return inventoryFactory.apply(title, row);
    }

    public int getRow() {
        return row;
    }
}
