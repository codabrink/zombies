package com.zombies;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;

//TODO: This isn't used
public class PlayerPulse {
	private GameView view;
	private Player player;
	private boolean setUp = false;
	
	protected LinkedList<Mesh> meshes = new LinkedList<Mesh>();
	protected LinkedList<float[]> verticies = new LinkedList<float[]>();
	
	public PlayerPulse(GameView view, Player player) {
		this.view = view;
		this.player = player;
		
		for (int i=0; i<=0; i++) {
			meshes.add(new Mesh(true, 4, 4,
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.ColorPacked, 4, "a_color")));
			meshes.get(i).setIndices(new short[] {0, 1, 2, 3});
		}
		
	}

	public void draw() {
		if (!setUp) {this.updateVerticies(); setUp=true;}
		for (Mesh mesh: meshes) {
            //TODO temporarily disabling this
			//mesh.draw(GL20.GL_TRIANGLE_STRIP, 0, 4);
		}
	}
	
	public void update() {
		this.updateVerticies();
	}
	
	private void updateVerticies() {
		verticies.clear();
		PerspectiveCamera cam = view.getCamera();
		int width = view.getWidth();
		int height = view.getHeight();
		float percent = ((float)(Math.sin((double)(System.currentTimeMillis()/C.PULSE_RATE)) + 1)) / 2.0f;
		float pulseWidth = 0.2f;
		verticies.add(new float[] {
				cam.position.x - width / 2.0f,                                cam.position.y + height / 2.0f, 0, Color.toFloatBits(255, 0, 0, (int)(percent * 255f)),
                cam.position.x + width / 2.0f,                                cam.position.y + height / 2.0f, 0, Color.toFloatBits(255, 0, 0, (int)(percent * 255f)),
                cam.position.x - width / 2.0f + width * pulseWidth,           cam.position.y + height / 2.0f - (height * pulseWidth), 0, Color.toFloatBits(0, 0, 0, 0),
                cam.position.x + width / 2.0f - width * pulseWidth,           cam.position.y + height / 2.0f - (height * pulseWidth), 0, Color.toFloatBits(0, 0, 0, 0)
		});
//		verticies.add(new float [] {
//				cam.position.x + width - (width * pulseWidth), cam.position.y + height * pulseWidth, 0, Color.toFloatBits(0, 0, 0, 0),
//                cam.position.x + width,                        cam.position.y, 0, Color.toFloatBits(255, 0, 0, (int)(percent * 255f)),
//                cam.position.x + width - (width * pulseWidth), cam.position.y + height - (height * pulseWidth), 0, Color.toFloatBits(0, 0, 0, 0),
//                cam.position.x + width,                        cam.position.y + height, 0, Color.toFloatBits(255, 0, 0, (int)(percent * 255f))
//		});
//		verticies.add(new float [] {
//				cam.position.x + width * pulseWidth,           cam.position.y + height - (height * pulseWidth), 0, Color.toFloatBits(0, 0, 0, 0),
//				cam.position.x + width - (width * pulseWidth), cam.position.y + height - (height * pulseWidth), 0, Color.toFloatBits(0, 0, 0, 0),
//                cam.position.x,                                cam.position.y + height, 0, Color.toFloatBits(255, 0, 0, (int)(percent * 255f)),
//                cam.position.x + width,                        cam.position.y + height, 0, Color.toFloatBits(255, 0, 0, (int)(percent * 255f))
//		});
//		verticies.add(new float [] {
//				cam.position.x,                                cam.position.y, 0, Color.toFloatBits(255, 0, 0, (int)(percent * 255f)),
//				cam.position.x + width * pulseWidth,           cam.position.y + height * pulseWidth, 0, Color.toFloatBits(0, 0, 0, 0),
//                cam.position.x,                                cam.position.y + height, 0, Color.toFloatBits(255, 0, 0, (int)(percent * 255f)),
//                cam.position.x + width * pulseWidth,           cam.position.y + height - (height * pulseWidth), 0, Color.toFloatBits(0, 0, 0, 0)
//		});
		for (int i=0; i<=0; i++) {
			meshes.get(i).setVertices(verticies.get(i));
		}
	}
	
}
