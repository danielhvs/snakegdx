package br.com.danielhabib.snake.listeners;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;

import br.com.danielhabib.snake.listeners.SnakeEvent.Type;

public class SnakeListener implements EventListener {

	@Override
	public boolean handle(Event event) {
		if (!(event instanceof SnakeEvent)) {
			return false;
		}
		SnakeEvent snakeEvent = (SnakeEvent) event;

		switch (snakeEvent.getType()) {
		case revert:
			return revert(snakeEvent.getSource(), event);
		case addTail:
			return addTail(snakeEvent.getSource(), event);
		case removeTail:
			return removeTail(snakeEvent.getSource(), event);
		case colided:
			return colided(snakeEvent.getSource(), event);
		default:
			return handle(snakeEvent.getSource(), snakeEvent.getType());
		}
	}

	public boolean handle(Actor source, Type type) {
		return false;
	}

	public boolean colided(Actor source, Event event) {
		return false;
	}

	public boolean revert(Actor actor, Event event) {
		return false;
	}

	public boolean addTail(Actor actor, Event event) {
		return false;
	}

	public boolean removeTail(Actor actor, Event event) {
		return false;
	}

}
