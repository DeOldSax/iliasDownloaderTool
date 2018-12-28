package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import model.persistance.Storable;

public class UpdateTime implements Storable {
	private static final long serialVersionUID = -2661614718820065199L;
	public static final String DEFAULT_VALUE = "Letzte Aktualisierung: -";
	private LocalDateTime lastUpdateTime;

	public UpdateTime() {
		this.lastUpdateTime = LocalDateTime.now();
	}

	@Override
	public String toString() {
		if (this.lastUpdateTime == null) {
			return DEFAULT_VALUE;
		}

		final LocalDate today = LocalDate.now();
		final LocalDate lastUpdateDay = LocalDate.from(this.lastUpdateTime);

		String prefix = "";
		String pattern = "HH:mm";

		final boolean lastUpdateWasToday = today.isEqual(lastUpdateDay);
		final boolean lastUpdateWasYesterday = today.minusDays(1).isEqual(lastUpdateDay);

		if (lastUpdateWasToday) {
			prefix = "Heute, ";
		} else if (lastUpdateWasYesterday) {
			prefix = "Gestern, ";
		} else {
			pattern = "EEEE d. MMMM, HH:mm";
		}

		String formattedTime = this.lastUpdateTime.format(DateTimeFormatter.ofPattern(pattern));
		return "Letzte Aktualisierung: " + prefix + formattedTime;
	}

	@Override
	public String getStorageFileName() {
		return "time.ser";
	}
}
