/*******************************************************************************
 * pathlayout,
 * 
 * a library for PathVisio plug-ins with layout algorithms
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.pathvisio.sbml;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.pathvisio.core.model.ObjectType;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.core.view.Graphics;
import org.pathvisio.core.view.VPathway;
import org.pathvisio.gui.SwingEngine;
/**
 * LayoutAbstract Class<p>
 * Abstract class with functions used by all layout algorithms
 * @author Christ Leemans
 *
 */
public abstract class LayoutAbstract {
	public static String NAME;
	public static String DESCRIPTION;
	Pathway pwy;
	VPathway vpwy;
	SwingEngine swingEngine;
	boolean selection;
	List<PathwayElement> pwyNodes;
	List<PathwayElement> pwyLines;
	
	LayoutAbstract(SwingEngine se){
		this(se,false);
	}
	LayoutAbstract(SwingEngine se,boolean selection){
		this.pwy = se.getEngine().getActivePathway();
		this.vpwy = se.getEngine().getActiveVPathway();
		this.swingEngine = se;
		this.selection = selection;
		pwyNodes = new ArrayList<PathwayElement>();
		pwyLines = new ArrayList<PathwayElement>();
		if (selection){
			List<Graphics> graphics = vpwy.getSelectedGraphics();
			for (Graphics g : graphics){
				PathwayElement pe = g.getPathwayElement();
				if (pe.getObjectType().equals(ObjectType.DATANODE)){
					pwyNodes.add(pe);
				}
				else if (pe.getObjectType().equals(ObjectType.LINE)){
					pwyLines.add(pe);
				}
			}
		}
		else {
			for (PathwayElement pe : pwy.getDataObjects()){
				if (pe.getObjectType().equals(ObjectType.DATANODE)){
					pwyNodes.add(pe);
				}
				else if (pe.getObjectType().equals(ObjectType.LINE)){
					pwyLines.add(pe);
				}
			}
		}
		for (PathwayElement pe : pwyNodes){
			//Make sure each element has a unique graphId
			try {
				pe.getGraphId().isEmpty();
			}
			catch (NullPointerException e){
				pe.setGraphId(pwy.getUniqueGraphId());
			}
		}
	}
	
	protected void setLocations(Map<String,Point2D> points){
		double plusx = 0;
		if (selection){
			// if it's the layout of a selection, first put the selection to 0, then put them on the right side of the other Pathway Elements
			for (PathwayElement pe: pwyNodes){
				pe.setMCenterX(0);
			}
			PathwayElement lastx = pwy.getDataObjects().get(0);
			for (PathwayElement pe : pwy.getDataObjects()){
				if (lastx.getMCenterX()<pe.getMCenterX()){
					lastx = pe;
				}
			}
			plusx = lastx.getMCenterX()+lastx.getMWidth()/2;
		}
		double minx = 0;
		double miny = 0;
		boolean first = true;
		for (Entry<String,Point2D> e : points.entrySet()){
			double x = e.getValue().getX();
			double y = e.getValue().getY();
			if (first){
				minx = x;
				miny = y;
				first = false;
			}
			else {
				if (x<minx){
					minx = x;
				}
				if (y<miny){
					miny = y;
				}
			}
		}
		for (Entry<String,Point2D> e : points.entrySet()){
			PathwayElement pe = pwy.getElementById(e.getKey());
			if (minx<0){
				pe.setMCenterX(e.getValue().getX()+Math.abs(minx)+pe.getMWidth()/2+plusx);
			}
			else {
				pe.setMCenterX(e.getValue().getX()-minx+pe.getMWidth()/2+plusx);
			}
			if (miny<0){
				pe.setMCenterY(e.getValue().getY()+Math.abs(miny)+pe.getMHeight()/2);
			}
			else {
				pe.setMCenterY(e.getValue().getY()-Math.abs(miny)+pe.getMHeight()/2);
			}
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