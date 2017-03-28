package com.zombies.map.neighborhood;

import com.badlogic.gdx.math.Vector2;
import com.zombies.C;
import com.zombies.interfaces.Streets.StreetConnection;
import com.zombies.interfaces.Streets.StreetNode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class StreetSystem {
    public final static float GRIDSIZE = C.GRIDSIZE * 10;
    public final static float MAXGAP = GRIDSIZE * 3;

    public static LinkedHashSet<StreetSystem> systems = new LinkedHashSet<>();

    private Vector2 center;
    private LinkedHashSet<StreetConnection>   connections = new LinkedHashSet<>();

    private LinkedHashMap<String, StreetNode>           nodes         = new LinkedHashMap<>();
    private HashMap<Integer, LinkedHashSet<StreetNode>> nodesColindex = new HashMap<>();
    private HashMap<Integer, LinkedHashSet<StreetNode>> nodesRowIndex = new HashMap<>();

    private double orientation = 0;

    public static void populateBox(Vector2 point, float w, float h, float resolution) {
        StreetSystem ss = closestStreetSystem(point);
        for (float x = point.x; x <= point.x + w; x += resolution) {
            for (float y = point.y; y <= point.y + h; y += resolution) {
                Vector2 p = new Vector2(x, y);
                StreetNode node = ss.getClosestNode(p, 1);
                if (node != null) continue;

                StreetNode[] row = ss.closestOnRow(p, 0);
                StreetNode[] col = ss.closestOnCol(p, 0);

                if (row[0] != null)
                    p.set(p.x, row[0].getPosition().y);
                if (col[0] != null)
                    p.set(col[0].getPosition().x, p.y);

                node = Intersection.createIntersection(ss, p);

                if (row[0] != null)
                    Street.createStreet(ss, node, row[0]);
                if (row[1] != null)
                    Street.createStreet(ss, node, row[1]);
                if (col[0] != null)
                    Street.createStreet(ss, node, col[0]);
                if (col[1] != null)
                    Street.createStreet(ss, node, col[1]);
            }
        }
    }

    public StreetSystem(Vector2 center) {
        this.center = center;
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

    public void addConnection(StreetConnection sc) {
        connections.add(sc);
    }
    public StreetNode addNode(StreetNode sn) {
        StreetNode node = nodes.get(sKey(sn.getKey()));
        if (node != null)
            return node;

        getCache(nodesColindex, sn.getKey()[0]).add(sn);
        getCache(nodesRowIndex, sn.getKey()[1]).add(sn);

        sn.getZone().addObject(sn);
        return nodes.put(sKey(sn.getKey()), sn);
    }
    public StreetNode getNode(Vector2 p) {
        return nodes.get(sKey(p));
    }
    public StreetNode getClosestNode(Vector2 p, int limit) {
        int[] key = keyOf(p);
        StreetNode node = null;
        float dst = 0;

        for (int y = -limit; y <= limit; y++) {
            for (int x = -limit; x <= limit; x++) {
                StreetNode sn = nodes.get((key[0]+x) + "," + (key[1]+y));
                if (sn == null) continue;
                if (node == null || node.getPosition().dst(p) < dst) {
                    node = sn;
                    dst  = node.getPosition().dst(p);
                }
            }
        }
        return node;
    }


    public StreetNode[] closestOnRow(Vector2 p, int limit) {
        return closestOnCache(p, limit, nodesRowIndex);
    }
    public StreetNode[] closestOnCol(Vector2 p, int limit) {
        return closestOnCache(p, limit, nodesColindex);
    }
    public StreetNode[] closestOnCache(Vector2 p, int limit, HashMap<Integer, LinkedHashSet<StreetNode>> index) {
        int[]        key           = keyOf(p);
        StreetNode[] result        = new StreetNode[2]; // 0 is closer to origin, 1 is further away
        float[]      dst           = new float[2];
        float        dstFromCenter = p.dst(center);
        int          lower         = key[1] - limit, upper = key[1] + limit;
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
    private Vector2 normalize(Vector2 v) { return normalize$(v.cpy()); }
    private Vector2 denormalize(Vector2 v) { return denormalize$(v.cpy()); }
    private Vector2 normalize$(Vector2 v) { return v.sub(center).rotateRad((float) -orientation); }
    private Vector2 denormalize$(Vector2 v) { return v.rotateRad((float) orientation).add(center); }

    public  int[] keyOf(Vector2 p) { return normalizedKey(normalize(p)); }
    private int[] normalizedKey(Vector2 p) { return new int[]{ (int) Math.floor(p.x / GRIDSIZE), (int) Math.floor(p.y / GRIDSIZE) }; }
    private String sKey(int[] key) { return key[0] + "," + key[1]; }
    private String sKey(Vector2 p) { return (int) Math.floor(p.x / GRIDSIZE) + "," + (int) Math.floor(p.y / GRIDSIZE); }
    private String sKey(StreetNode sn) { return sKey(sn.getPosition()); }

    public Vector2 getCenter() { return center; }
    public LinkedHashMap<String, StreetNode> getNodes() { return nodes; }
    public LinkedHashSet<StreetConnection> getConnections() { return connections; }
}
