package com.zombies.map.neighborhood;

import com.zombies.interfaces.Streets.StreetNode;
import com.zombies.util.G;

import java.util.LinkedHashMap;

public class StreetGrid {
    public LinkedHashMap<Integer, StreetNode> grid = new LinkedHashMap<>();
    public double orientation = G.PIHALF;

    public StreetGrid() {
    }
    public StreetGrid(double orientation) {
        this.orientation = orientation;
    }
}
