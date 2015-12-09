package br.com.danielhabib.snake.game;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

import br.com.danielhabib.snake.rules.AFruitRule;
import br.com.danielhabib.snake.rules.AMovingRules;
import br.com.danielhabib.snake.rules.BoingMovingRules;
import br.com.danielhabib.snake.rules.CounterSnakeListener;
import br.com.danielhabib.snake.rules.Direction;
import br.com.danielhabib.snake.rules.Entity;
import br.com.danielhabib.snake.rules.FruitRule;
import br.com.danielhabib.snake.rules.HoleMovingRules;
import br.com.danielhabib.snake.rules.IRule;
import br.com.danielhabib.snake.rules.MapMovingRules;
import br.com.danielhabib.snake.rules.NOPRule;
import br.com.danielhabib.snake.rules.PoisonedFruitRule;
import br.com.danielhabib.snake.rules.RotatingEntity;
import br.com.danielhabib.snake.rules.Snake;
import br.com.danielhabib.snake.rules.SnakeController;
import br.com.danielhabib.snake.rules.SnakeDeathRule;
import br.com.danielhabib.snake.rules.SnakeFactory;
import br.com.danielhabib.snake.rules.SnakeListener;
import br.com.danielhabib.snake.rules.StaticEntity;
import br.com.danielhabib.snake.rules.TextFactory;
import br.com.danielhabib.snake.rules.WormHole;

public class SnakeScreen extends AbstractScreen {

	private static final int SIZE = Entity.SIZE;
	private Game game;
	public SnakeScreen(Game game) {
		this.game = game;
	}

	@Override
	public void buildStage() {
		BitmapFont font = new BitmapFont(Gdx.files.internal("font.fnt"));
		LabelStyle labelStyle = new LabelStyle(font, Color.WHITE);
		final Label title = new Label("", labelStyle);
		final Label counter1 = new Label("", labelStyle);
		final Label counter2 = new Label("", labelStyle);
		final Label counter3 = new Label("", labelStyle);

		Texture headTexture = new Texture(Gdx.files.internal("head.png"));
		Texture tailTexture = new Texture(Gdx.files.internal("tail.png"));
		Texture pieceTexture = new Texture(Gdx.files.internal("circle.png"));
		Texture wallTexture = new Texture(Gdx.files.internal("box.png"));
		Texture appleTexture = new Texture(Gdx.files.internal("apple.png"));
		Texture poisonTexture = new Texture(Gdx.files.internal("poison.png"));
		Texture holeTexture = new Texture(Gdx.files.internal("hole.jpg"));

		final Snake snake = SnakeFactory.newSnakeAtXY(5, 1, Direction.RIGHT, headTexture, pieceTexture, tailTexture);

		IRule boingMovingRules = new BoingMovingRules(stage);
		IRule snakeDeathRule = new SnakeDeathRule(game);
		IRule regularFruitRule = new FruitRule(stage);
		IRule poisonedFruitRule = new PoisonedFruitRule(stage);
		IRule boingFruitRule = new BoingMovingRules(stage);
		IRule identityRule = new NOPRule();

		Entity lastHole = new StaticEntity(holeTexture, new Vector2(13, 12));
		Entity initialHole = new RotatingEntity(holeTexture, new Vector2(3, 8), 100);

		Map<Entity, IRule> map = createMap(wallTexture, boingMovingRules, identityRule);
		Map<Entity, IRule> fruits = createFruits(appleTexture, poisonTexture, wallTexture, regularFruitRule,
				poisonedFruitRule, boingFruitRule);

		AFruitRule fruitsRule = new AFruitRule(fruits, snake);
		AMovingRules realMovingRules = new HoleMovingRules(new WormHole(initialHole, lastHole), snake);
		AMovingRules movingRules = new MapMovingRules(realMovingRules, identityRule, map, snake);
		Actor controller = new SnakeController(movingRules, snake);

		counter1.addListener(new CounterSnakeListener(0) {
			@Override
			public boolean addTail(Actor source, Event event) {
				incrementCounter();
				TextFactory.addCountingAnimation(counter1, String.valueOf(getCounter()), Color.WHITE, 0, Entity.SIZE);
				return false;
			}
		});
		counter2.addListener(new CounterSnakeListener(0) {
			@Override
			public boolean removeTail(Actor source, Event event) {
				incrementCounter();
				TextFactory.addCountingAnimation(counter2, String.valueOf(getCounter()), Color.RED, Entity.SIZE,
						Entity.SIZE);
				return false;
			}
		});
		counter3.addListener(new CounterSnakeListener(0) {
			@Override
			public boolean revert(Actor source, Event event) {
				incrementCounter();
				TextFactory.addCountingAnimation(counter3, String.valueOf(getCounter()), Color.ORANGE, 2 * Entity.SIZE,
						Entity.SIZE);
				return false;
			}
		});
		
		

		title.addListener(new SnakeListener() {
			@Override
			public boolean addTail(Actor source, Event event) {
				TextFactory.addNotifyAnimation(title, source, "glup", Color.WHITE);
				return false;
			}

			@Override
			public boolean removeTail(Actor source, Event event) {
				TextFactory.addNotifyAnimation(title, source, "ick!", Color.RED);
				return false;
			}
			
			@Override
			public boolean revert(Actor source, Event event) {
				TextFactory.addNotifyAnimation(title, source, "OMG!", Color.YELLOW);
				return false;
			}
		});

		snake.addListener(new SnakeListener() {
			@Override
			public boolean revert(Actor source, Event event) {
				snake.revert();
				return false;
			}

			@Override
			public boolean addTail(Actor source, Event event) {
				snake.addTail();
				return false;
			}

			@Override
			public boolean removeTail(Actor source, Event event) {
				snake.removeTail();
				return false;
			}
		});
		stage.addActor(movingRules);
		stage.addActor(controller);
		stage.addActor(fruitsRule);
		stage.addActor(snake);
		stage.addActor(title);
		stage.addActor(counter1);
		stage.addActor(counter2);
		stage.addActor(counter3);
	}

