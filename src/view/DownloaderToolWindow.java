package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import laf.Button;
import laf.FocusBorderOfTree;
import laf.TextFieldListener;
import model.Adresse;
import worker.ButtonHandler;
import worker.EmailOpener;
import worker.FileSearcher;
import worker.IliasStarter;
import worker.PdfNodePopupMenu;
import worker.ResultListPopupMenu;
import worker.ResultSelector;

public class DownloaderToolWindow {

	private final Container c;
	private final JFrame window;
	private final Background backgroundNorth, backgroundSouth;
	private final DefaultMutableTreeNode overview;
	private final JTree treeAllePdf;
	private final JScrollPane treeAllePdfScrollPane, resultListScrollPane;
	private final JTextField search;
	private final Vector<String> resultVector;
	private final JList<String> searchResults;
	private final JTreeContentFiller treeFiller;

	/**
	 * @wbp.parser.entryPoint
	 */
	public DownloaderToolWindow(IliasStarter iliasStarter) {
		treeFiller = new JTreeContentFiller();
		window = new JFrame();
		c = window.getContentPane();
		c.setLayout(new BorderLayout());
		backgroundNorth = new Background();
		backgroundNorth.setLayout(new GridLayout(1, 0, 20, 20));
		backgroundNorth.setBorder(new EmptyBorder(20, 20, 0, 20));
		c.add(backgroundNorth, BorderLayout.NORTH);
		backgroundSouth = new Background();
		backgroundSouth.setLayout(new GridLayout(1, 0, 20, 20));
		backgroundSouth.setBorder(new EmptyBorder(20, 20, 20, 20));
		c.add(backgroundSouth, BorderLayout.SOUTH);

		window.setSize(920, 500);
		window.setTitle("DownloaderTool");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(true);
		window.setLocationRelativeTo(null);

		Button actionButton = new Button("   ungelesene Dateien anzeigen   ");
		backgroundNorth.add(actionButton);
		overview = new DefaultMutableTreeNode("Übersicht");
		treeAllePdf = new JTree(overview);
		actionButton.addMouseListener(new ButtonHandler("ungelesen", treeAllePdf, overview, iliasStarter, this));
		Button actionButton2 = new Button("   lokal nicht vorhandene Dateien anzeigen   ");
		actionButton2.addMouseListener(new ButtonHandler("nicht_vorhanden", treeAllePdf, overview, iliasStarter, this));
		backgroundNorth.add(actionButton2);
		//

		treeFiller.addKurseToTree(overview, iliasStarter.getKurse());
		final Enumeration children = overview.children();
		while (children.hasMoreElements()) {
			DefaultMutableTreeNode object = (DefaultMutableTreeNode) children.nextElement();
			treeAllePdf.scrollPathToVisible(new TreePath(object.getPath()));
		}

		search = new JTextField("suchen");
		search.setSelectionColor(Color.BLUE);
		search.setForeground(Color.LIGHT_GRAY);
		search.setFont(new Font("Calibri", Font.PLAIN, 15));
		search.setBorder(new EmptyBorder(4, 4, 4, 4));
		search.setBackground(new Color(255, 255, 255));
		search.addKeyListener((new FileSearcher(iliasStarter.getAllPdfs(), search, this)));
		search.addMouseListener(new TextFieldListener(search));
		backgroundNorth.add(search);

		Background backgroundCenter = new Background();
		backgroundCenter.setLayout(new BorderLayout());
		backgroundCenter.setBorder(new EmptyBorder(20, 20, 20, 20));
		c.add(backgroundCenter, BorderLayout.CENTER);
		treeAllePdf.setForeground(Color.WHITE);
		treeAllePdf.setFont(new Font("Calibri", Font.PLAIN, 14));
		treeAllePdf.setBorder(null);
		treeAllePdf.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		treeAllePdf.setRootVisible(false);
		DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) treeAllePdf.getCellRenderer();
		renderer.setTextSelectionColor(Color.GREEN);
		renderer.setBackgroundSelectionColor(Color.CYAN);
		renderer.setBorderSelectionColor(Color.LIGHT_GRAY);
		treeAllePdfScrollPane = new JScrollPane(treeAllePdf);
		treeAllePdfScrollPane.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
		treeAllePdf.addMouseListener(new PdfNodePopupMenu(iliasStarter));
		treeAllePdf.addMouseListener(new FocusBorderOfTree());
		treeAllePdf.setCellRenderer(new CustomNodeRenderer(iliasStarter));

		searchResults = new JList<String>();
		resultListScrollPane = new JScrollPane(searchResults);
		searchResults.setSelectionBackground(Color.BLUE);
		searchResults.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
		searchResults.setFont(new Font("Calibri", Font.PLAIN, 14));
		resultVector = new Vector<String>();
		searchResults.addMouseListener(new ResultSelector(overview, treeAllePdf, searchResults));
		searchResults.addMouseListener(new ResultListPopupMenu(iliasStarter));
		searchResults.addKeyListener(new ResultSelector(overview, treeAllePdf, searchResults));

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeAllePdfScrollPane, resultListScrollPane);
		splitPane.setDividerLocation(350);
		splitPane.getComponent(2).setBackground(Color.BLUE);
		backgroundCenter.add(splitPane, BorderLayout.CENTER);

		CloseIcon closeIcon = new CloseIcon(Color.WHITE);
		closeIcon.setToolTipText("Beenden");

		Button contactDevButton = new Button("contact developer");
		contactDevButton.addMouseListener(new EmailOpener());
		backgroundCenter.add(contactDevButton, BorderLayout.SOUTH);
		// backgroundSouth.add(closeIcon);

		window.setVisible(true);
	}

	public void clearResultList() {
		resultVector.removeAllElements();
		searchResults.setListData(resultVector);
	}

	public void addToResultList(String name, Adresse adresse) {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		if (adresse != null) {
			sb.append(" [").append(adresse.getName()).append("]");
		}
		resultVector.add(sb.toString());
		searchResults.setListData(resultVector);
	}
}
