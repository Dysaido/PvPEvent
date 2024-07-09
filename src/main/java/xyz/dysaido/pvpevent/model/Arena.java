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

package xyz.dysaido.pvpevent.model;

import org.bukkit.Location;
import xyz.dysaido.pvpevent.api.model.Model;
import xyz.dysaido.pvpevent.util.CustomLocation;

public class Arena implements Model<String, Arena> {

    private final String identifier;
    private CustomLocation lobby;
    private CustomLocation pos1;
    private CustomLocation pos2;
    private CustomLocation min;
    private CustomLocation max;
    private String kitName = "";
    private int minCapacity = 2;
    private int capacity = 30;
    private int queueCountdown = 10;
    private int fightCountdown = 3;
    private boolean toggleInventory = false;
    private boolean comboMode = false;

    public Arena(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Arena getOwner() {
        return this;
    }

    public boolean shouldTeleport() {
        return lobby != null && (pos1 != null || pos2 != null);
    }

    public boolean shouldApplyKit() {
        return kitName != null && !kitName.isEmpty();
    }

    public void setMinCapacity(int minCapacity) {
        this.minCapacity = minCapacity;
    }

    public int getMinCapacity() {
        return minCapacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setQueueCountdown(int queueCountdown) {
        this.queueCountdown = queueCountdown;
    }
    public int getQueueCountdown() {
        return queueCountdown;
    }

    public void setKitName(String kitName) {
        this.kitName = kitName;
    }

    public String getKitName() {
        return kitName;
    }

    public void setFightCountdown(int fightCountdown) {
        this.fightCountdown = fightCountdown;
    }

    public int getFightCountdown() {
        return fightCountdown;
    }

    public void setPos1(CustomLocation pos1) {
        this.pos1 = pos1;
    }

    public CustomLocation getPos1() {
        return pos1;
    }

    public void setPos2(CustomLocation pos2) {
        this.pos2 = pos2;
    }

    public CustomLocation getPos2() {
        return pos2;
    }

    public void setLobby(CustomLocation lobby) {
        this.lobby = lobby;
    }

    public CustomLocation getLobby() {
        return lobby;
    }

    public void setToggleInventory(boolean toggleInventory) {
        this.toggleInventory = toggleInventory;
    }

    public boolean isToggleInventory() {
        return toggleInventory;
    }

    public void setComboMode(boolean comboMode) {
        this.comboMode = comboMode;
    }

    public boolean isComboMode() {
        return comboMode;
    }
}
