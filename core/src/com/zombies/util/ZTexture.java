package com.zombies.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;

public class ZTexture {
    public Texture texture;
    public TextureAttribute textureAttribute;

    public ZTexture(String path) {
        Assets.a.load(path, Texture.class);
        Assets.a.finishLoading();
        texture = Assets.a.get(path, Texture.class);
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        textureAttribute = new TextureAttribute(Attribute.getAttributeType("diffuseTexture"),
                new TextureDescriptor<Texture>(texture),
                0, 0, 1, 1);
    }
}
