package model;

import javafx.scene.image.ImageView;

public class IliasForum extends IliasTreeNode {
	private static final long serialVersionUID = 5332458530562614267L;

	public IliasForum(String name, String url, IliasFolder parentDirectory) {
		super(name, url, parentDirectory);
	}

	@Override
	public ImageView getGraphic() {
		return new ImageView("img/forum.png");
	}
}
