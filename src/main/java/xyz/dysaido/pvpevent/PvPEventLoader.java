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

package xyz.dysaido.pvpevent;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.dysaido.pvpevent.api.PvPEvent;
import xyz.dysaido.pvpevent.config.Settings;
import xyz.dysaido.pvpevent.util.Logger;

public final class PvPEventLoader extends JavaPlugin {

    private static final String TAG= "Loader";
    private PvPEventPlugin event;
    private Metrics metrics;

    @Override
    public void onEnable() {
        if (this.event != null) {
            Settings.IMP.reload(this.event.getConfigFile());
            Logger.debug(TAG, "enable");
            if (Settings.IMP.METRICS) {
                Logger.debug(TAG, "metrics active");
                metrics = new Metrics(this, 19693);
            } else {
                Logger.debug(TAG, "metrics inactive");
            }

            // Optional: Add custom charts
            this.event.enable();
        } else {
            Logger.error(TAG, "Plugin is null, unload this plugin from the server and load it again!");
        }
    }

    @Override
    public void onDisable() {
        Logger.debug(TAG, "disable");
        if (this.event != null) {
            this.event.disable();
        }
        if (this.metrics != null) {
            this.metrics.shutdown();
        }
        this.event = null;
        this.metrics = null;
    }

    @Override
    public void onLoad() {
        Logger.info(TAG, "Initialize plugin");
        this.event = new PvPEventPlugin(this);
    }

    public PvPEvent getPvPEvent() {
        return event;
    }
}
