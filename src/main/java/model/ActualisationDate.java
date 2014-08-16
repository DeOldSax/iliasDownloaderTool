package model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import model.persistance.Storable;

public class ActualisationDate implements Storable {
	private static final long serialVersionUID = -2661614718820065199L;
	private String time;
	private String day;

	public ActualisationDate() {
		time = new SimpleDateFormat("EEEE',' HH:mm").format(Calendar.getInstance().getTime());
		day = new SimpleDateFormat("D'-'M'-'Y").format(Calendar.getInstance().getTime());
	}

	@Override
	public String toString() {
		final String currentDay = new SimpleDateFormat("D'-'M'-'Y").format(Calendar.getInstance().getTime());

		if (currentDay.equals(day)) {
			final int index = time.indexOf(',');
			time = time.substring(index);
			time = "Heute" + time;
		} else {
			final String[] split = day.split("-");
			final String[] split2 = currentDay.split("-");
			if (Integer.valueOf(split[0]) < Integer.valueOf(split2[0]) && Integer.valueOf(split[1]) == Integer.valueOf(split2[1])) {
				final int index = time.indexOf(',');
				time = time.substring(index);
				time = "Gestern" + time;
			}
		}
		return "Letzte Aktualisierung: " + time;
	}

	@Override
	public String getStorageFileName() {
		return "time.ser";
	}
}
