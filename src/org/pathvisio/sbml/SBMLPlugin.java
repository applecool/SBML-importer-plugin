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
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.plugin.Plugin;
import org.sbml.jsbml.SBMLDocument;

/**
 * SBML importer and exporter
 */
public class SBMLPlugin implements Plugin
{
	private PvDesktop desktop;
	
	private SBMLDocument lastImported = null;
	
	public void init(PvDesktop desktop) 
	{
		// save the desktop reference so we can use it later
		this.desktop = desktop;
		
		// new side tab
		JPanel mySideBarPanel = new JPanel();
		JTabbedPane sidebarTabbedPane = desktop.getSideBarTabbedPane();
		sidebarTabbedPane.add("SBML", mySideBarPanel);
		
		// register importer / exporter
		SBMLFormat sbmlFormat = new SBMLFormat(this);		
		desktop.getSwingEngine().getEngine().addPathwayExporter(sbmlFormat);
		desktop.getSwingEngine().getEngine().addPathwayImporter(sbmlFormat);
		
		
		// add our action (defined below) to the toolbar
		desktop.getSwingEngine().getApplicationPanel()
				.addToToolbar(toolbarAction);
	}

	private final MyToolbarAction toolbarAction = new MyToolbarAction();
	
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
	public void done() {}
	

	public void setLastImported(SBMLDocument document)
	{
		lastImported = document;
	}
}
