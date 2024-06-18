package uk.co.forgottendream.vfbackports.util;

import net.minecraft.util.math.intprovider.UniformIntProvider;

import java.util.concurrent.TimeUnit;

public class ModTimeHelper {
    public static final long SECOND_IN_MILLIS;
    public static final long HOUR_IN_SECONDS;
    public static final int MINUTE_IN_SECONDS;

    public ModTimeHelper() {
    }

    public static UniformIntProvider betweenSeconds(int min, int max) {
        return UniformIntProvider.create(min * 20, max * 20);
    }

    static {
        SECOND_IN_MILLIS = TimeUnit.SECONDS.toMillis(1L);
        HOUR_IN_SECONDS = TimeUnit.HOURS.toSeconds(1L);
        MINUTE_IN_SECONDS = (int) TimeUnit.MINUTES.toSeconds(1L);
    }
}
