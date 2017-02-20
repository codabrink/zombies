package com.zombies.map.room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.zombies.C;
import com.zombies.GameView;
import com.zombies.Unit;
import com.zombies.Zombies;
import com.zombies.Zone;
import com.zombies.interfaces.Drawable;
import com.zombies.interfaces.HasZone;
import com.zombies.interfaces.Loadable;
import com.zombies.interfaces.Modelable;
import com.zombies.interfaces.Updateable;
import com.zombies.util.Assets;

public class Room implements Loadable, HasZone, Drawable, Modelable, Updateable {
    public static int roomCount = 0;

    private int id;
    public  HashSet<Box> boxes = new HashSet<>();
    private boolean finalized = false;
    public boolean connected = false;
    private ArrayList<Wall> walls = new ArrayList<Wall>();
    private HashSet<DoorContainer> doorContainers = new HashSet<>();
    private Random random = new Random();
    private boolean alarmed = false;
    private Zone zone;
    private Vector2 center;
    private Thread doorCalcThread;

    private Building building;
    public HashMap<String, HashMap<String, Box[]>> doors = new HashMap<>();

    private Model wallModel, floorModel;
    private ModelInstance wallModelInstance, floorModelInstance;

    public Room(Building building) {
        this.building = building;
        id = roomCount;
        roomCount++;
    }

    public void finalize() {
        center = calculateMedian();
        zone = Zone.getZone(center);
        zone.addObject(this);

        for (Box b: boxes)
            b.setRoom(this);

        building.associateBoxes();

        buildFloorModel();
        rasterizeWalls();
        handleZoning();

        building.refresh();

        // Calculate where doors should be in a separate thread
        Runnable runnable     = new CalculateDoors(this);
        doorCalcThread = new Thread(runnable);
        doorCalcThread.start();

        finalized = true;
    }

    private void handleZoning() {
        HashSet<Zone> zones = new HashSet<>();
        for (Box b : getBoxes())
            for (Vector2 v : b.getCorners())
                zones.add(Zone.getZone(v).addObject(b));
        for (Zone z : zones)
            z.addObject(this);
    }

    // calculates the median position of all of the boxes
    private Vector2 calculateMedian() {
        Vector2 center = new Vector2(0, 0);
        for (Box b: boxes)
            center.add(b.getCenter());
        return new Vector2(center.x / boxes.size(), center.y / boxes.size());
    }

    public void currentRoom() {
        load(); // load self
    }

    public void load() {
        for (Box b : boxes)
            b.load();
        for (Wall w: walls)
            w.load();
    }

    public void unload() {
        for (Box b: boxes) {
            b.unload();
        }
        for (Wall w: walls)
            w.unload();
    }

    public void alarm(Unit victim) {
        if (!alarmed) {
            for (Box b: boxes) {
                for (Unit u: b.getUnits()) {
                    if (random.nextBoolean()) {
                        u.sick(victim);
                    }
                }
            }
            alarmed = true;
        }
    }

    public void rasterizeWalls() {
        // proposedPositions are sets of points where walls could be placed.
        ArrayList<Vector2[]> proposedPositions = new ArrayList<>();

        // propose positions for each box in the room.
        for (Box b: boxes) {
            proposedPositions.addAll(b.proposeWallPositions());
        }
        
        proposedPositions = consolidateWallPositions(proposedPositions);

        for (Vector2[] pstn: proposedPositions) {
            for (Zone z : Zone.zonesOnLine(pstn[0], pstn[1]))
                for (Wall w : z.getWalls())
                    if (w.similar(pstn[0], pstn[1]))
                        continue;
            walls.add(new Wall(pstn[0], pstn[1], this));
        }

        buildWallModel();
    }
    
