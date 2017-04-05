package com.zombies.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.utils.Array;

public class ZTexture {
    public String path;
    public Texture texture;
    public TextureRegion textureRegion;

    public Array<Attribute> attributes = new Array<>();

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
        attributes.add(new TextureAttribute(TextureAttribute.Diffuse,
                new TextureDescriptor<>(texture),
                0, 0, UVScale, UVScale));

        String[] file        = path.split("\\.");
        String normalPath    = file[0] + "-normal." + file[1];
        boolean normalExists = Gdx.files.local(normalPath).exists();
        String bumpPath      = file[0] + "-bump." + file[1];
        boolean bumpExists   = Gdx.files.local(bumpPath).exists();

        if (normalExists) {
            Assets.a.load(normalPath, Texture.class);
            Assets.a.finishLoading();

            attributes.add(new TextureAttribute(TextureAttribute.Normal,
                    new TextureDescriptor<>(Assets.a.get(normalPath, Texture.class)),
                    0, 0, UVScale, UVScale));
        }
        if (bumpExists) {
            Assets.a.load(bumpPath, Texture.class);
            Assets.a.finishLoading();

            attributes.add(new TextureAttribute(TextureAttribute.Bump,
                    new TextureDescriptor<>(Assets.a.get(bumpPath, Texture.class)),
                    0, 0, UVScale, UVScale));
        }

    }
}
