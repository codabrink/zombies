package com.zombies.util;

import com.zombies.C;
import java.util.Random;
import java.util.Set;

public class U {
    private static Random random = new Random();
    public static void p(Object s) {
        if (!C.DEBUG) return;

        System.out.println(getCallerClassName() + ": " + s);
    }

    public static < E > Object random(Set<E> set) {
        if (set.size() == 0) return null;

        int index = random.nextInt(set.size());
        int i = 0;
        for (Object o : set) {
            if (i == index)
                return o;
            i++;
        }
        return null;
    }

    private static String getCallerClassName() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i=1; i<stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(U.class.getName()) && ste.getClassName().indexOf("java.lang.Thread")!=0) {
                return ste.getClassName();
            }
        }
        return null;
    }
}
