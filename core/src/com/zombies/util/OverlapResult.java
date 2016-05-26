package com.zombies.util;

import com.zombies.interfaces.Overlappable;

public class OverlapResult {
    public static enum OverlapType {
        CORNER, HORIZ_TOP, HORIZ_BOTTOM, VERT_LEFT, VERT_RIGHT, FULL, NONE
    }
    public Overlappable overlappable;
    public OverlapType overlapType;

    public OverlapResult(Overlappable o, OverlapType ot) {
        overlappable = o;
        overlapType = ot;
    }
}
