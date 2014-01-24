package view;

import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class LoginFader implements EventHandler<ActionEvent> {

	private final double c;
	private final GridPane login;
	private final GridPane menu;
	private final ParallelTransition p;

	public LoginFader(double c, GridPane login, GridPane menu) {
		this.c = c;
		this.login = login;
		this.menu = menu;
		p = new ParallelTransition();
	}

	public ParallelTransition getT() {
		return p;
	}

	@Override
	public void handle(ActionEvent event) {
		if (c < 0) {
			fadeOut();
		} else {
			fadeIn();
		}
	}

	private void fade(double c, GridPane login, GridPane menu) {
		TranslateTransition t1 = new TranslateTransition(Duration.millis(500), menu);
		t1.setByX(c);

		TranslateTransition t2 = new TranslateTransition(Duration.millis(500), login);
		t2.setByX(c);
		t2.setFromX(login.getLayoutX() - c);
		t2.setToX(login.getLayoutX());

		p.getChildren().addAll(t1, t2);
		p.setInterpolator(new Interpolator() {
			@Override
			protected double curve(double t) {
				return Math.pow(t, 2);
			}
		});
		p.play();
		if (login.getOpacity() == 0) {
			login.setOpacity(1);
		} else {
			login.setOpacity(0);
		}
	}

	public void fadeIn() {
		Dashboard.setMenuTransparent(true);
		fade(500, login, menu);
	}

	public void fadeOut() {
		Dashboard.setMenuTransparent(false);
		fade(-500, login, menu);
	}
}
