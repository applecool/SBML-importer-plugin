package org.pathvisio.sbml;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import org.apache.commons.collections15.Transformer;
import org.pathvisio.core.preferences.PreferenceManager;
import org.pathvisio.gui.SwingEngine;
import org.pathvisio.sbml.SBMLPlugin.PlPreference;


import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;

public class FruchtRein extends LayoutAbstract{

	FRLayout<String,String> l; 
	Transformer<String,Point2D> in;
	
	
	FruchtRein(SwingEngine swingEngine){
		super.swingEngine = swingEngine;
		super.pwy = swingEngine.getEngine().getActivePathway();
		
		createDSMultigraph();
		l = new FRLayout<String,String>( g );
		Dimension d = new Dimension(800,600);
		l.setSize(d);
		double att = Double.parseDouble(PreferenceManager.getCurrent().get(PlPreference.PL_LAYOUT_FR_ATTRACTION));
		double rep = Double.parseDouble(PreferenceManager.getCurrent().get(PlPreference.PL_LAYOUT_FR_REPULSION));
		l.setAttractionMultiplier(att);
		l.setRepulsionMultiplier(rep);
		
		l.initialize();
		while(!l.done()){
			l.step();
		}
		drawNodes((AbstractLayout<String,String>) l);
		//re-draw the lines
		drawLines();
	}
}
