// PathSBML Plugin
// SBML Plugin for PathVisio.
// Copyright 2013 developed for Google Summer of Code
//
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// 
// http://www.apache.org/licenses/LICENSE-2.0 
//  
// Unless required by applicable law or agreed to in writing, software 
// distributed under the License is distributed on an "AS IS" BASIS, 
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
// See the License for the specific language governing permissions and 
// limitations under the License.
//
package org.pathvisio.sbml;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.bridgedb.Xref;
import org.pathvisio.core.ApplicationEvent;
import org.pathvisio.core.ApplicationEvent.Type;
import org.pathvisio.core.Engine;
import org.pathvisio.core.Engine.ApplicationEventListener;
import org.pathvisio.core.model.ObjectType;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.core.view.GeneProduct;
import org.pathvisio.core.view.Graphics;
import org.pathvisio.core.view.VPathway;
import org.pathvisio.core.view.VPathwayElement;
import org.pathvisio.gui.SwingEngine;
import org.pathvisio.sbgn.SbgnFormat;
import org.pathvisio.sbgn.SbgnTemplates;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.Species;

/**
 * This class adds action to the SBML side pane.
 * 
 * When there is an active pathway, the side pane displays the components of
 * the SBML file.
 * 
 * @author applecool
 *
 */
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
	
	/**
	 * This method sets the input as the SBML Document if a new pathway is opened.
	 * 
	 */
	@Override
	public void applicationEvent(ApplicationEvent e) {
		// TODO Auto-generated method stub

		if (e.getType() == Type.PATHWAY_NEW
				|| e.getType() == Type.PATHWAY_OPENED)

		{
			setInput(SBMLFormat.doc);

		}

	}
	
	/**
	 * This method is invoked in the applicationEvent if the pathway is new
	 * or if a new pathway is opened.
	 *  
	 * @param doc
	 */
	private void setInput(SBMLDocument doc) {
		// TODO Auto-generated method stub
		lastImported = doc;
		doQuery();
	}
	
						
	

	
	private void highlightResults(PathwayElement pe) {
		Rectangle2D interestingRect = null;
	
		VPathway vpy = eng.getEngine().getActiveVPathway();
		for (VPathwayElement velt : vpy.getDrawingObjects()) {
			if (velt instanceof GeneProduct) {
				GeneProduct gp = (GeneProduct) velt;
			

					if (pe.equals(gp.getPathwayElement())) {
						gp.highlight(Color.YELLOW);
						if (interestingRect == null) {
							interestingRect = gp.getVBounds();
						}
						break;
					}
				
			}
		}
		if (interestingRect != null)
			vpy.getWrapper().scrollTo(interestingRect.getBounds());
	}		
	
	private void unhighLightAll() {
		Rectangle2D interestingRect = null;
	
		VPathway vpy = eng.getEngine().getActiveVPathway();
		for (VPathwayElement velt : vpy.getDrawingObjects()) {
			for(PathwayElement pe :eng.getEngine().getActivePathway().getDataObjects())
			if (velt instanceof GeneProduct) {
				GeneProduct gp = (GeneProduct) velt;
			

					if (pe.equals(gp.getPathwayElement())) {
						gp.unhighlight();
						
						break;
					}
				
			}
		}
	
	}		
			
	/**
	 * This method is invoked by the setInput function.
	 * 
	 * This method adds and removes the tree component from the side pane.
	 * 
	 */
	private void doQuery() {
		// TODO Auto-generated method stub

		executor.execute(new Runnable() {
			public void run() {
				if (lastImported == null)
					return;

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						remove(treePane);
						final JTree elementTree = new JTree();
						elementTree.addTreeSelectionListener(new TreeSelectionListener() {
						    public void valueChanged(TreeSelectionEvent e) {
						        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
						        		elementTree.getLastSelectedPathComponent();

						    /* if nothing is selected */ 
						        if (node == null) return;
						        
						       
						       Species sp= (Species) node.getUserObject();
						
						    System.out.println(sp.toString());
						unhighLightAll();
						    highlightResults(eng.getEngine().getActivePathway().getElementById(sp.toString()));
						    
						    }});
						TreeModel elementModel = new NavigationTree(
								SBMLFormat.doc).getTreeModel();
						elementTree.setModel(elementModel);
						treePane = new JScrollPane(elementTree);
						add(treePane);
					}
				});
			}
		});
	}
}