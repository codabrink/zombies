package com.zombies.map.neighborhood;

import com.badlogic.gdx.math.Vector2;
import com.zombies.interfaces.Streets.StreetConnection;
import com.zombies.interfaces.Streets.StreetNode;

import java.util.Iterator;
import java.util.LinkedHashSet;

public class StreetSystem {
    private LinkedHashSet<StreetConnection> connections = new LinkedHashSet<>();
    private LinkedHashSet<StreetNode>       nodes       = new LinkedHashSet<>();

    public StreetSystem() {

    }

    public void addConnection(StreetConnection sc) {
        connections.add(sc);
    }

    public StreetConnection closestConnection(Vector2 p) {
        Iterator<StreetConnection> itr = connections.iterator();
        StreetConnection minConnection = itr.next();
        float minDistance              = minConnection.distance(p);
        while (itr.hasNext()) {
            StreetConnection connection = itr.next();
            float distance              = connection.distance(p);

            if (distance > minDistance)
                continue;

            minDistance   = distance;
            minConnection = connection;
        }
        return minConnection;
    }
}
