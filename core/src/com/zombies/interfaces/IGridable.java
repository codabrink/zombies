package com.zombies.interfaces;

import com.zombies.map.building.Building;
import java.util.LinkedList;

public interface IGridable {
    Building getBuilding();
    LinkedList<int[]> getOpenAdjKeys();
    int[] getKey();
}
