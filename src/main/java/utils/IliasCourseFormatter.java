package utils;

public class IliasCourseFormatter {

	public static String formatCourseName(String courseName) {
		String formattedCourseName = courseName;
		formattedCourseName = formattedCourseName.replaceAll(".*\\[[0-9]+\\](\\s)*", "");
		return formattedCourseName;
	}
}
