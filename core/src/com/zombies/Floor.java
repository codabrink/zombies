package com.zombies;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;

public class Floor {
	
	private Mesh squareMesh;
	private float[] verticies;
	private C c;
	private Box box;
	private float size;
	private int updateInt;
	private Random random = new Random();
	private GameView view;
	private Texture t;
    private ShaderProgram shaderProgram;

    private String vertexShader =
            "attribute vec4 vPosition; 		\n" +
                    "void main()					\n" +
                    "{								\n" +
                    "	gl_Position = vPosition;	\n" +
                    "}								\n";
    private String fragmentShader =
                    "#ifdef GL_ES 								\n"+
                    "precision mediump float;					\n"+
                    "#endif 									\n"+
                    "void main()								\n"+
                    "{											\n"+
                    "	gl_FragColor = vec4(1.0,0.0,0.0,1.0);	\n"+
                    "}											\n";

	public Floor(GameView view, Box box) {
		this.c = view.c;
		this.box = box;
		this.view = view;
		size = c.BOX_HEIGHT / 2.0f;
		this.updateInt = random.nextInt(c.UPDATE_LIGHTING_INTERVAL);
        shaderProgram = new ShaderProgram(vertexShader, fragmentShader);

		squareMesh = new Mesh(true, 4, 4,
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.ColorPacked, 4, "a_color"),
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));
		
				verticies = new float[] {
		                -size, -size, 0, Color.toFloatBits(254, 254, 254, 255), 0, 0,
		                size, -size, 0, Color.toFloatBits(254, 254, 254, 255), 1, 0,
		                -size, size, 0, Color.toFloatBits(254, 254, 254, 255), 0 , 1,
		                size, size, 0, Color.toFloatBits(254, 254, 254, 255), 1, 1};
				squareMesh.setVertices(verticies);
		        squareMesh.setIndices(new short[] { 0, 1, 2, 3 });
		
		if (random.nextBoolean()) {
			t = view.getMeshes().floor1Texture;
		} else {
			t = view.getMeshes().floor2Texture;
		}

	}

	public void draw() {
        Gdx.graphics.getGL20().glEnable(GL20.GL_TEXTURE_2D);
		t.bind();
		squareMesh.render(shaderProgram, GL20.GL_TRIANGLE_STRIP);
		Gdx.graphics.getGL20().glDisable(GL20.GL_TEXTURE_2D);
		handleColors();
	}

	private void handleColors() {
		if (c.DISABLE_LIGHTING) { return; }
		float d1 = box.getPosition().dst(view.getPlayer().getBody().getPosition());
		float d2 = box.getPosition().add(new Vector2(c.BOX_WIDTH, 0)).dst(view.getPlayer().getBody().getPosition());
		float d3 = box.getPosition().add(new Vector2(0, c.BOX_HEIGHT)).dst(view.getPlayer().getBody().getPosition());
		float d4 = box.getPosition().add(new Vector2(c.BOX_WIDTH, c.BOX_HEIGHT)).dst(view.getPlayer().getBody().getPosition());
		
		float p1 = (c.LIGHT_DIST - d1) / c.LIGHT_DIST;
		float p2 = (c.LIGHT_DIST - d2) / c.LIGHT_DIST;
		float p3 = (c.LIGHT_DIST - d3) / c.LIGHT_DIST;
		float p4 = (c.LIGHT_DIST - d4) / c.LIGHT_DIST;
		if (p1 < 0f) {
			p1 = 0f;
		}
		if (p2 < 0f) {
			p2 = 0f;
		}
		if (p3 < 0f) {
			p3 = 0f;
		}
		if (p4 < 0f) {
			p4 = 0f;
		}
		
		verticies[3] = Color.toFloatBits((int)(172f * p1), (int)(178f * p1), (int)(189f * p1), 255);
		verticies[7] = Color.toFloatBits((int)(172f * p2), (int)(178f * p2), (int)(189f * p2), 255);
		verticies[11] = Color.toFloatBits((int)(172f * p3), (int)(178f * p3), (int)(189f * p3), 255);
		verticies[15] = Color.toFloatBits((int)(172f * p4), (int)(178f * p4), (int)(189f * p4), 255);
		squareMesh.setVertices(verticies);
	}
	
}