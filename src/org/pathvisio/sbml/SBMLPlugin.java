// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2009 BiGCaT Bioinformatics
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

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.pathvisio.core.preferences.Preference;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.plugin.Plugin;
import org.sbml.jsbml.SBMLDocument;

/**
 * SBML importer and exporter
 */
public class SBMLPlugin implements Plugin {
	private PvDesktop desktop;

	private SBMLDocument lastImported = null;
	JPanel mySideBarPanel;

	public void init(PvDesktop desktop) {
		// save the desktop reference so we can use it later
		this.desktop = desktop;

		// register importer / exporter
		SBMLFormat sbmlFormat = new SBMLFormat(this);
		desktop.getSwingEngine().getEngine().addPathwayExporter(sbmlFormat);
		desktop.getSwingEngine().getEngine().addPathwayImporter(sbmlFormat);

		// add our action (defined below) to the toolbar
		desktop.getSwingEngine().getApplicationPanel()
				.addToToolbar(toolbarAction);
		desktop.getSwingEngine().getApplicationPanel()
		.addToToolbar(toolbarAction2);

		// add new SBML side pane
		DocumentPanel pane = new DocumentPanel(desktop.getSwingEngine());
		JTabbedPane sidebarTabbedPane = desktop.getSideBarTabbedPane();
		sidebarTabbedPane.add("SBML", pane);
		
		// add functionality to the pane
		desktop.getSwingEngine().getEngine().addApplicationEventListener(pane);

	}

	private final MyToolbarAction toolbarAction = new MyToolbarAction();
	private final MyToolbarAction2 toolbarAction2 = new MyToolbarAction2();
	
	/**
	 * This class adds action to the Validate button.
	 * 
	 * When the button is clicked, a dialog box is opened where an
	 * SBML file can be chosen to validate.
	 * @author ShellZero
	 *
	 */
	private class MyToolbarAction extends AbstractAction {

		MyToolbarAction() {
			putValue(NAME, "Validate");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			ValidatePanel vp = new ValidatePanel();
			JDialog d = new JDialog(desktop.getFrame(), "Validate");
			d.getContentPane().add(vp);
			d.pack();
			d.setVisible(true);

		}

	}
	
	/**
	 * This class adds the action to the Force Directed Layout button.
	 * 
	 * Works properly only with three data nodes. Doesn't work with process nodes.
	 * This method is added just to experiment with FR Layout algorithm.
	 * 
	 * @author ShellZero
	 *
	 */
	private class MyToolbarAction2 extends AbstractAction {

		MyToolbarAction2() {
			putValue(NAME, "ForceDirectedLayout");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			new FruchtRein(desktop.getSwingEngine());

		}

	}
	
	public static enum PlPreference implements Preference
	{
		PL_LAYOUT_FR_ATTRACTION("0.5"),
		PL_LAYOUT_FR_REPULSION("1"),
		PL_LAYOUT_SPRING_FORCE("0.33"),
		PL_LAYOUT_SPRING_REPULSION("100"),
		PL_LAYOUT_SPRING_STRETCH("0.7");
		
	
		private final String defaultVal;
		
		PlPreference (String _defaultVal) { defaultVal = _defaultVal; }
		
		@Override
		public String getDefault() { return defaultVal; }
	}

	public void done() {
	}
	
	/**
	 * This method is called in the SBMLFormat.java
	 * This method sets the imported document to the lastImported variable.
	 * 
	 * @param document
	 */
	public void setLastImported(SBMLDocument document) {
		lastImported = document;

	}

}
