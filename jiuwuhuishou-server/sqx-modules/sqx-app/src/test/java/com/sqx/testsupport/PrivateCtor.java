package com.sqx.testsupport;

import java.lang.reflect.Constructor;

/** 触发 Defaults 私有构造，补齐 JaCoCo LINE。 */
public final class PrivateCtor {

    private PrivateCtor() {
    }

    public static void invoke(Class<?> type) {
        try {
            Constructor<?> ctor = type.getDeclaredConstructor();
            ctor.setAccessible(true);
            ctor.newInstance();
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