    // consolidate the proposed walls into as few as possible.
    public ArrayList<Vector2[]> consolidateWallPositions(ArrayList<Vector2[]> proposedPositions) {

        ArrayList<Vector2[]> iteratedPositions = new ArrayList<>(proposedPositions);

        for (Vector2[] pstn1: iteratedPositions) {
            for (Vector2[] pstn2: iteratedPositions) {

                // if the first wall's end meets the other wall's start and they have the same
                // angle...
                if (pstn1[1].equals(pstn2[0]) && Math.abs(pstn1[1].cpy().sub(pstn1[0]).angle() - (pstn2[1].cpy().sub(pstn2[0]).angle())) < 0.0001) {
                    Vector2[] points = new Vector2[2];
                    points[0] = pstn1[0];
                    points[1] = pstn2[1];

                    proposedPositions.add(points);

                    proposedPositions.remove(pstn1);
                    proposedPositions.remove(pstn2);

                    // keep going until no more matched walls are found.
                    proposedPositions = consolidateWallPositions(proposedPositions);
                    return proposedPositions;
                }
            }
        }

        return proposedPositions;
    }

    public Unit findUnit(Body b) {
        for (Box box: boxes) {
            for (Unit u: box.getUnits()) {
                if (u.getBody() == b)
                    return u;
            }
        }
        return null;
    }

    public ArrayList<Wall> getWalls() { return walls; }
    public HashSet<Box> getBoxes() {
        return boxes;
    }
    public Building getBuilding() { return building; }

    public void buildWallModel() {
        Assets.modelBuilder.begin();
        MeshPartBuilder wallBuilder = Assets.modelBuilder.part("Walls",
                GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                new Material(ColorAttribute.createDiffuse(Color.WHITE)));
        for (Wall w: walls)
            w.buildWallMesh(wallBuilder, center);
        MeshPartBuilder frameBuilder = Assets.modelBuilder.part("DoorFrames",
                GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                new Material(ColorAttribute.createDiffuse(Color.BROWN)));
        for (DoorContainer dc : doorContainers)
            dc.getDoorFrame().buildMesh(frameBuilder, center);
        wallModel = Assets.modelBuilder.end();
        wallModelInstance = new ModelInstance(wallModel);
        wallModelInstance.transform.setTranslation(center.x, center.y, 0);
    }

    public void buildFloorModel() {
        Assets.modelBuilder.begin();
        MeshPartBuilder floorBuilder = Assets.modelBuilder.part("floor",
                GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,
                new Material(Assets.floor1Diffuse));
        for (Box b: boxes) {
            b.buildFloorMesh(floorBuilder, center);
        }
        floorModel = Assets.modelBuilder.end();
        floorModelInstance = new ModelInstance(floorModel);
        floorModelInstance.transform.setTranslation(center.x, center.y, 1);
    }

    public int getId() { return id; }
    public HashSet<Box> getOuterBoxes() {
        HashSet<Box> outerBoxes = new HashSet<>();
        // TODO: expensive
        for (Box b : boxes) {
            if (b.getOpenAdjKeys().size() > 0)
                outerBoxes.add(b);
        }
        return outerBoxes;
    }

    private void generateDoor(Map.Entry pair) {
        System.out.println((String)pair.getKey());
    }

    @Override
    public Zone getZone() {
        return zone;
    }

    @Override
    public void setZone(Zone z) {
        // Zone is set in the constructor
    }

