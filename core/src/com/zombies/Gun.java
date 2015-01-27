package com.zombies;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Gun {

	protected GameView view;
	protected Unit unit;
	protected long lastShot = System.currentTimeMillis();
	protected long speed;
	protected int ammo;
	protected LinkedList<Bullet> bullets = new LinkedList<Bullet>();
	protected String type;
	protected GunBox gb;
	protected Texture texture;
	protected C c;
	
	public Gun(GameView view, Unit unit) {
		this.view = view;
		this.unit = unit;
		this.c = view.c;
		gb = new GunBox(view, this);
	}
	
	public void update() {
		
	}
	
	public void draw() {
        // TODO: Render gun
        //view.getMeshes().gunMesh.render(GL10.GL_TRIANGLE_STRIP, 0, 4);
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public void renderGunBox(int num, SpriteBatch spriteBatch) {
		gb.render(num, spriteBatch);
	}
	
	public void handleTouch(float x, float y) {
		gb.handleTouch(x, y);
	}
	
	public boolean isType(String type) {
		return this.type == type;
	}
	
	public boolean isEmpty() {
		return ammo <= 0;
	}
	
	public int getAmmo() {
		return ammo;
	}
	
	public String getType() {
		return type;
	}
	
	public void shoot(Vector2 direction) {
		
	}
	
	public void addAmmo(int add) {
		ammo += add;
	}
	
}
