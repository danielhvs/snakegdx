package br.com.danielhabib.snake.game;

import java.util.Stack;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

import br.com.danielhabib.snake.rules.AMovingRules;
import br.com.danielhabib.snake.rules.Direction;
import br.com.danielhabib.snake.rules.MirrorMapMovingRules;
import br.com.danielhabib.snake.rules.Piece;
import br.com.danielhabib.snake.rules.RandomMovingRules;
import br.com.danielhabib.snake.rules.Snake;

public class Splash implements Screen {

	private SpriteBatch batch;
	private Game game;
	private Stage stage;
	private Stack<Snake> snakes;
	private Stack<Snake> updatedSnakes;
	private static final int SIZE = 16;
	private AMovingRules movingRules;
	private float time;
	private Sprite boxSprite;
	private OrthographicCamera camera;
	private Texture boxTexture;

	public Splash(Game game) {
		this.game = game;
	}

	@Override
	public void show() {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(true);
		stage = new Stage();

		int lastX = -1 + Gdx.graphics.getWidth() / SIZE;
		int lastY = -1 + Gdx.graphics.getHeight() / SIZE;

		snakes = new Stack<Snake>();
		updatedSnakes = new Stack<Snake>();

		BitmapFont font = new BitmapFont(Gdx.files.internal("font.fnt"));
		LabelStyle labelStyle = new LabelStyle(font, Color.ORANGE);
		Label title = new Label("OMG! Crazy Snakes!", labelStyle);
		Skin skin = new Skin(new TextureAtlas("buttons.pack"));
		TextButtonStyle buttonStyle = newSnakeButtonStyle(skin);
		TextButton playButton = newButton("Go go go!", buttonStyle);
		TextButton quitButton = newButton("I'm out!", buttonStyle);
		Table table = new Table();

		title.setFontScale(1);
		table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		table.add(title);
		table.getCell(title).spaceBottom(100);
		table.row();
		table.add(playButton).width(Gdx.graphics.getWidth() / 4).height(Gdx.graphics.getHeight() / 10);
		table.getCell(playButton).spaceBottom(10);
		table.row();
		table.add(quitButton).width(Gdx.graphics.getWidth() / 4).height(Gdx.graphics.getHeight() / 10);
		table.getCell(quitButton).spaceBottom(10);
		// table.debug();

		playButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				game.setScreen(new SnakeScreen(game));
				stage.clear();
				return true;
			}
		});
		quitButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				Gdx.app.exit();
				return true;
			}
		});

		stage.addActor(table);
		Gdx.input.setInputProcessor(stage);

		boxTexture = new Texture(Gdx.files.internal("box.png"));
		boxSprite = new Sprite(boxTexture);
		setSizeAndFlip(boxSprite);
		boxSprite.setColor(Color.YELLOW);

		snakes = newRandomSnakes();
		movingRules = new MirrorMapMovingRules(new RandomMovingRules(), lastX, lastY);
	}

	private TextButton newButton(String text, TextButtonStyle buttonStyle) {
		TextButton button = new TextButton(text, buttonStyle);
		return button;
	}

	private TextButtonStyle newSnakeButtonStyle(Skin skin2) {
		TextButtonStyle buttonStyle = new TextButtonStyle();
		buttonStyle.up = skin2.getDrawable("button");
		buttonStyle.over = skin2.getDrawable("buttonpressed");
		buttonStyle.down = skin2.getDrawable("buttonpressed");
		buttonStyle.font = new BitmapFont(Gdx.files.internal("font.fnt"));
		return buttonStyle;
	}

	private Stack<Snake> newRandomSnakes() {
		Stack<Snake> stack = new Stack<Snake>();
		stack.push(newSnakeAtXY(10, 1, Direction.RIGHT));
		stack.push(newSnakeAtXY(Gdx.graphics.getWidth() / SIZE - 10, 1, Direction.LEFT));
		stack.push(newSnakeAtXY(10, Gdx.graphics.getHeight() / SIZE - 1, Direction.RIGHT));
		stack.push(
				newSnakeAtXY(Gdx.graphics.getWidth() / SIZE - 10, Gdx.graphics.getHeight() / SIZE - 1, Direction.LEFT));
		stack.push(
				newSnakeAtXY(Gdx.graphics.getWidth() / SIZE / 2, Gdx.graphics.getHeight() / SIZE / 2, Direction.RIGHT));
		return stack;
	}

	// FIXME: DRY
	private Snake newSnakeAtXY(int x, int y, Direction direction) {
		Stack<Piece> pieces = new Stack<Piece>();
		pieces.push(new Piece(new Vector2(x, y), direction, boxTexture));
		Snake snake = new Snake(pieces);
		int size = 10;
		for (int i = 0; i < size; i++) {
			snake = snake.addTail();
		}
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

		// Managing FPS
		time += delta;
		if (time > 0.08) {
			while (!snakes.isEmpty()) {
				Snake update = movingRules.update(movingRules.turnLeft(snakes.pop()));
				updatedSnakes.push(update);
			}
			while (!updatedSnakes.isEmpty()) {
				snakes.push(updatedSnakes.pop());
			}
			time = 0;
		}

		camera.update();
		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		// Snake
		for (Snake snake : snakes) {
			for (Piece piece : snake.getPieces()) {
				Vector2 position = piece.getPoint();
				boxSprite.setPosition(position.x * SIZE, position.y * SIZE);
				boxSprite.draw(batch);
			}
		}
		batch.end();

		stage.act();
		stage.draw();
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

	@Override
	public void dispose() {
		dispose(boxSprite);
		batch.dispose();
	}

	private void dispose(Sprite sprite) {
		sprite.getTexture().dispose();
	}
}
