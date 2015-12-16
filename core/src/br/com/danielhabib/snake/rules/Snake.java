package br.com.danielhabib.snake.rules;

import java.util.List;
import java.util.Stack;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Snake extends Actor {

	private List<Piece> pieces;
	private Texture pieceTexture;
	private Vector2 direction;

	public Snake(List<Piece> pieces, Texture pieceTexture, Vector2 direction) {
		this.pieces = pieces;
		this.pieceTexture = pieceTexture;
		this.direction = direction;
	}

	public Piece getTail() {
		return pieces.get(getTailIndex());
	}

	public Snake addTail(int x, int y) {
		// pieces.add(new Piece(new Vector2(x, y), getTail().getDirection(),
		// getTextureOf(getHead())));
		return this;
	}

	public Snake addTail() {
		// FIXME
		// Piece tail = getTail();
		// Vector2 tailDirection = tail.getDirection();
		// Vector2 point = tail.getPosition().cpy();
		// Vector2 newPoint = point.sub(tail.getDirection());
		// Piece newTail = new Tail(newPoint, tailDirection,
		// tail.getSprite().getTexture());
		// Piece newPiece = new Piece(tail.getPosition(), tailDirection,
		// pieceTexture);
		//
		// pieces.remove(getTailIndex());
		// pieces.add(newPiece);
		// pieces.add(newTail);
		return this;
	}

	public Snake move(float delta) {
		Piece snakeHead = pieces.get(0);
		int snakeLength = pieces.size() - 1;
		float speed = 4f;
		Vector2 newPosition = snakeHead.getPosition().add(getDirection().scl(speed * delta));//
		snakeHead.setPosition(newPosition.x, newPosition.y);
		for (int i = 1; i <= snakeLength; i++) {
			Piece partBefore = pieces.get(i - 1);
			Piece thisPart = pieces.get(i);
			float deltaX = partBefore.getX() - thisPart.getX();
			float deltaY = partBefore.getY() - thisPart.getY();
			float angle = (float) Math.atan2(deltaY, deltaX);
			thisPart.setX(partBefore.getX() - (float) Math.cos(angle) * Entity.SIZE);
			thisPart.setY(partBefore.getY() - (float) Math.sin(angle) * Entity.SIZE);
			thisPart.setRotation((float) Math.toDegrees(angle));
		}
		return this;
	}

	public Stack<Vector2> getNextPositions() {
		// Stack<Vector2> nextPositions = new Stack<Vector2>();
		// for (int i = 1; i < pieces.size(); i++) {
		// nextPositions.push(pieces.get(i).getNextPosition());
		// }
		// nextPositions.push(getHead().getNextPosition());
		// return nextPositions;
		return new Stack<Vector2>();
	}

	public Vector2 getNextPosition(float delta) {
		float speed = 4;
		return getPosition().add(getDirection().scl(speed * delta));
	}

	public Snake removeTail() {
		// int size = pieces.size();
		// if (size <= 2) {
		// return this;
		// } else {
		// Piece piece = pieces.get(getTailIndex() - 1);
		// getTail().move(piece.getPosition());
		// getTail().turn(piece.getDirection());
		// pieces.remove(piece);
		// }
		return this;
	}

	public Piece getHead() {
		return pieces.get(0);
	}

	private int getTailIndex() {
		return pieces.size() - 1;
	}

	public Vector2 getPosition() {
		return getHead().getPosition().cpy();
	}

	public Vector2 getDirection() {
		return direction.cpy();
	}

	@Override
	public String toString() {
		return pieces.toString() + " DIRECTION: " + direction;
	}

	public Snake turn(Vector2 direction) {
		this.direction = direction;
		getHead().setRotation(direction.angle());
		return this;
	}

	// FIXME!? Test.
	public Snake revert() {
		// Stack<Piece> newPieces = new Stack<Piece>();
		// Stack<Piece> piecesCopy = copyPieces();
		// Piece newHead = piecesCopy.pop();
		// Piece newHeadPiece = new Head(newHead.getPosition(),
		// newHead.getDirection().rotate(180f),
		// getTextureOf(getHead()));
		// newPieces.push(newHeadPiece);
		// Texture tailTexture = getTextureOf(getTail());
		// while (piecesCopy.size() > 1) {
		// Piece pop = piecesCopy.pop();
		// Piece newPiece = new Piece(pop.getPosition(),
		// pop.getDirection().rotate(180f), pieceTexture);
		// newPieces.push(newPiece);
		// }
		// if (piecesCopy.size() == 1) {
		// Piece pop = piecesCopy.pop();
		// Piece newPiece = new Tail(pop.getPosition(),
		// pop.getDirection().rotate(180f), tailTexture);
		// newPieces.push(newPiece);
		// }
		// this.pieces = newPieces;
		return this;
	}

	private Texture getTextureOf(Piece piece) {
		return piece.getSprite().getTexture();
	}

	public Stack<Piece> copyPieces() {
		Stack<Piece> copy = new Stack<Piece>();
		copy.addAll(pieces);
		return copy;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pieces == null) ? 0 : pieces.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Snake other = (Snake) obj;
		if (pieces == null) {
			if (other.pieces != null) {
				return false;
			}
		} else if (!pieces.equals(other.pieces)) {
			return false;
		}
		return true;
	}

	@Override
	public void act(float delta) {
		for (Piece piece : pieces) {
			piece.act(delta);
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		for (Piece piece : pieces) {
			piece.draw(batch, parentAlpha);
		}
	}

	public Snake move(Vector2 newPoint) {
		// // FIXME: moves instantly.
		// Vector2 headPoint = newPoint;
		// Vector2 headDirection = pieces.get(0).getDirection();
		// for (int i = 0; i < pieces.size(); i++) {
		// Piece piece = pieces.get(i);
		// Vector2 lastHeadPoint = piece.getPosition();
		// Vector2 lastDirection = piece.getDirection();
		//
		// piece.move(headPoint).turn(headDirection);
		//
		// headPoint = lastHeadPoint;
		// headDirection = lastDirection;
		// }
		return this;
	}

}
