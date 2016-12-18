package com.zombies.map.data.join;

import com.badlogic.gdx.math.Vector2;
import com.zombies.abstract_classes.Overlappable;
import com.zombies.interfaces.IOverlappable;
import com.zombies.map.Hallway;

public class JoinOverlappableOverlappable {
    public Vector2 point;
    public IOverlappable o1, o2;
    public Hallway hallway;

    public JoinOverlappableOverlappable(Vector2 point, Overlappable o1, Overlappable o2) {
        this.point   = point;
        this.o1      = o1;
        this.o2      = o2;

    }
}
