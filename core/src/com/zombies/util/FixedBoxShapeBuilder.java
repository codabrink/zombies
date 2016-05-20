package com.zombies.util;

import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.collision.BoundingBox;

public class FixedBoxShapeBuilder extends BoxShapeBuilder {
    public static void build(MeshPartBuilder builder, BoundingBox box) {
        builder.box(box.getCorner000(obtainV3()), box.getCorner010(obtainV3()), box.getCorner100(obtainV3()), box.getCorner110(obtainV3()),
                box.getCorner001(obtainV3()), box.getCorner011(obtainV3()), box.getCorner101(obtainV3()), box.getCorner111(obtainV3()));
        freeAll();
    }
}
