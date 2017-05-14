package com.zombies.map.building.room;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.zombies.map.building.Box;
import com.zombies.map.building.objects.Fridge;
import com.zombies.map.building.objects.Table;
import com.zombies.lib.U;

import java.util.HashSet;

public class Kitchen extends Room {
    protected Kitchen(com.zombies.map.building.Building building, int[] key, int maxBoxes, RoomType roomType) {
        super(building, key, maxBoxes, roomType);
    }

    @Override
    protected void generate(int[] key, int maxBoxes) {
        super.generate(key, maxBoxes);

        Box b = ((Box) U.random(boxes));
        if (b == null)
            return;
        addObject(new Table(b.getCenter(), 0));

        b = ((Box) U.random(boxes));
        if (b == null)
            return;
        addObject(new Fridge(b));

        b = ((Box) U.random(boxes));
        if (b == null)
            return;
    }

    private void addObject(Object o) {
        if (o == null) return;
        HashSet<Object> set = objectMap.get(o.getClass());
        if (set == null) {
            set = new HashSet<>();
            objectMap.put(o.getClass(), set);
        }
        set.add(o);
    }
}
