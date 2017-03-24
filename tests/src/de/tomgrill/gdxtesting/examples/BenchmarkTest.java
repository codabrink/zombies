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

import com.badlogic.gdx.math.Vector2;
import com.zombies.abstract_classes.Overlappable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BenchmarkTest {

	@Test
	public void oneEqualsOne() {
		Overlappable o1 = new Overlappable(new Vector2(0, 0), 100, 100);
		Overlappable o2 = new Overlappable(new Vector2(50, 50), 100, 100);

		assertTrue(o1.overlaps(o2) == true);

		for (int i = 0; i < 1000000; i++) {
			o1.overlaps(o2);
		}
	}
}
