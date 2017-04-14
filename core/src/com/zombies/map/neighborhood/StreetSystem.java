package com.zombies.map.neighborhood;

import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.interfaces.Streets.StreetConnection;
import com.zombies.interfaces.Streets.StreetNode;
import com.zombies.util.G;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class StreetSystem {
    public final static float GRIDSIZE = C.ZONE_SIZE * 0.5f;
    public final static float MAXGAP = GRIDSIZE * 3;

    public static LinkedHashSet<StreetSystem> systems = new LinkedHashSet<>();

    private Vector2 center;
    private LinkedHashSet<StreetNode>         nodes       = new LinkedHashSet<>();
    private LinkedHashSet<StreetConnection>   connections = new LinkedHashSet<>();

    private float rigidity;
    private float rsm = (float) G.PIHALF; // Rotational Segmentation Factor

    public static void populateBox(Vector2 point, float w, float h, int resolution) {

    }

    public StreetSystem(Vector2 center) {
        this.center = center;
        rigidity    = 0.9f;
        systems.add(this);
        StreetNode node = Intersection.createIntersection(this, center);
    }

    public static StreetSystem closestStreetSystem(Vector2 p) {
        StreetSystem streetSystem = null;
        float dst = 0;
        for (StreetSystem ss : systems) {
            if (streetSystem == null || ss.getCenter().dst(p) < dst) {
                dst = ss.getCenter().dst(p);
                streetSystem = ss;
            }
        }
        return streetSystem;
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
    public StreetNode closestNode(Vector2 p) {
        float dst = 0;
        StreetNode node = null;

        for (StreetNode sn : nodes) {
            float tDst = sn.getPosition().dst(p);
            if (node == null || tDst < dst) {
                node = sn;
                dst = tDst;
            }
        }
        return node;
    }

    public void addConnection(StreetConnection sc) {
        connections.add(sc);
    }
    public void addNode(StreetNode sn) {
        nodes.add(sn);
    }

    public StreetNode[] closestOnCache(int key, Vector2 p, int limit, HashMap<Integer, LinkedHashSet<StreetNode>> index) {
        StreetNode[] result        = new StreetNode[2]; // 0 is closer to origin, 1 is further away
        float[]      dst           = new float[2];
        float        dstFromCenter = p.dst(center);
        int          lower         = key - limit, upper = key + limit;
        for (int i = lower; i <= upper; i++) {
            LinkedHashSet<StreetNode> cache = index.get(i);
            if (cache == null) continue;
            for (StreetNode n : cache) {
                if (n.dstFromCenter() < dstFromCenter) {
                    float tDst = n.getPosition().dst(p);
                    if (!(result[0] == null || tDst < dst[0]))
                        continue;

                    dst[0]    = tDst;
                    result[0] = n;
                } else if (n.dstFromCenter() > dstFromCenter) {
                    float tDst = n.getPosition().dst(p);
                    if (!(result[1] == null || tDst < dst[1]))
                        continue;

                    dst[1]    = tDst;
                    result[1] = n;
                }
            }
        }
        return result;
    }

    private LinkedHashSet<StreetNode> getCache(HashMap<Integer, LinkedHashSet<StreetNode>> c, int i) {
        LinkedHashSet<StreetNode> cache = c.get(i);
        if (cache == null) {
            cache = new LinkedHashSet<>();
            c.put(i, cache);
            return cache;
        }

        return cache;
    }


    private String sKey(int[] key) { return key[0] + "," + key[1]; }
    private String sKey(Vector2 p) { return (int) Math.floor(p.x / GRIDSIZE) + "," + (int) Math.floor(p.y / GRIDSIZE); }
    private String sKey(StreetNode sn) { return sKey(sn.getPosition()); }

    public Vector2 getCenter() { return center; }
    public LinkedHashSet<StreetNode> getNodes() { return nodes; }
    public LinkedHashSet<StreetConnection> getConnections() { return connections; }

    public HashSet<StreetConnection> connectionsInRadius(Vector2 p, float r) {
        HashSet<StreetConnection> result = new HashSet<>();
        for (StreetConnection sc : connections)
            if (sc.getP1().dst(p) < r || sc.getP2().dst(p) < r)
                result.add(sc);
        return result;
    }
    public HashSet<StreetNode> nodesInRadius(Vector2 p, float r) {
        HashSet<StreetNode> result = new HashSet<>();
        for (StreetNode sn : nodes)
            if (sn.getPosition().dst(p) < r)
                result.add(sn);
        return result;
    }
}
