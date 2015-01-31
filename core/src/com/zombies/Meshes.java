package com.zombies;

import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;

public class Meshes {

	public Texture wall1Texture;
	public Texture wall2Texture;
	public Texture crateTexture;
	public Texture shotgunTexture;
	public Texture pistolTexture;
	public Texture healthTexture;
	public Texture floor1Texture;
	public Texture floor2Texture;
	public Mesh gunMesh;
	
	public Sound shotgunPickup = Gdx.audio.newSound(Gdx.files.internal("data/sound/shotgun-pickup.mp3"));
	
	//public Music main = Gdx.audio.newMusic(Gdx.files.internal("data/sound/music/main.mp3"));
	
	public Meshes() {
				FileHandle imageFileHandle = Gdx.files.internal("data/wall1.png");
		        wall1Texture = new Texture(imageFileHandle);
		        imageFileHandle = Gdx.files.internal("data/wall2.png");
		        wall2Texture = new Texture(imageFileHandle);
		        imageFileHandle = Gdx.files.internal("data/crate.png");
		        crateTexture = new Texture(imageFileHandle);
		        imageFileHandle = Gdx.files.internal("data/shotgun.png");
		        shotgunTexture = new Texture(imageFileHandle);
		        imageFileHandle = Gdx.files.internal("data/pistol.png");
		        pistolTexture = new Texture(imageFileHandle);
		        imageFileHandle = Gdx.files.internal("data/health.png");
		        healthTexture = new Texture(imageFileHandle);
		        imageFileHandle = Gdx.files.internal("data/floor1.png");
		        floor1Texture = new Texture(imageFileHandle);
		        imageFileHandle = Gdx.files.internal("data/floor2.png");
		        floor2Texture = new Texture(imageFileHandle);
		        
		        gunMesh = new Mesh(true, 4, 4,
						new VertexAttribute(Usage.Position, 3, "a_position"),
						new VertexAttribute(Usage.ColorPacked, 4, "a_color"));
		        gunMesh.setVertices(new float[] {
	                -0.15f, -0.15f, 0, Color.toFloatBits(135, 135, 135, 255),
	                0.9f, -0.15f, 0, Color.toFloatBits(114, 114, 114, 255),
	                -0.15f, 0.15f, 0, Color.toFloatBits(114, 114, 114, 255),
	                0.9f, 0.15f, 0, Color.toFloatBits(135, 135, 135, 255) });
		        gunMesh.setIndices(new short[] { 0, 1, 2, 3});
		        
	}
	
}
