package fengliu.cloudmusic.music163;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import net.minecraft.text.Text;

public enum Quality implements IConfigOptionListEntry {
    STANDARD ("cloudmusic.play.quality.standard", "standard"),
    HIGHER ("cloudmusic.play.quality.higher", "higher"),
    EXHIGH ("cloudmusic.play.quality.exhigh", "exhigh"),
    LOSSLESS ("cloudmusic.play.quality.lossless", "lossless"),
    HIRES ("cloudmusic.play.quality.hires", "hires");

    private final String translationKey;
    private final String quality;

    Quality(String translationKey, String quality){
        this.translationKey = translationKey;
        this.quality = quality;
    }

    @Override
    public String getStringValue() {
        return this.quality;
    }

    @Override
    public String getDisplayName() {
        return Text.translatable(this.translationKey).getString();
    }

    @Override
    public IConfigOptionListEntry cycle(boolean forward) {
        int id = this.ordinal();

        if (forward) {
            if (++id >= values().length) {
                id = 0;
            }
        } else {
            if (--id < 0) {
                id = values().length - 1;
            }
        }

        return values()[id % values().length];
    }

    @Override
    public IConfigOptionListEntry fromString(String value) {
        for (Quality mode : Quality.values()) {
            if (!mode.getDisplayName().equals(value)) {
                continue;
            }

            return mode;
        }

        return Quality.EXHIGH;
    }
}
