package com.pxy.reptile;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;


public class JxBrowserCrack {
    static {
        try {
            Class cls = Class.forName("com.teamdev.jxbrowser.chromium.az");
            Field e = cls.getDeclaredField("e");
            e.setAccessible(true);
            Field f = cls.getDeclaredField("f");
            f.setAccessible(true);
            Field modifierField = Field.class.getDeclaredField("modifiers");
            modifierField.setAccessible(true);
            modifierField.setInt(e, e.getModifiers() & ~Modifier.FINAL);
            modifierField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
            e.set(null, new BigInteger("1"));
            f.set(null, new BigInteger("1"));
            modifierField.setAccessible(false);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }
}
