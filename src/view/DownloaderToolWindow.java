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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import laf.Button;
import laf.FocusBorderOfTree;
import laf.TextFieldListener;
import model.Directory;
import model.PDF;
import model.SearchResult;
import worker.ButtonHandler;
import worker.EmailOpener;
import worker.FAQOpener;
import worker.FileSearcher;
import worker.IliasStarter;
import worker.PdfNodePopupMenu;
import worker.ResultListPopupMenu;
import worker.ResultSelector;
import worker.TreeCollapser;

public class DownloaderToolWindow {

	private final Container c;
	private final JFrame window;
	private final Background backgroundNorth, backgroundSouth;
	private final DefaultMutableTreeNode overview;
	private final JTree treeAllePdf;
	private final JScrollPane treeAllePdfScrollPane, resultListScrollPane;
	private final JTextField search;
	private final Vector<SearchResult> resultVector;
	private final JList<SearchResult> searchResults;
	private final JTreeContentFiller treeFiller;

	/**
	 * @wbp.parser.entryPoint
	 */
	public DownloaderToolWindow(IliasStarter iliasStarter) {
		LookAndFeelChanger.changeToJava();
		treeFiller = new JTreeContentFiller();
		window = new JFrame();
		c = window.getContentPane();
		c.setLayout(new BorderLayout());
		backgroundNorth = new Background();
		backgroundNorth.setBorder(new EmptyBorder(20, 20, 0, 20));
		c.add(backgroundNorth, BorderLayout.NORTH);
		backgroundSouth = new Background();
		backgroundSouth.setLayout(new GridLayout(1, 0, 20, 20));
		backgroundSouth.setBorder(new EmptyBorder(20, 20, 20, 20));
		c.add(backgroundSouth, BorderLayout.SOUTH);

		window.setSize(1200, 530);
		window.setTitle("DownloaderTool");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(true);
		window.setLocationRelativeTo(null);

		overview = new DefaultMutableTreeNode(new Directory("Übersicht", null, null));
		treeAllePdf = new JTree(overview);
		backgroundNorth.setLayout(new BorderLayout(20, 20));
		Button collapseAll = new Button("   <   ");
		backgroundNorth.add(collapseAll, BorderLayout.WEST);
		collapseAll.addMouseListener(new TreeCollapser(treeAllePdf));
		Background innerNorth = new Background();
		innerNorth.setLayout(new GridLayout(1, 0, 20, 20));
		backgroundNorth.add(innerNorth, BorderLayout.CENTER);
		Button actionButton = new Button("ungelesene Dateien anzeigen");
		innerNorth.add(actionButton);
		actionButton.addMouseListener(new ButtonHandler("ungelesen", treeAllePdf, iliasStarter, this));
		Button actionButton2 = new Button("lokal nicht vorhandene Dateien anzeigen");
		actionButton2.addMouseListener(new ButtonHandler("nicht_vorhanden", treeAllePdf, iliasStarter, this));
		innerNorth.add(actionButton2);
		Button actionButton3 = new Button("ignorierte Dateien anzeigen");
		actionButton3.addMouseListener(new ButtonHandler("ignorierte", treeAllePdf, iliasStarter, this));
		innerNorth.add(actionButton3);
		//

		treeFiller.addKurseToTree(overview, iliasStarter.getKurse());
		final Enumeration<?> children = overview.children();
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
		innerNorth.add(search);

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
		treeAllePdf.addMouseListener(new PdfNodePopupMenu());
		treeAllePdf.addMouseListener(new FocusBorderOfTree());
		treeAllePdf.setCellRenderer(new CustomNodeRenderer(iliasStarter));

		searchResults = new JList<SearchResult>();
		resultListScrollPane = new JScrollPane(searchResults);
		searchResults.setSelectionBackground(Color.BLUE);
		searchResults.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
		searchResults.setFont(new Font("Calibri", Font.PLAIN, 14));
		resultVector = new Vector<SearchResult>();
		searchResults.addMouseListener(new ResultSelector(overview, treeAllePdf, searchResults));
		searchResults.addMouseListener(new ResultListPopupMenu());
		searchResults.addKeyListener(new ResultSelector(overview, treeAllePdf, searchResults));

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeAllePdfScrollPane, resultListScrollPane);
		splitPane.setDividerLocation(570);
		splitPane.getComponent(2).setBackground(Color.BLUE);
		backgroundCenter.add(splitPane, BorderLayout.CENTER);

		CloseIcon closeIcon = new CloseIcon(Color.WHITE);
		closeIcon.setToolTipText("Beenden");

		JPanel southPanel = new JPanel(new GridLayout(1, 0, 4, 4));
		Button contactDevButton = new Button("Entwickler kontaktieren");
		contactDevButton.setHorizontalTextPosition(SwingConstants.CENTER);
		contactDevButton.addMouseListener(new EmailOpener());
		Button openFAQ = new Button("FAQ");
		openFAQ.setHorizontalAlignment(SwingConstants.CENTER);
		openFAQ.addMouseListener(new FAQOpener());
		southPanel.add(openFAQ);
		southPanel.add(contactDevButton);
		backgroundCenter.add(southPanel, BorderLayout.SOUTH);
		// backgroundSouth.add(closeIcon);
		window.setVisible(true);
	}

	public void clearResultList() {
		resultVector.removeAllElements();
		searchResults.setListData(resultVector);
	}

	public void addToResultList(PDF pdf) {
		resultVector.add(new SearchResult(pdf));
		searchResults.setListData(resultVector);
	}
}
