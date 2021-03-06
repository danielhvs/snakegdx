package br.com.danielhabib.snake.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kotcrab.vis.ui.widget.VisDialog;

public class SnakeDialog {

	private GameScreen screen;
	private VisDialog dialog;

	public SnakeDialog(GameScreen screen) {
		this.screen = screen;
		this.dialog = buildDialog();
	}

	private static boolean showingExitDialog;

	public void showDialog() {
		if (!showingExitDialog) {
			dialog.show(screen);
			Camera camera = screen.getCamera();
			float xPosition = getCenterPoint(camera.viewportWidth, camera.position.x, dialog.getWidth());
			float yPosition = getCenterPoint(camera.viewportHeight, camera.position.y, dialog.getHeight());
			dialog.setPosition(xPosition, yPosition);
			screen.pauseGame();
		}
	}

	private float getCenterPoint(float viewportWidth, float x, float width) {
		return (x - viewportWidth / 2) + (viewportWidth - width) / 2;
	}

	private VisDialog buildDialog() {
		VisDialog dialog = new VisDialog("Do you really want to leave?") {
			@Override
			protected void result(Object object) {
				boolean exit = (Boolean) object;
				if (exit) {
					ScreenManager.getInstance().showScreen(ScreenEnum.MAIN_MENU);
				} else {
					remove();
					screen.unpauseGame();
				}
				showingExitDialog = false;
			}

			@Override
			public VisDialog show(Stage stage) {
				showingExitDialog = true;
				screen.pauseGame();
				return super.show(stage);
			}

			@Override
			public void cancel() {
				showingExitDialog = false;
				screen.unpauseGame();
				super.cancel();
			}

		};

		dialog.button("Yes", true);
		dialog.button("No", false);
		dialog.key(Input.Keys.ENTER, true);
		dialog.key(Input.Keys.ESCAPE, false);
		return dialog;
	}
}
