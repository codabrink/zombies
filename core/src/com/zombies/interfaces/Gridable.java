package com.zombies.interfaces;

import com.zombies.map.building.Building;
import java.util.ArrayList;

public interface Gridable {
    Building getBuilding();
    ArrayList<int[]> getOpenAdjKeys();
    int[] getKey();
}
