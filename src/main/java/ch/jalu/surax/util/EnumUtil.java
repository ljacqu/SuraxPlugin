package ch.jalu.surax.util;

import javax.annotation.Nullable;

public final class EnumUtil {

    private EnumUtil() {
    }

    @Nullable
    public static <E extends Enum<E>> E toEnum(Class<E> clazz, String entry) {
        for (E e : clazz.getEnumConstants()) {
            if (entry.equalsIgnoreCase(e.name())) {
                return e;
            }
        }
        return null;
    }
}
