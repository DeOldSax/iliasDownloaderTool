package iliasWorker;

import java.util.ArrayList;
import java.util.List;

import model.Adresse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class IliasCourseFinder {
	/**
	 * saves all subjects and urls from startpage in List subject<Adresse>
	 * 
	 * @param html
	 *            startPage
	 */
	public List<Adresse> getSubjects(String html) {
		List<Adresse> subjects = new ArrayList<Adresse>();
		String s = null;
		Document doc = Jsoup.parse(html);
		List<Element> temp = doc.select("h4");

		for (Element x : temp) {
			s = s + x.toString();
		}

		doc = Jsoup.parse(s);
		temp = doc.select("a[href]");

		for (Element x : temp) {
			String url = x.attr("abs:href");
			if (url.contains("goto_produktiv_crs_")) {
				String name = x.text();
				if (name.contains("]")) {
					int index = x.text().indexOf("]");
					name = name.substring(index + 2);
				}
				subjects.add(new Adresse(name, url, null, false, false, 0.0));
			}
		}
		return subjects;
	}
}
