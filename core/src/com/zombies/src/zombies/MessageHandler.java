package com.zombies.src.zombies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.LinkedList;

public class MessageHandler {

	private LinkedList<Message> messages = new LinkedList<Message>();
	private LinkedList<Message> messagesDump = new LinkedList<Message>();
	private GameView view;
	public BitmapFont font;
	public SpriteBatch sBatch = new SpriteBatch();
	
	public MessageHandler(GameView view) {
		this.view = view;
		font = new BitmapFont(Gdx.files.internal("data/main.fnt"), Gdx.files.internal("data/main.png"), false);
	}
	
	public void addMessage (Message m) {
		messages.add(m);
	}
	
	public void removeMessage(Message m) {
		messagesDump.add(m);
	}
	
	public void update() {
		clearDumps();
		for (Message m: messages) {
			m.update();
		}
		this.draw();
	}

	private void draw() {
		sBatch.begin();
		float y = view.getHeight() - 2;
		for (Message m: messages) {
			y -= 10;
			m.draw(10, y);
		}
		
		sBatch.end();
	}
	
	private void clearDumps() {
		for (Message m: messagesDump) {
			messages.remove(m);
		}
		messagesDump.clear();
	}
	
}