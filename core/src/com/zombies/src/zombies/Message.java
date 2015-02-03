package com.zombies.src.zombies;

public class Message {

	private MessageHandler mh;
	private long destroyAt = System.currentTimeMillis() + 5000l;
	private String text;
	
	public Message(GameView view, String text) {
		this.mh = view.mh;
		this.text = text;
	}
	
	public void draw(float x, float y) {
		mh.font.draw(mh.sBatch, text, x, y);
	}
	
	public void update() {
		if (System.currentTimeMillis() > destroyAt) {
			mh.removeMessage(this);
		}
	}
	
}