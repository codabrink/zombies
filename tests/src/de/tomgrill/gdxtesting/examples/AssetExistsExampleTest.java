/*******************************************************************************
 * Copyright 2015 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package de.tomgrill.gdxtesting.examples;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.zombies.C;
import com.zombies.GameView;
import com.zombies.Zombies;
import com.zombies.Zone;
import com.zombies.map.MapGen;

import java.util.HashMap;
import java.util.HashSet;

import de.tomgrill.gdxtesting.GdxTestRunner;

@RunWith(GdxTestRunner.class)
public class AssetExistsExampleTest {

	@Test
	public void getAdjZone() {
		Zombies instance = new Zombies();
		instance.setScreen(new GameView());

		Zone zone = Zone.getZone(0f, 0f);
		MapGen.genRoom(zone);

        // assert a room is generating
		assertTrue(zone.getRooms().size() > 0);

        // assert getAdjZones works
        HashSet<Zone> adjZones = zone.getAdjZones(1);
        HashMap<String, Zone> adjZonesMap = new HashMap<String, Zone>();
        assertTrue(adjZones.size() == 9);

        for (Zone z : adjZones) {
            int x = (int)(z.getPosition().x / C.ZONE_SIZE);
            int y = (int)(z.getPosition().y / C.ZONE_SIZE);
            adjZonesMap.put(x+","+y, z);
        }

        assertTrue(adjZonesMap.get("-1,-1") != null);
        assertTrue(adjZonesMap.get("0,-1") != null);
        assertTrue(adjZonesMap.get("1,-1") != null);
        assertTrue(adjZonesMap.get("-1,0") != null);
        assertTrue(adjZonesMap.get("0,0") != null);
        assertTrue(adjZonesMap.get("1,0") != null);
        assertTrue(adjZonesMap.get("-1,1") != null);
        assertTrue(adjZonesMap.get("0,1") != null);
        assertTrue(adjZonesMap.get("1,1") != null);

        adjZones = zone.getAdjZones(2);
        assertTrue(adjZones.size() == 25);
    }
}