	private Map<Entity, IRule> createFruits(Texture appleTexture, Texture poisonTexture, Texture boingTexture,
			IRule regularFruitRule, IRule poisonedFruitRule, IRule boingFruitRule) {
		Map<Entity, IRule> fruits = new HashMap<Entity, IRule>();
		for (int i = 0; i < 12; i++) {
			fruits.put(new StaticEntity(appleTexture, new Vector2(8, i + 1)), regularFruitRule);
		}
		for (int i = 0; i < 12; i++) {
			fruits.put(new RotatingEntity(poisonTexture, new Vector2(i + 8, i + 2), -2), poisonedFruitRule);
		}
		for (int i = 0; i < 12; i++) {
			fruits.put(new RotatingEntity(boingTexture, new Vector2(i + 5, i + 3), .2f), boingFruitRule);
		}

		return fruits;
	}

	private Map<Entity, IRule> createMap(Texture wallTexture, IRule boingMovingRules, IRule identityRule) {
		Map<Entity, IRule> map = new HashMap<Entity, IRule>();
		int lastX = -1 + Gdx.graphics.getWidth() / SIZE;
		int lastY = -1 + Gdx.graphics.getHeight() / SIZE;
		for (int x = 1; x < lastX; x++) {
			Entity entity = new RotatingEntity(wallTexture, new Vector2(x, 0), 2);
			map.put(entity, new DestroyEntityRule(entity, map, boingMovingRules));
			map.put(new StaticEntity(wallTexture, new Vector2(x, lastY)), identityRule);
		}
		for (int y = 0; y <= lastY; y++) {
			map.put(new StaticEntity(wallTexture, new Vector2(lastX, y)), boingMovingRules);
		}
		return map;
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
		// TODO Auto-generated method stub

	}

}
