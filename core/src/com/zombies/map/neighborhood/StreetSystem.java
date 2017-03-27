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

    private double                            orientation = 0;

    public static void populateBox(Vector2 p, float w, float h, float resolution) {
        StreetSystem ss = closestStreetSystem(p);
        p = p.cpy();
        for (float x = p.x; x <= p.x + w; x += resolution) {
            for (float y = p.y; y <= p.y + h; y += resolution) {
                StreetNode node = ss.getClosestNode(new Vector2(x, y), 1);
                if (node != null) continue;

                StreetNode row = ss.closestOnRow(p, 1);
                StreetNode col = ss.closestOnCol(p, 1);

                if (row != null)
                    p.set(p.x, row.getPosition().y);
                if (col != null)
                    p.set(col.getPosition().x, p.y);

                node = new Intersection(ss, p);

                if (row != null)
                    new Street(ss, node, row);
                if (col != null)
                    new Street(ss, node, col);
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
        StreetNode node = nodes.get(sn.getKey());
        if (node != null)
            return node;

        getCache(nodesColindex, sn.getKey()[0]).add(sn);
        getCache(nodesRowIndex, sn.getKey()[1]).add(sn);

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

    public StreetNode closestOnCol(Vector2 p, int limit) {
        StreetNode result = null;
        int[] key = keyOf(p);
        int lower = key[0] - limit, upper = key[0] + limit;
        for (int i = lower; i <= upper; i++) {
            LinkedHashSet<StreetNode> cache = nodesColindex.get(i);
            if (cache == null) continue;
            for (StreetNode n : cache)
                if (result == null || Math.abs(n.getKey()[1] - key[1]) < Math.abs(result.getKey()[1] - key[1]))
                    result = n;
        }
        return result;
    }
    public StreetNode closestOnRow(Vector2 p, int limit) {
        StreetNode result = null;
        int[] key = keyOf(p);
        int lower = key[1] - limit, upper = key[1] + limit;
        for (int i = lower; i <= upper; i++) {
            LinkedHashSet<StreetNode> cache = nodesRowIndex.get(i);
            if (cache == null) continue;
            for (StreetNode n : cache)
                if (result == null || Math.abs(n.getKey()[0] - key[0]) < Math.abs(result.getKey()[0] - key[0]))
                    result = n;
        }
        return result;
    }

    private LinkedHashSet<StreetNode> getCache(HashMap<Integer, LinkedHashSet<StreetNode>> c, int i) {
        LinkedHashSet<StreetNode> cache = c.get(i);
        if (cache == null)
            return c.put(i, new LinkedHashSet<StreetNode>());
        return cache;
    }
    private Vector2 normalize(Vector2 v) { return normalize$(v.cpy()); }
    private Vector2 denormalize(Vector2 v) { return denormalize$(v.cpy()); }
    private Vector2 normalize$(Vector2 v) { return v.sub(center).rotateRad((float) -orientation); }
    private Vector2 denormalize$(Vector2 v) { return v.rotateRad((float) orientation).add(center); }

    public Vector2 getCenter() { return center; }
    public  int[] keyOf(Vector2 p) { return normalizedKey(normalize(p)); }
    private int[] normalizedKey(Vector2 p) { return new int[]{ (int) Math.floor(p.x / GRIDSIZE), (int) Math.floor(p.y / GRIDSIZE) }; }
    private String sKey(int[] key) { return key[0] + "," + key[1]; }
    private String sKey(Vector2 p) { return (int) Math.floor(p.x / GRIDSIZE) + "," + (int) Math.floor(p.y / GRIDSIZE); }
    private String sKey(StreetNode sn) { return sKey(sn.getPosition()); }
}
