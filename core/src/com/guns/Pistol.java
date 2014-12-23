package zombies.Guns;

import zombies.Bullet;
import zombies.GameView;
import zombies.Gun;
import zombies.Message;
import zombies.Unit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;

public class Pistol extends Gun {
	
	private Sound shoot = Gdx.audio.newSound(Gdx.files.internal("data/sound/pistol.mp3"));
	
	public Pistol(GameView view, Unit unit, int ammo) {
		super(view, unit);
		speed = 200l;
		type = "pistol";
		this.ammo = ammo;
		this.texture = view.getMeshes().pistolTexture;
	}

	@Override
	public void draw() {
		for (Bullet b: bullets) {
			b.draw();
		}
	}
	
	@Override
	public void shoot(Vector2 direction) {
		if (System.currentTimeMillis() < lastShot + speed) { return; }
		if (ammo <= 0) {
			view.mh.addMessage(new Message(view, "You're out of ammo! Find another gun!"));
			lastShot = System.currentTimeMillis();
			return;
		}
		lastShot = System.currentTimeMillis();
		if (bullets.size() > view.c.MAX_BULLETS) {
			bullets.getFirst().reShoot(unit.getBody().getPosition(), direction);
			// throw bullet to back of list
			bullets.add(bullets.removeFirst().refresh());
		} else {
			bullets.add(new Bullet(view, unit, unit.getGroup(), unit.getBody().getPosition(), direction));
		}
		unit.getBox().getRoom().alarm(unit);
		view.s.shots ++;
		ammo--;
		shoot.play(0.2f);
	}
	
	@Override
	public void update() {
		
	}
	
}
