package com.zombies.map.neighborhood;

import com.badlogic.gdx.math.Vector2;
import com.zombies.interfaces.Streets.StreetConnection;
import com.zombies.interfaces.Streets.StreetNode;
import com.zombies.util.Geom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class StreetSystem {
    private LinkedHashSet<StreetConnection> connections = new LinkedHashSet<>();
    private LinkedHashSet<StreetNode>       nodes       = new LinkedHashSet<>();
    private ArrayList<StreetGrid>           grids       = new ArrayList<>();
    private double                          orientation = Geom.PIHALF;

    public StreetSystem() {
        grids.add(new StreetGrid());
    }

    public StreetConnection closestConnection(Vector2 p) {
        Iterator<StreetConnection> itr  = connections.iterator();
        StreetConnection minConnection  = itr.next();
        float minDistance               = minConnection.distance(p);
        while (itr.hasNext()) {
            StreetConnection connection = itr.next();
            float distance              = connection.distance(p);

            if (distance > minDistance)
                continue;

            minDistance                 = distance;
            minConnection               = connection;
        }
        return minConnection;
    }

    public void addConnection(StreetConnection sc) {
        connections.add(sc);
    }
    public void addNode(StreetNode sn) {
        nodes.add(sn);
    }
}
