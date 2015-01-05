package com.zombies;

import java.util.ArrayList;
import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Unit {

	protected Unit attack = null;
	protected BodyDef bDef = new BodyDef();
	protected Body body;
	protected Box box;
	protected LinkedList<Bullet> bullets = new LinkedList<Bullet>();
	protected C c;
	protected boolean dead = false;
	protected float diffX, diffY;
	protected FixtureDef fDef = new FixtureDef();
	protected short GROUP;
	protected int gunIndex = 0;
	protected ArrayList<Gun> guns = new ArrayList<Gun>();
	protected float health;
	protected long holdMPos = System.currentTimeMillis();
	protected Vector2 mPos;
	private LinkedList<Fixture> obstacles = new LinkedList<Fixture>();
	protected CircleShape shape;
	protected float speed;
	protected Mesh squareMesh;
	protected float[] verticies;
	protected GameView view;

    // All the GLv2 crap
    protected static final String VERT_SHADER =
            "attribute vec2 a_position;\n" +
                    "attribute vec4 a_color;\n" +
                    "uniform mat4 u_projTrans;\n" +
                    "varying vec4 vColor;\n" +
                    "void main() {\n" +
                    "	vColor = a_color;\n" +
                    "	gl_Position =  u_projTrans * vec4(a_position.xy, 0.0, 1.0);\n" +
                    "}";
    protected static final String FRAG_SHADER =
            "#ifdef GL_ES\n" +
                    "precision mediump float;\n" +
                    "#endif\n" +
                    "varying vec4 vColor;\n" +
                    "void main() {\n" +
                    "	gl_FragColor = vColor;\n" +
                    "}";
    protected static ShaderProgram createMeshShader() {
        ShaderProgram.pedantic = false;
        ShaderProgram shader = new ShaderProgram(VERT_SHADER, FRAG_SHADER);
        String log = shader.getLog();
        if (!shader.isCompiled())
            throw new GdxRuntimeException(log);
        if (log != null && log.length() != 0)
            System.out.println("Shader Log: " + log);
        return shader;
    }
    //Position attribute - (x, y)
    protected static final int POSITION_COMPONENTS = 2;
    //Color attribute - (r, g, b, a)
    protected static final int COLOR_COMPONENTS = 4;
    //Total number of components for all attributes
    protected static final int NUM_COMPONENTS = POSITION_COMPONENTS + COLOR_COMPONENTS;
    //The maximum number of triangles our mesh will hold
    protected static final int MAX_TRIS = 2;
    //The maximum number of vertices our mesh will hold
    protected static final int MAX_VERTS = MAX_TRIS * 3;
    //The array which holds all the data, interleaved like so:
    //    x, y, r, g, b, a
    //    x, y, r, g, b, a,
    //    x, y, r, g, b, a,
    //    ... etc ...
    private float[] verts = new float[MAX_VERTS * NUM_COMPONENTS];
    //The index position
    private int idx = 0;
    Mesh mesh;
    ShaderProgram shader;


	private RayCastCallback vision = new RayCastCallback() {
		
		@Override
		public float reportRayFixture(Fixture f, Vector2 point, Vector2 normal, float fraction) {
			obstacles.add(f);
			return 0;
		}
		
	};

	public Unit(GameView view) {
		this.c = view.c;
		shape = new CircleShape();
		this.view = view;

        mesh = new Mesh(true, MAX_VERTS, 0,
                new VertexAttribute(VertexAttributes.Usage.Position, POSITION_COMPONENTS, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.Color, COLOR_COMPONENTS, "a_color"));
        shader = createMeshShader();
	}
	
	public void addGun(Gun g) {
		for (Gun gun: guns) {
			if (gun.isType(g.getType())) {
				gun.addAmmo(g.getAmmo());
				return;
			}
		}
		guns.add(g);
		gunIndex = guns.size() - 1;
	}
	
	protected boolean canChangeMPos() {
		return (System.currentTimeMillis() > holdMPos);
	}
	
	protected void capSpeed() {
		if (body.getLinearVelocity().len() > speed) {
            body.setLinearVelocity(body.getLinearVelocity().scl(c.PLAYER_SPEED));
		}
	}
	
	public void destroy() {
		shape.dispose();
		body.getWorld().destroyBody(body);
	}
	
	public float distance(Unit z) {
		diffX = body.getPosition().x - z.getX();
		diffY = body.getPosition().y - z.getY();
		return (float) Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
	}

    void flush() {
        if (idx == 0)
            return;

        //sends our vertext data to the mesh
        mesh.setVertices(verts);
        //no need for depth...
        Gdx.gl.glDepthMask(false);
        //enable blending, for alpha
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        //number of vertices we need to render
        int vertexCount = (idx/NUM_COMPONENTS);
        //start the shader before setting any uniforms
        shader.begin();
        //render the mesh
        mesh.render(shader, GL20.GL_TRIANGLES, 0, vertexCount);
        shader.end();
        //re-enable depth to reset states to their default
        Gdx.gl.glDepthMask(true);
        //reset index to zero
        idx = 0;
    }

    public Vector2 rotatePoint(float x, float y, float r) {
        float nx = (float)(x * Math.cos(r) - y * Math.sin(r));
        float ny = (float)(x * Math.sin(r) - y * Math.cos(r));
        return new Vector2(nx, ny);
    }

	public void drawSquare(float x, float y, float width, float r, Color color) {
        //we don't want to hit any index out of bounds exception...
        //so we need to flush the batch if we can't store any more verts
        if (idx==verts.length)
            flush();

        // rotation: http://math.stackexchange.com/questions/270194/how-to-find-the-vertices-angle-after-rotation
        //FIRST TRIANGLE
        //bottom left vertex
        Vector2 point = rotatePoint(x - width / 2, y - width / 2, r);
        verts[idx++] = point.x; 			//Position(x, y)
        verts[idx++] = point.y;
        verts[idx++] = color.r; 	//Color(r, g, b, a)
        verts[idx++] = color.g;
        verts[idx++] = color.b;
        verts[idx++] = color.a;

        //top left vertex
        point = rotatePoint(x - width / 2, y + width / 2, r);
        verts[idx++] = point.x; 			//Position(x, y)
        verts[idx++] = point.y;
        verts[idx++] = color.r; 	//Color(r, g, b, a)
        verts[idx++] = color.g;
        verts[idx++] = color.b;
        verts[idx++] = color.a;

        //bottom right vertex
        point = rotatePoint(x + width / 2, y - width / 2, r);
        verts[idx++] = point.x;	 //Position(x, y)
        verts[idx++] = point.y;
        verts[idx++] = color.r;		 //Color(r, g, b, a)
        verts[idx++] = color.g;
        verts[idx++] = color.b;
        verts[idx++] = color.a;

        //SECOND TRIANGLE
        //top left vertex
        point = rotatePoint(x - width / 2, y + width / 2, r);
        verts[idx++] = point.x; 			//Position(x, y)
        verts[idx++] = point.y;
        verts[idx++] = color.r; 	//Color(r, g, b, a)
        verts[idx++] = color.g;
        verts[idx++] = color.b;
        verts[idx++] = color.a;

        //top right vertex
        point = rotatePoint(x + width / 2, y + width / 2, r);
        verts[idx++] = point.x;
        verts[idx++] = point.y;
        verts[idx++] = color.r;
        verts[idx++] = color.g;
        verts[idx++] = color.b;
        verts[idx++] = color.a;

        //bottom right vertex
        point = rotatePoint(x + width / 2, y + width / 2, r);
        verts[idx++] = point.x;	 //Position(x, y)
        verts[idx++] = point.y;
        verts[idx++] = color.r;		 //Color(r, g, b, a)
        verts[idx++] = color.g;
        verts[idx++] = color.b;
        verts[idx++] = color.a;
    }

	public Body getBody() {return body;}
	public Box getBox() {
		return box;
	}
	
	public LinkedList<Bullet> getBullets() {
		return bullets;
	}
	
	public Fixture getFixture() {
		return body.getFixtureList().get(0);
	}
	
	public short getGroup() {
		return GROUP;
	}
	
	public float getX() {
		return body.getPosition().x;
	}
	
	public float getY() {
		return body.getPosition().y;
	}
	
	public void hurt(float zombieStrength, Unit u) {
		if (view.getPlayer().getRoom() != box.getRoom()) {
			body.setAwake(false);
			return;
		}
		health -= zombieStrength;
		if (health < 0) {
			u.victory();
			kill(u);
		}
		sick(u);
	}

	public float getHealth(){
		return health;
	}
	
	public void heal(float h) {
		health += h;
		if (health > c.PLAYER_HEALTH) {
			health = c.PLAYER_HEALTH;
		}
	}
	
	public boolean isDead() {
		return dead;
	}
	
	public boolean isVisionClear() {
		obstacles.clear();
		view.getWorld().rayCast(vision, body.getPosition(), view.getPlayer().getBody().getPosition());
		for (Fixture f: obstacles) {
			if (f == null || f.getBody() == null || f.getBody().getUserData() == null)
				return false;
			String type = ((BodData)f.getBody().getUserData()).getType();
			if (type != "zombie" && type != "player") {
				return false;
			}
		}
		return true;
	}
	
	public void kill(Unit u) {
		destroy();
		box.getUnits().remove(this);
		view.getPlayer().addZombieKill();
		dead = true;
	}
	
	protected void move() {
		body.applyForce(mPos.sub(body.getPosition().scl(c.ZOMBIE_AGILITY)), new Vector2(), true);
	}
	
	public Vector2 randomDirection() {
		return box.randomPoint();
	}
	
	public void setBox(Box b) {
		box = b;
	}
	
	protected void shove(float x, float y, long duration) {
		float nx = body.getPosition().x + x;
		float ny = body.getPosition().y + y;
		mPos = new Vector2(nx, ny);
	}
	
	public void sick(Unit a) {
		if (attack == null)
			attack = a;
	}
	
	public void update() {}
	
	public void victory() {}
	
}
