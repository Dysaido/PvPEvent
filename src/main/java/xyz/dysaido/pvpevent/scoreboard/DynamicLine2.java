package xyz.dysaido.pvpevent.scoreboard;

import org.bukkit.ChatColor;
import xyz.dysaido.pvpevent.util.BukkitHelper;

import java.util.NoSuchElementException;

public class DynamicLine {
    private final String rawText;
    private String prefix;
    private String name;
    private String suffix;
    private DynamicLine next;

    public DynamicLine(String input, Object... objects) {
        this.rawText = BukkitHelper.colorize(input, objects);
    }

    void calculateTags() {
        String prefix = "";
        String name = "";
        String suffix = "";

        if (rawText.length() <= 16) {
            prefix = rawText;
        } else {
            int split = rawText.charAt(15) == ChatColor.COLOR_CHAR ? 15 : 16;

            prefix = rawText.substring(0, split);
            name = ChatColor.getLastColors(prefix) + rawText.substring(split);

            if (name.length() > 16) {
                split = name.charAt(15) == ChatColor.COLOR_CHAR ? 15 : 16;

                suffix = ChatColor.getLastColors(name) + name.substring(split);
                name = name.substring(0, split);
                if (suffix.length() > 16) {
                    suffix = suffix.substring(0, 16);
                    this.next = new DynamicLine(suffix.substring(16));
                }
            }
        }
        this.prefix = prefix;
        this.name = name;
        this.suffix = suffix;
    }

    public String getRawText() {
        return rawText;
    }

    String getPrefix() {
        return this.prefix;
    }

    public String getName() {
        return name;
    }

    String getSuffix() {
        return this.suffix;
    }

    boolean equals(SidebarLine other) {
        return this.rawText.length() == other.getRawText().length() && this.rawText.equals(other.getRawText());
    }

    public DynamicLineIterator iterator() {
        return new DynamicLineIterator(this);
    }

    public class DynamicLineIterator {
        private DynamicLine current;

        DynamicLineIterator(DynamicLine start) {
            this.current = start;
        }

        public boolean hasNext() {
            return current.next != null;
        }

        public DynamicLine next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No next element");
            }
            current = current.next;
            current.calculateTags();
            return current;
        }
    }
}