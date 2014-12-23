package zombies.Guns;

import java.util.Random;

import zombies.Bullet;
import zombies.GameView;
import zombies.Gun;
import zombies.Message;
import zombies.Unit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;

public class Shotgun extends Gun {

	private int shotSize = 4;
	private Random random = new Random();
	private float shotSpread = 10;
	private Sound shoot = Gdx.audio.newSound(Gdx.files.internal("data/sound/shotgun.mp3"));
	
	public Shotgun(GameView view, Unit unit, int ammo) {
		super(view, unit);
		speed = c.SHOTGUN_SPEED;
		type = "shotgun";
		this.ammo = ammo;
		this.texture = view.getMeshes().shotgunTexture;
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
		for (int i=1; i <= shotSize; i++) {
			Vector2 tempDirection = direction.rotate(random.nextFloat() * shotSpread * 2 - shotSpread);
			if (bullets.size() > c.MAX_BULLETS) {
				bullets.getFirst().reShoot(unit.getBody().getPosition(), tempDirection);
				// throw bullet to back of list
				bullets.add(bullets.removeFirst().refresh());
			} else {
				bullets.add(new Bullet(view, unit, unit.getGroup(), unit.getBody().getPosition(), tempDirection));
			}
		}
		unit.getBox().getRoom().alarm(unit);
		view.s.shots ++;
		ammo--;
		shoot.play();
	}
	
	@Override
	public void update() {
		
	}
	
}