package nl.siegmann.epublib.viewer;

import java.awt.GridLayout;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.browsersupport.Navigator.SectionChangeEvent;
import nl.siegmann.epublib.browsersupport.Navigator.SectionChangeListener;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.TOCReference;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang.StringUtils;

/**
 * Creates a JTree for navigating a Book via its Table of Contents.
 * 
 * @author paul
 *
 */
public class TableOfContentsPane extends JPanel implements SectionChangeListener {

	private static final long serialVersionUID = 2277717264176049700L;
	
	private MultiMap href2treeNode = new MultiValueMap();
	private JTree tree;
	
	/**
	 * Creates a JTree that displays all the items in the table of contents from the book in SectionWalker.
	 * Also sets up a selectionListener that updates the SectionWalker when an item in the tree is selected.
	 * 
	 * @param sectionWalker
	 * @return
	 */
	public TableOfContentsPane(Navigator sectionWalker) {
		super(new GridLayout(1, 0));
		tree = new JTree(createTree(sectionWalker));
		add(new JScrollPane(tree));
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
//		tree.setRootVisible(false);
		tree.addTreeSelectionListener(new TableOfContentsTreeSelectionListener(sectionWalker));
		sectionWalker.addSectionChangeEventListener(this);
	}
	
	
	/**
	 * Wrapper around a TOCReference that gives the TOCReference's title when toString() is called
	 * .createTableOfContentsTree
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
	
	private void addToHref2TreeNode(Resource resource, DefaultMutableTreeNode treeNode) {
		if (resource == null || StringUtils.isBlank(resource.getHref())) {
			return;
		}
		href2treeNode.put(resource.getHref(), treeNode);
	}
	
	private DefaultMutableTreeNode createTree(Navigator sectionWalker) {
		Book book = sectionWalker.getBook();
		TOCItem rootTOCItem = new TOCItem(new TOCReference(book.getTitle(), book.getCoverPage()));
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(rootTOCItem);
		addToHref2TreeNode(book.getCoverPage(), top);
		createNodes(top, book);
		return top;
	}
	
	/**
	 * Updates the SectionWalker when a tree node is selected.
	 * 
	 * @author paul
	 *
	 */
	private class TableOfContentsTreeSelectionListener implements TreeSelectionListener {
		
		private Navigator sectionWalker;
		
		public TableOfContentsTreeSelectionListener(Navigator sectionWalker) {
			this.sectionWalker = sectionWalker;
		}
		
		public void valueChanged(TreeSelectionEvent e) {
			JTree tree = (JTree) e.getSource();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			
			if (node == null) {
				return;
			}
			TOCItem tocItem = (TOCItem) node.getUserObject();
			sectionWalker.gotoResource(tocItem.getTOReference().getResource(), TableOfContentsPane.this);
		}
	}

	private void createNodes(DefaultMutableTreeNode top, Book book) {
		addNodesToParent(top, book.getTableOfContents().getTocReferences());
	}
	
	private void addNodesToParent(DefaultMutableTreeNode parent, List<TOCReference> tocReferences) {
		if (tocReferences == null) {
			return;
		}
		for (TOCReference tocReference: tocReferences) {
			TOCItem tocItem = new TOCItem(tocReference);
			DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(tocItem);
			addToHref2TreeNode(tocReference.getResource(), treeNode);
			addNodesToParent(treeNode, tocReference.getChildren());
			parent.add(treeNode);
		}
	}


	@Override
	public void sectionChanged(SectionChangeEvent sectionChangeEvent) {
		Collection treenodes = (Collection) href2treeNode.get(sectionChangeEvent.getCurrentResource().getHref());
		if (treenodes == null || treenodes.isEmpty()) {
			if (sectionChangeEvent.getCurrentSectionIndex() == (sectionChangeEvent.getPreviousSectionIndex() + 1)) {
				return;
			}
			tree.setSelectionPath(null);
			return;
		}
		for (Iterator iter = treenodes.iterator(); iter.hasNext();) {
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) iter.next();
			TreeNode[] path = treeNode.getPath();
			TreePath treePath = new TreePath(path);
			tree.setSelectionPath(treePath);
		}
	}
}
