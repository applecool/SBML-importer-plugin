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

import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.plugin.Plugin;
import org.sbml.jsbml.SBMLDocument;

/**
 * SBML importer and exporter
 */
public class SBMLPlugin implements Plugin
{
	private PvDesktop desktop;
	private SbmlTreeAction sbmlTreeAction;
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
		
		sbmlTreeAction = new SbmlTreeAction();
		sbmlTreeAction.setEnabled(false);
		desktop.registerMenuAction("File", sbmlTreeAction);
	}

	public void done() {}
	
	private class SbmlTreeAction extends AbstractAction
	{
		SbmlTreeAction()
		{
			super ("View SBML document");
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			if (lastImported != null)
			{
				JSBMLvisualizer vis = new JSBMLvisualizer(lastImported);
				vis.createAndShow(desktop.getFrame());
			}
		}
	}

	public void setLastImported(SBMLDocument document)
	{
		lastImported = document;
		sbmlTreeAction.setEnabled(true);
	}
}
