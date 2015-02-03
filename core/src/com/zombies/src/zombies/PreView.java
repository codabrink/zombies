package com.zombies.src.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PreView implements Screen {

	private SpriteBatch spriteBatch;
	
	protected Zombies main;
	protected long startTime = System.currentTimeMillis();
	
	public PreView(Zombies main) {
		this.main = main;
		spriteBatch = new SpriteBatch();
	}

	@Override
	public void render(float arg0) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		spriteBatch.begin();
		spriteBatch.enableBlending();
		spriteBatch.end();
		main.view.mh.sBatch.begin();
		main.view.mh.font.draw(main.view.mh.sBatch, "ZOMBIE SURGE", 10, getHeight() / 2 + 50);
		main.view.mh.font.draw(main.view.mh.sBatch, "Kill as many zombies as you can before dying.", 10, getHeight() / 2 + 30);
		main.view.mh.font.draw(main.view.mh.sBatch, "There are health packs and guns scattered through the maze.", 10, getHeight() / 2 + 10);
		main.view.mh.font.draw(main.view.mh.sBatch, "You have 3 other survivors with you. They'll follow.", 10, getHeight() / 2 - 10);
		main.view.mh.font.draw(main.view.mh.sBatch, "Touch the screen to continue.", 10, getHeight() / 2 - 30);
		main.view.mh.sBatch.end();
		spriteBatch.begin();
		spriteBatch.end();
		Gdx.gl.glFlush();
		handleKeys();
	}
	
	private void handleKeys() {
		for (int i=0; i<3; i++) {
			if (Gdx.input.isTouched(i) && System.currentTimeMillis() > startTime + 500l) {
				main.setScreen(main.view);
			}
		}
	}
	
	public int getWidth() {
		return main.getWidth();
	}
	
	public int getHeight() {
		return main.getHeight();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	

	@Override
	public void resize(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}
}
