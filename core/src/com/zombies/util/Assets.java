package com.zombies.util;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.zombies.map.room.Room;

import java.util.HashMap;

public class Assets {
    public static AssetManager a;
    public static ModelBuilder modelBuilder;
    public static MeshBuilder meshBuilder;
    public static Texture floor1Texture;
    public static TextureAttribute wildGrassTextureDiffuse, floor1Diffuse;

    public static HashMap<Room.RoomType, ZTexture> roomFloorTextures = new HashMap<>();

    public Assets() {
        a = new AssetManager();
        modelBuilder = new ModelBuilder();
        meshBuilder = new MeshBuilder();

        roomFloorTextures.put(Room.RoomType.KITCHEN, new ZTexture("data/room/floor/kitchen.png"));
        roomFloorTextures.put(Room.RoomType.LIVING_ROOM, new ZTexture("data/room/floor/living_room.jpg"));

        a.load("data/texture/wildgrass.jpg", Texture.class);
        a.finishLoading();

        Texture rawWildGrass = a.get("data/texture/wildgrass.jpg", Texture.class);
        rawWildGrass.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        wildGrassTextureDiffuse = new TextureAttribute(Attribute.getAttributeType("diffuseTexture"),
                new TextureDescriptor<Texture>(rawWildGrass),
                0, 0, 7f, 7f);                     // offsetU, offsetV, scaleU, scaleV
    }
}
