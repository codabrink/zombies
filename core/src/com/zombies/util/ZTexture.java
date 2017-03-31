package com.zombies.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;

public class ZTexture {
    public String path;
    public Texture texture;
    public TextureRegion textureRegion;
    public TextureAttribute textureAttribute;

    public ZTexture(String path) {
        this(path, 1);
    }

    public ZTexture(String path, int UVScale) {
        this.path = path;
        Assets.a.load(path, Texture.class);
        Assets.a.finishLoading();
        texture = Assets.a.get(path, Texture.class);
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        textureRegion = new TextureRegion(texture);
        textureAttribute = new TextureAttribute(Attribute.getAttributeType("diffuseTexture"),
                new TextureDescriptor<>(texture),
                0, 0, UVScale, UVScale);
    }
}
