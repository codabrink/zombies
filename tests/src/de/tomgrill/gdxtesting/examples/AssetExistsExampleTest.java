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

import com.zombies.GameView;
import com.zombies.Zombies;
import com.zombies.Zone;
import com.zombies.map.MapGen;

import de.tomgrill.gdxtesting.GdxTestRunner;

@RunWith(GdxTestRunner.class)
public class AssetExistsExampleTest {

	@Test
	public void badlogicLogoFileExists() {
		Zombies instance = new Zombies();
		instance.setScreen(new GameView());

		Zone z = Zone.getZone(0f, 0f);
		MapGen.genRoom(z);

		assertTrue(z.getRooms().size() > 0);
	}
}
