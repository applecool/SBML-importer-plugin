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

import java.awt.geom.Point2D;
import java.util.List;

import org.pathvisio.core.model.ObjectType;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.gui.SwingEngine;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;

public abstract class LayoutAbstract {
	
	Pathway pwy;
	SwingEngine swingEngine;
	Graph<String,String> g;
	
	protected void createDSMultigraph(){
		g = new DirectedSparseMultigraph<String, String>();
		List<PathwayElement> elements = pwy.getDataObjects();
		for (PathwayElement element : elements){
			if (element.getObjectType().equals(ObjectType.DATANODE)){
				g.addVertex(element.getGraphId());
				Point2D point = new Point2D.Double();
				point.setLocation(element.getMCenterX(),element.getMCenterY());
			}
			else if(element.getObjectType().equals(ObjectType.LINE)){
				g.addEdge(element.getGraphId(),element.getStartGraphRef(), element.getEndGraphRef());
			}
		}
	}
	
	
	protected void drawNodes(AbstractLayout<String,String> l){
		for (String v : l.getGraph().getVertices()){
			l.transform(v);
			double x = l.getX(v);
			double y = l.getY(v);
			PathwayElement e = pwy.getElementById(v);
			x = x + .5 * e.getMWidth();
			y = y + .5 * e.getMHeight();
			e.setMCenterX(x);
			e.setMCenterY(y);
		}
	}
	
	protected void drawLines(){
		for (PathwayElement line : pwy.getDataObjects())
			if (line.getObjectType().equals(ObjectType.LINE)){
				PathwayElement startNode = pwy.getElementById(line.getStartGraphRef());
				PathwayElement endNode = pwy.getElementById(line.getEndGraphRef());
				line.getMStart().unlink();
				line.getMEnd().unlink();
				double differenceX;
				double differenceY;
				boolean startBiggerX = true;
				boolean startBiggerY = true;
				
				if (startNode.getMCenterX() < endNode.getMCenterX()){
					double endSide = endNode.getMCenterX() - endNode.getMWidth()/2;
					double startSide = startNode.getMCenterX() + startNode.getMWidth()/2;
					differenceX = endSide - startSide;
					startBiggerX = false;
				}
				else {
					double startSide = startNode.getMCenterX() - startNode.getMWidth()/2;
					double endSide = endNode.getMCenterX() + endNode.getMWidth()/2;
					differenceX = startSide - endSide;
				}
				if (startNode.getMCenterY() < endNode.getMCenterY()){
					double startSide = startNode.getMCenterY() + startNode.getMHeight()/2;
					double endSide = endNode.getMCenterY() - endNode.getMHeight()/2;
					differenceY = endSide - startSide;
					startBiggerY = false;
				}
				else {
					double startSide = startNode.getMCenterY() - startNode.getMHeight()/2;
					double endSide = endNode.getMCenterY() + endNode.getMHeight()/2;
					differenceY = startSide - endSide;
				}
				
				if (differenceX>differenceY && startBiggerX) {
					line.setMStartY(startNode.getMCenterY());
					line.setMStartX(startNode.getMCenterX() - startNode.getMWidth() / 2);
					line.setMEndY(endNode.getMCenterY());
					line.setMEndX(endNode.getMCenterX() + endNode.getMWidth() / 2);
				} else if (differenceX>differenceY && !startBiggerX) {
					line.setMStartY(startNode.getMCenterY());
					line.setMStartX(startNode.getMCenterX() + startNode.getMWidth() / 2);
					line.setMEndY(endNode.getMCenterY());
					line.setMEndX(endNode.getMCenterX() - endNode.getMWidth() / 2);
				} else if (differenceX<differenceY && startBiggerY){
					line.setMStartX(startNode.getMCenterX());
					line.setMStartY((startNode.getMCenterY() - startNode.getMHeight() /2));
					line.setMEndX(endNode.getMCenterX());
					line.setMEndY(endNode.getMCenterY() + startNode.getMHeight() /2);
				} else {
					line.setMStartX(startNode.getMCenterX());
					line.setMStartY(startNode.getMCenterY() + startNode.getMHeight() / 2);
					line.setMEndX(endNode.getMCenterX());
					line.setMEndY(endNode.getMCenterY() - endNode.getMHeight() / 2);
				}
				line.getMStart().linkTo(startNode);
				line.getMEnd().linkTo(endNode);
			}
	}
}
