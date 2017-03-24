package com.zombies.map.neighborhood;

import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.interfaces.Streets.StreetConnection;
import com.zombies.interfaces.Streets.StreetNode;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class StreetSystem {
    public final static float GRIDSIZE = C.GRIDSIZE * 10;

    public Vector2 center;
    private LinkedHashSet<StreetConnection>   connections = new LinkedHashSet<>();
    private LinkedHashMap<String, StreetNode> nodes       = new LinkedHashMap<>();
    private double                            orientation = 0;
    public StreetSystem(Vector2 center) {
        this.center = center;
        StreetNode node = Intersection.createIntersection(this, center);
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
    public StreetNode addNode(StreetNode sn) {
        StreetNode node = nodes.get(sn.getKey());
        if (node != null)
            return node;
        return nodes.put(sKey(sn.getKey()), sn);
    }
    public StreetNode getNode(Vector2 p) {
        return nodes.get(sKey(p));
    }

    public StreetNode closestOnCol(Vector2 p, int limit) {
        StreetNode result = null;
        int[] key = key(p);
        for (StreetNode n : nodes.values())
            if (n.getKey()[0] >= key[0] - limit && n.getKey()[0] <= key[0] + limit)
                if (result == null || Math.abs(n.getKey()[1] - key[1]) < Math.abs(result.getKey()[1] - key[1]))
                    result = n;
        return result;
    }
    public StreetNode closestOnRow(Vector2 p, int limit) {
        StreetNode result = null;
        int[] key = key(p);
        for (StreetNode n : nodes.values())
            if (n.getKey()[1] >= key[1] - limit && n.getKey()[1] <= key[1] + limit)
                if (result == null || Math.abs(n.getKey()[0] - key[0]) < Math.abs(result.getKey()[0] - key[0]))
                    result = n;
        return result;
    }

    private Vector2 normalize(Vector2 v) { return normalize$(v.cpy()); }
    private Vector2 denormalize(Vector2 v) { return denormalize$(v.cpy()); }
    private Vector2 normalize$(Vector2 v) { return v.sub(center).rotateRad((float) -orientation); }
    private Vector2 denormalize$(Vector2 v) { return v.rotateRad((float) orientation).add(center); }

    public  int[] key(Vector2 p) { return normalizedKey(normalize(p)); }
    private int[] normalizedKey(Vector2 p) { return new int[]{ (int) Math.floor(p.x / GRIDSIZE), (int) Math.floor(p.y / GRIDSIZE) }; }
    private String sKey(int[] key) { return key[0] + "," + key[1]; }
    private String sKey(Vector2 p) { return (int) Math.floor(p.x / GRIDSIZE) + "," + (int) Math.floor(p.y / GRIDSIZE); }
    private String sKey(StreetNode sn) { return sKey(sn.getPosition()); }
}
