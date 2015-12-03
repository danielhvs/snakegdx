package br.com.danielhabib.snake.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import br.com.danielhabib.snake.rules.AMovingRules;
import br.com.danielhabib.snake.rules.Direction;
import br.com.danielhabib.snake.rules.Entity;
import br.com.danielhabib.snake.rules.FruitRule;
import br.com.danielhabib.snake.rules.Hole;
import br.com.danielhabib.snake.rules.Piece;
import br.com.danielhabib.snake.rules.PoisonedFruitRule;
import br.com.danielhabib.snake.rules.RestrictedMovingRules;
import br.com.danielhabib.snake.rules.Snake;
import br.com.danielhabib.snake.rules.SnakeController;
import br.com.danielhabib.snake.rules.Wall;

public class SnakeScreen implements Screen {

	private Sprite boxSprite;
	private Sprite headSprite;
	private Sprite appleSprite;
	private Sprite poisonedSprite;
	private Sprite holeSprite;
	private static final int SIZE = Entity.SIZE;
	private Game game;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Snake snake;
	private FruitRule fruitRule;
	private PoisonedFruitRule poisonRule;
	private float time;
	private SnakeController controller;
	private AMovingRules movingRules;
	private Hole hole;
	private List<Entity> map;
	private Texture boxTexture;
	private Texture headTexture;
	private float fps = 8;
	private float threshold = 0.125f;

	public SnakeScreen(Game game) {
		this.game = game;
	}

	@Override
	public void show() {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(true);

		headTexture = new Texture(Gdx.files.internal("head.png"));
		headSprite = new Sprite(headTexture);
		boxTexture = new Texture(Gdx.files.internal("box.jpg"));
		boxSprite = new Sprite(boxTexture);
		setSizeAndFlip(boxSprite);
		setSizeAndFlip(headSprite);
		headSprite.setOrigin(headSprite.getWidth() / 2, headSprite.getHeight() / 2);

		// Map
		map = new ArrayList<Entity>();
		int lastX = -1 + Gdx.graphics.getWidth() / SIZE;
		int lastY = -1 + Gdx.graphics.getHeight() / SIZE;
		for (int x = 0; x < lastX; x++) {
			map.add(new Wall(boxTexture, new Vector2(x, 0)));
			map.add(new Wall(boxTexture, new Vector2(x, lastY)));
		}
		//FIXME!? <=?
		for (int y = 0; y <= lastY; y++) {
			map.add(new Wall(boxTexture, new Vector2(0, y)));
			map.add(new Wall(boxTexture, new Vector2(lastX, y)));
		}

		// Apples
		appleSprite = new Sprite(new Texture(Gdx.files.internal("apple.png")));
		poisonedSprite = new Sprite(new Texture(Gdx.files.internal("poison.png")));
		setSizeAndFlip(appleSprite);
		setSizeAndFlip(poisonedSprite);

		// Hole
		holeSprite = new Sprite(new Texture(Gdx.files.internal("hole.jpg")));
		setSizeAndFlip(holeSprite);

		// Map
		snake = newSnakeAtXY(5, 1, Direction.RIGHT);
		fruitRule = new FruitRule(new Vector2(3, 4));
		poisonRule = new PoisonedFruitRule(new Vector2(8, 17));
		hole = new Hole(new Vector2(3, 8), new Vector2(19, 9));
		// AMovingRules realMovingRules = new HoleMovingRules(hole);
		AMovingRules realMovingRules = new RestrictedMovingRules();
		controller = new SnakeController(realMovingRules);
		// movingRules = new MapMovingRules(realMovingRules, map);
		// movingRules = new MirrorMapMovingRules(holeMovingRules, lastX,
		// lastY);
		// movingRules = new BoingMovingRules(realMovingRules, 1, 1, lastX - 1,
		// lastY - 1);
		movingRules = realMovingRules;
	}

	// FIXME: DRY
	private Snake newSnakeAtXY(int x, int y, Direction direction) {
		Stack<Piece> pieces = new Stack<Piece>();
		pieces.push(new Piece(new Vector2(x, y), direction, headTexture));
		int size = 10;
		for (int i = 1; i < size; i++) {
			pieces.push(new Piece(new Vector2(x - i, y), direction, boxTexture));
		}
		Snake snake = new Snake(pieces);
		return snake;
	}

	private void setSizeAndFlip(Sprite sprite) {
		sprite.setSize(SIZE, SIZE);
		sprite.flip(false, true);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
			game.setScreen(new Splash(game));
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
			System.out.println(threshold);
			setFPS(fps * 1.1f);
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_9)) {
			System.out.println(threshold);
			setFPS(fps * 0.9f);
		}

		controlSnake();

		// Managing FPS
		// This going to be the "speed".
		time += delta;
		if (time > threshold) {
			movingRules.update(snake);
			time = 0;
		}

		// Applying Rules
		fruitRule.update(snake);
		poisonRule.update(snake);

		for (Entity mapPoint : map) {
			mapPoint.update();
		}

		// Drawing
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		// Fruits
		appleSprite.setPosition(fruitRule.getFruitPosition().x * SIZE, fruitRule.getFruitPosition().y * SIZE);
		poisonedSprite.setPosition(poisonRule.getFruitPosition().x * SIZE, poisonRule.getFruitPosition().y * SIZE);

		// Hole
		Vector2 point = hole.getInitialPoint();
		holeSprite.setPosition(point.x * SIZE, point.y * SIZE);
		holeSprite.draw(batch);
		point = hole.getFinalPoint();
		holeSprite.setPosition(point.x * SIZE, point.y * SIZE);
		holeSprite.draw(batch);

		// Snake
		snake.draw(batch);

		// Map
		for (Entity mapPoint : map) {
			mapPoint.render(batch);
		}

		// Draw to batch
		appleSprite.draw(batch);
		poisonedSprite.draw(batch);

		batch.end();
	}

	// This will be the "speed"
	private void setFPS(float fps) {
		this.fps = fps;
		this.threshold = 1 / fps;
	}

	private void controlSnake() {
		if (Gdx.input.isKeyPressed(Input.Keys.M)) {
			snake = movingRules.update(snake);
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
			snake = movingRules.update(snake);
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
			snake = movingRules.turnLeft(snake);
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
			snake = movingRules.turnRight(snake);
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
			snake = controller.left(snake);
		} else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
			snake = controller.right(snake);
		} else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
			snake = controller.up(snake);
		} else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
			snake = controller.down(snake);
		}
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		dispose();
	}

	// FIXME: call dispose on snake pieces too..?
	@Override
	public void dispose() {
		dispose(boxSprite);
		dispose(headSprite);
		dispose(appleSprite);
		dispose(poisonedSprite);
		dispose(holeSprite);
		batch.dispose();
	}

	private void dispose(Sprite sprite) {
		sprite.getTexture().dispose();
	}

}
