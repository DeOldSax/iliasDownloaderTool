package control;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeItem.TreeModificationEvent;
import model.PDF;

public class NodeCollapseHandler implements EventHandler<TreeModificationEvent<Object>> {

	@Override
	public void handle(TreeModificationEvent<Object> event) {
		System.out.println(event.getSource().getValue());
		final TreeNodeGraphicChanger graphicChanger = new TreeNodeGraphicChanger();
		final ObservableList<TreeItem<Object>> children = event.getTreeItem().getChildren();
		for (TreeItem<Object> treeItem : children) {
			if (treeItem.getValue() instanceof PDF) {
				final PDF pdf = (PDF) treeItem.getValue();
				if (!pdf.isIgnored()) {
					graphicChanger.changeGraphicInTreeView((PDF) treeItem.getValue());
				}
			}
		}
	}
}
