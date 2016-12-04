package com.zombies.util;

import com.zombies.C;

public class U {
    public static void p(String s) {
        if (!C.DEBUG) return;

        System.out.println(getCallerClassName() + ": " + s);
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