    @Override
    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, ModelBatch modelBatch) {
        modelBatch.begin(GameView.gv.getCamera());
        modelBatch.render(floorModelInstance, GameView.environment);
        modelBatch.render(wallModelInstance, GameView.environment);
        modelBatch.end();

        if (C.DEBUG) {
            BitmapFont f = Zombies.getFont("sans-reg:8:white");
            String s = "";

            spriteBatch.begin();
            for (Box b : boxes) {
                if (C.DEBUG_SHOW_BOXMAP)
                    //s = b.getBMLocation();
                if (C.DEBUG_SHOW_ADJBOXCOUNT)
                    s = b.getAdjBoxes().size() + "";
                f.draw(spriteBatch, s, b.getPosition().x + C.BOX_DIAMETER / 2, b.getPosition().y + C.BOX_DIAMETER / 2);
            }
            spriteBatch.end();
        }
    }

    @Override
    public void rebuildModel() {
        buildWallModel();
    }

    public String giveKey(Room r) {
        return Math.min(id, r.getId()) + "," + Math.max(id, r.getId());
    }

    @Override
    public void update() {
        if (doorCalcThread != null && !doorCalcThread.isAlive()) {
            for (HashMap<String, Box[]> roomConnections : doors.values()) {
                Iterator itr = roomConnections.entrySet().iterator();
                while (itr.hasNext()) {
                    Map.Entry pair = (Map.Entry)itr.next();
                    String key = (String)pair.getKey();
                    if (key.charAt(0) != 'u')
                        continue;

                    generateDoor(pair);
                }
            }
            doorCalcThread = null;
        }
    }
}

class CalculateDoors implements Runnable {
    private Room room;
    public CalculateDoors(Room r) {
        room = r;
    }
    public void run() {
        HashSet<Room> adjRooms = new HashSet<>();
        HashMap<String, HashMap<String, Box[]>> potentialConnections = new HashMap<>();
        Random rand = new Random();

        HashMap<String, Box[]> doorMap;
        for (Box b1 : room.getBoxes()) {
            for (Box b2 : b1.getAdjBoxes()) {
                if (b2.getRoom() != room)
                    adjRooms.add(b2.getRoom());

                String roomKey = room.giveKey(b2.getRoom());
                if (potentialConnections.get(roomKey) == null)
                    potentialConnections.put(roomKey, new HashMap<String, Box[]>());

                doorMap = potentialConnections.get(roomKey);
                String boxKey = b1.giveKey(b2);
                if (doorMap.get(boxKey) == null)
                    doorMap.put(boxKey, new Box[]{b1, b2});
            }
        }

        HashMap<String, Box[]> connectionList;
        Box[] connection;
        for (String roomsKey : potentialConnections.keySet()) {
            connectionList = potentialConnections.get(roomsKey);

            Iterator itr = connectionList.entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry pair = (Map.Entry)itr.next();
                connection = (Box[])pair.getValue();
                Room otherRoom = connection[0].getRoom() == room ? connection[1].getRoom() : connection[0].getRoom();
                if ((!room.connected || !otherRoom.connected) && !itr.hasNext())
                    connectRooms(connection[0], connection[1], roomsKey, (String)pair.getKey());
                else if (rand.nextFloat() < 0.3f)
                    connectRooms(connection[0], connection[1], roomsKey, (String)pair.getKey());
            }
        }
    }

    private void connectRooms(Box b1, Box b2, String roomKey, String boxKey) {
        initRoomConnectionList(b1, b2, roomKey);

        // do not generate door twice
        if (checkDoorExistence(b1, roomKey, boxKey))
            return;

        // u stands for "un-generated"
        b1.getRoom().doors.get(roomKey).put("u" + boxKey, new Box[]{b1, b2});
        b2.getRoom().doors.get(roomKey).put("u" + boxKey, new Box[]{b2, b1});

        if (b1.getRoom().connected == true)
            b2.getRoom().connected = true;
        if (b2.getRoom().connected == true)
            b1.getRoom().connected = true;
    }

    // true - exists, false - doesn't exist
    private boolean checkDoorExistence(Box b, String roomKey, String boxKey) {
        if (b.getRoom().doors.get(roomKey).get("u" + boxKey) != null)
            return true;
        if (b.getRoom().doors.get(roomKey).get(boxKey) != null)
            return true;
        return false;
    }

    private void initRoomConnectionList(Box b1, Box b2, String roomKey) {
        if (b1.getRoom().doors.get(roomKey) == null)
            b1.getRoom().doors.put(roomKey, new HashMap<String, Box[]>());
        if (b2.getRoom().doors.get(roomKey) == null)
            b2.getRoom().doors.put(roomKey, new HashMap<String, Box[]>());
    }
}