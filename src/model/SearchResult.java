package model;

public class SearchResult {
	private final PDF pdf;

	public SearchResult(PDF pdf) {
		this.pdf = pdf;
	}

	public PDF getPdf() {
		return pdf;
	}

	@Override
	public String toString() {
		return pdf.getName() + " [" + pdf.getRootCourse() + "]";
	}
}
