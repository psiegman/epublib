package nl.siegmann.epublib.viewer;

import java.util.List;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.SectionWalker;
import nl.siegmann.epublib.domain.TOCReference;

/**
 * Creates a JTree for navigating a Book via its Table of Contents.
 * 
 * @author paul
 *
 */
public class TableOfContentsTreeFactory {

	/**
	 * Wrapper around a TOCReference that gives the TOCReference's title when toString() is called
	 * 
	 * @author paul
	 *
	 */
	private static class TOCItem {
		private TOCReference tocReference;
		
		public TOCItem(TOCReference tocReference) {
			super();
			this.tocReference = tocReference;
		}
		
		public TOCReference getTOReference() {
			return tocReference;
		}

		public String toString() {
			return tocReference.getTitle();
		}
	}
	
	
	/**
	 * Creates a JTree that displays all the items in the table of contents from the book in SectionWalker.
	 * Also sets up a selectionListener that updates the SectionWalker when an item in the tree is selected.
	 * 
	 * @param sectionWalker
	 * @return
	 */
	public static JTree createTableOfContentsTree(SectionWalker sectionWalker) {
		Book book = sectionWalker.getBook();
		// Create the nodes.
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(
				book.getTitle());
		createNodes(top, book);

		// Create a tree that allows one selection at a time.
		JTree tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setRootVisible(false);
		// Listen for when the selection changes.
		tree.addTreeSelectionListener(new TableOfContentsTreeSelectionListener(sectionWalker));

		return tree;
	}

	/**
	 * Updates the SectionWalker when a tree node is selected.
	 * 
	 * @author paul
	 *
	 */
	private static class TableOfContentsTreeSelectionListener implements TreeSelectionListener {
		
		private SectionWalker sectionWalker;
		
		public TableOfContentsTreeSelectionListener(SectionWalker sectionWalker) {
			this.sectionWalker = sectionWalker;
		}
		
		public void valueChanged(TreeSelectionEvent e) {
			JTree tree = (JTree) e.getSource();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			
			if (node == null) {
				return;
			}
			TOCItem tocItem = (TOCItem) node.getUserObject();
			sectionWalker.gotoResource(tocItem.getTOReference().getResource());
		}
	}

	private static void createNodes(DefaultMutableTreeNode top, Book book) {
		createNodes(top, book, book.getTableOfContents().getTocReferences());
	}
	
	private static void createNodes(DefaultMutableTreeNode parent, Book book, List<TOCReference> tocReferences) {
		if (tocReferences == null) {
			return;
		}
		for (TOCReference tocReference: tocReferences) {
			TOCItem tocItem = new TOCItem(tocReference);
			DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(tocItem);
			createNodes(treeNode, book, tocReference.getChildren());
			parent.add(treeNode);
		}
	}
}
