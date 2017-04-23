package com.zombies.util;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.zombies.map.building.room.Room;

import java.util.HashMap;

public class Assets {
    public static AssetManager a = new AssetManager();
    public static ModelLoader loader = new ObjLoader();

    public static TextureAttribute wildGrassTextureDiffuse, floor1Diffuse;

    public static HashMap<Room.RoomType, ZTexture> roomFloorTextures = new HashMap<>();

    public enum MATERIAL {
        GRASS ("grass", "data/texture/wildgrass.jpg", 10),
        GREEN_TILE ("greentile", "data/room/floor/kitchen.jpg"),
        FLOOR_CARPET ("floorcarpet", "data/room/floor/living_room.jpg"),
        FLOOR_WOOD ("floorwood", "data/room/floor/dining_room.jpg"),
        STREET ("street", "data/neighborhood/street.jpg"),
        WALL_WHITE_WALLPAPER ("whitewallpaper", "data/room/wall/wall.jpg"),
        WALL_PAINTED_RED ("wallpaintedred", "data/room/wall/painted_red.jpg"),
        SIDING_BEIGE_VINYL("beigevinyl", "data/room/building/vinyl-beige.jpg"),
        SIDING_BRICK("sidingbrick", "data/room/wall/brick.jpg");

        public ZTexture texture;
        public String partName;
        MATERIAL(String partName, String path) {
            this(partName, path, 1);
        }
        MATERIAL(String partName, String path, int UVScale) {
            this.partName = partName;
            texture = new ZTexture(path, UVScale);
        }
        MATERIAL(String partName, MATERIAL m) {
            this.partName = partName;
        }
    }

    public Assets() {
        a.load("data/texture/wildgrass.jpg", Texture.class);
        a.load("data/room/wall/wall.jpg", Texture.class);
        a.load("data/models/wood-table.g3dj", Model.class);
        a.load("data/models/fridge.g3dj", Model.class);
        a.finishLoading();

        Texture rawWildGrass = a.get("data/texture/wildgrass.jpg", Texture.class);
        rawWildGrass.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        wildGrassTextureDiffuse = new TextureAttribute(Attribute.getAttributeType("diffuseTexture"),
                new TextureDescriptor<Texture>(rawWildGrass),
                0, 0, 7f, 7f);                     // offsetU, offsetV, scaleU, scaleV
    }
}
