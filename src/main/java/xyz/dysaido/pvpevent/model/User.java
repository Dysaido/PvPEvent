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

import xyz.dysaido.pvpevent.api.model.Model;

import java.util.UUID;

public class User implements Model<UUID, User> {

    private final UUID identifier;
    private String name;
    private int kills = 0;
    private int deaths = 0;
    private int wins = 0;
    private boolean punished = false;

    public User(UUID uniqueId) {
        this.identifier = uniqueId;
    }

    @Override
    public UUID getIdentifier() {
        return identifier;
    }

    @Override
    public User getOwner() {
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPunished(boolean punished) {
        this.punished = punished;
    }

    public boolean isPunished() {
        return punished;
    }

    public int getKills() {
        return kills;
    }

    public void addKill() {
        this.kills = getKills() + 1;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void addDeath() {
        this.deaths = getDeaths() + 1;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getWins() {
        return wins;
    }

    public void addWin() {
        this.wins = getWins() + 1;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }
}
