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

package xyz.dysaido.pvpevent.api.pagination.row;

import xyz.dysaido.pvpevent.api.pagination.BaseInventory;

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
