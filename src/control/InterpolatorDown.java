package control;

import javafx.animation.Interpolator;

public class InterpolatorDown extends Interpolator {
	@Override
	protected double curve(double x) {
		return Math.sqrt(x);
	}
}