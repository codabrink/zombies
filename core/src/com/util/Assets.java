package com.util;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;

public class Assets {
    public static AssetManager a;
    public static ModelBuilder modelBuilder;
    public static MeshBuilder meshBuilder;
    public static TextureAttribute wildGrassTextureDiffuse;

    public Assets() {
        a = new AssetManager();
        modelBuilder = new ModelBuilder();
        meshBuilder = new MeshBuilder();
        a.load("data/floor1.png", Texture.class);
        a.load("data/texture/wildgrass.jpg", Texture.class);
        a.finishLoading();

        Texture rawWildGrass = a.get("data/texture/wildgrass.jpg", Texture.class);
        rawWildGrass.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        wildGrassTextureDiffuse = new TextureAttribute(Attribute.getAttributeType("diffuseTexture"),
                new TextureDescriptor<Texture>(rawWildGrass),
                0, 0, 5f, 5f);                     // offsetU, offsetV, scaleU, scaleV
    }
}
