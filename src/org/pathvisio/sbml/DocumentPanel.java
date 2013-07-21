package org.pathvisio.sbml;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.pathvisio.core.ApplicationEvent;
import org.pathvisio.core.ApplicationEvent.Type;
import org.pathvisio.core.Engine;
import org.pathvisio.core.Engine.ApplicationEventListener;
import org.pathvisio.gui.SwingEngine;
import org.sbml.jsbml.Annotation;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.util.filters.Filter;
import org.sbml.jsbml.util.filters.NameFilter;

public class DocumentPanel extends JPanel implements ApplicationEventListener {
	private SwingEngine eng;
	private JPanel drawPanel;
	private SBMLDocument lastImported = null;
	Engine engine;
	Desktop desktop;
	private JScrollPane treePane = new JScrollPane();
	private ExecutorService executor;

	public DocumentPanel(SwingEngine eng) {
		// TODO Auto-generated constructor stub
		this.eng = eng;
		setLayout(new BorderLayout());
		treePane = new JScrollPane(new JTree(SBMLFormat.doc));
		add(treePane);
		eng.getEngine().addApplicationEventListener(this);
		executor = Executors.newSingleThreadExecutor();
	}

	@Override
	public void applicationEvent(ApplicationEvent e) {
		// TODO Auto-generated method stub

		if (e.getType() == Type.PATHWAY_NEW
				|| e.getType() == Type.PATHWAY_OPENED)

		{
			setInput(SBMLFormat.doc);

		}

	}

	private void setInput(SBMLDocument doc) {
		// TODO Auto-generated method stub
		lastImported = doc;
		doQuery();
	}

	private void doQuery() {
		// TODO Auto-generated method stub

		executor.execute(new Runnable() {
			public void run() {
				if (lastImported == null)
					return;

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						remove(treePane);
						JTree me = new JTree();
						TreeModel se = new NavigationTree(SBMLFormat.doc)
								.getTreeModel();
						me.setModel(se);
						treePane = new JScrollPane(me);
						add(treePane);
					}
				});
			}
		});
	}
}