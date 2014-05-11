package model;

import javafx.scene.image.ImageView;

public class IliasHyperlink extends IliasTreeNode {

	private static final long serialVersionUID = 5357926433787636223L;

	public IliasHyperlink(String name, String url, IliasFolder parentFolder) {
		super(name, url, parentFolder);
	}

	@Override
	public ImageView getGraphic() {
		return new ImageView("img/hyperlink.png");
	}

}
