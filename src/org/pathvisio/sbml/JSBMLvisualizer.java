package org.pathvisio.sbml;

import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import org.sbml.jsbml.SBMLDocument;

/** Displays the content of an SBML file in a {@link JTree} 
 * <p>
 * Derived from org.sbml.jsbml.test.gui.JSBMLvisualizer, with significant changes.
 */
public class JSBMLvisualizer extends JFrame 
{

	/** @param document The sbml root node of an SBML file */
	public JSBMLvisualizer(SBMLDocument document) {
		super("JSBML viz");
		getContentPane().add(new JScrollPane(new JTree(document)));
		pack();
	}
	
	public void createAndShow (Component parent)
	{
		setLocationRelativeTo(parent);
		setVisible(true);
	}
	
}