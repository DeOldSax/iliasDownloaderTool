package view;

import java.util.HashMap;
import java.util.Map;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import model.persistance.Settings;
import model.persistance.User;
import studportControl.Studierendenportal;
import studportControl.Transcript;

public class StudportBar extends GridPane {
	private final Button[] transcripts;
	private final Button openBarBtn;
	private final Button closeBarBtn;
	private boolean isTranscriptsVisible = true;
	private final Map<Button, Transition> activeTransitions;
	private final Dashboard dashboard;

	public StudportBar(Dashboard dashboard) {
		this.dashboard = dashboard;
		activeTransitions = new HashMap<Button, Transition>();
		setHgap(10);
		transcripts = new Button[4];
		transcripts[0] = new Button(Transcript.ALLE_LEISTUNGEN_DEUTSCH);
		transcripts[1] = new Button(Transcript.BESTANDEN_DEUTSCH);
		transcripts[2] = new Button(Transcript.ALLE_LEISTUNGEN_ENGLISCH);
		transcripts[3] = new Button(Transcript.BESTANDEN_ENGLISCH);
		closeBarBtn = new Button("X");
		closeBarBtn.setId("loginButtonCancel");
		closeBarBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				toggleTranscriptsOpacity();
			}
		});
		openBarBtn = new Button("Notenauszug");
		openBarBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				toggleTranscriptsOpacity();
			}
		});
		for (int i = 0; i < transcripts.length; i++) {
			add(transcripts[i], i + 1, 0);
			transcripts[i].setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					startDownload(event);
				}
			});
		}
		toggleTranscriptsOpacity();
	}

	private void startDownload(ActionEvent event) {
		User user = Settings.getInstance().getUser();
		final String name = user.getName();
		final String password = user.getPassword();

		if (name == null || name.isEmpty() || password.isEmpty() || password == null) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					dashboard.fadeInLogin();
					dashboard.setStatusText("Deine Benutzerdaten fehlen!", true);
				}
			});
			return;
		}

		new Thread(new Studierendenportal(dashboard, name, password, (Button) event.getSource(), this)).start();
		changeButtonstate((Button) event.getSource(), true);
	}

	private void toggleTranscriptsOpacity() {
		if (isTranscriptsVisible) {
			add(openBarBtn, 0, 0);
			isTranscriptsVisible = false;
			for (int i = 0; i < transcripts.length; i++) {
				transcripts[i].setOpacity(0.0);
			}
			getChildren().remove(closeBarBtn);
		} else {
			getChildren().remove(openBarBtn);
			isTranscriptsVisible = true;
			for (int i = 0; i < transcripts.length; i++) {
				transcripts[i].setOpacity(1.0);
			}
			add(closeBarBtn, 0, 0);
		}
	}

	public void changeButtonstate(Button button, boolean activate) {
		if (activate) {
			button.setStyle("-fx-background-color: linear-gradient(orange, orangered)");
			button.setMouseTransparent(true);
			FadeTransition transition = new FadeTransition(Duration.millis(1000), button);
			activeTransitions.put(button, transition);
			transition.setFromValue(1.0);
			transition.setToValue(0.0);
			transition.setAutoReverse(true);
			transition.setCycleCount(Timeline.INDEFINITE);
			transition.play();
		} else {
			final Transition transition = activeTransitions.get(button);
			transition.stop();
			button.setMouseTransparent(false);
			button.setOpacity(1.0);
			button.setStyle(null);
			button.setId("button");
		}
	}
}
