package org.pathvisio.sbml;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jws.WebParam.Mode;
import javax.xml.stream.XMLStreamException;

import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.ObjectType;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.sbgn.SbgnFormat;
import org.pathvisio.sbgn.SbgnImportHelper;
import org.pathvisio.sbgn.SbgnTemplates;
import org.pathvisio.sbml.peer.PeerModel;
import org.pathvisio.sbml.peer.PeerSpecies;
import org.sbgn.ArcClazz;
import org.sbgn.GlyphClazz;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.SBMLWriter;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.ext.layout.BoundingBox;
import org.sbml.jsbml.ext.layout.Dimensions;
import org.sbml.jsbml.ext.layout.ExtendedLayoutModel;
import org.sbml.jsbml.ext.layout.Layout;
import org.sbml.jsbml.ext.layout.LayoutConstants;
import org.sbml.jsbml.ext.layout.Point;
import org.sbml.jsbml.ext.layout.SpeciesGlyph;
import org.sbml.jsbml.xml.stax.SBMLReader;


public class SbmlExportHelper
{
	private final Pathway pathway;
	private final File file;
	

	

	ListOf<Species> listOfSpecies = new ListOf<Species>();
	SbmlExportHelper (Pathway pathway, File file)
	{
		this.pathway = pathway;
		this.file = file;
	

		
		
	}
	


	public void doExport() throws ConverterException
	{
	
		//doGlyphs();
	//	doPorts();
		//doArcs();
		//doStates();
	//	doArcChildren();
	//	doComplexChildren();
	//	linkArcs();
	//	fixCompartmentRefs();
		for (PathwayElement elt : pathway.getDataObjects())
		{
		
		addElement(elt);
		}
	SBMLDocument doc= new SBMLDocument();
	doc.setModel(doModel());
	doc.setLevelAndVersion(3, 1);
	
	try {
		 SBMLWriter w=new SBMLWriter();
		 
		w.writeSBMLToFile(doc, file.getAbsolutePath());
	} catch (SBMLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (XMLStreamException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	
	public void addElement(PathwayElement elt)
	{
		String sbgnClass = elt.getDynamicProperty(SbgnFormat.PROPERTY_SBGN_CLASS);
		if (sbgnClass != null)
		{
			if (elt.getObjectType() == ObjectType.LINE)
			{
				ArcClazz ac = ArcClazz.fromClazz(sbgnClass);
				switch (ac)
				{
				case CONSUMPTION:
				case PRODUCTION:
				case CATALYSIS:
				case STIMULATION:
					addSpeciesReference (elt);
				}
			}
			else
			{
				GlyphClazz gc = GlyphClazz.fromClazz(sbgnClass);
				switch (gc)
				{
				case PROCESS:
				case UNCERTAIN_PROCESS:
				case OMITTED_PROCESS:
				case ASSOCIATION:
				case DISSOCIATION:
					addReaction (elt);
					break;
				case SIMPLE_CHEMICAL:
				case SIMPLE_CHEMICAL_MULTIMER:
				case MACROMOLECULE:
				case MACROMOLECULE_MULTIMER:
					addSpecies (elt, gc);
					break;
				}
			}
		}
		else
		{
			// we only handle SBGN elements for now.
		}
		
	}
	private void addSpeciesReference(PathwayElement elt)
	{
		
	}

	private void addSpecies(PathwayElement elt, GlyphClazz gc)
	{ Species sp= new Species();
	
		sp.setName(elt.getTextLabel());
		sp.setId(elt.getGraphId());
		
		listOfSpecies.add(sp);
	}
	private void addReaction(PathwayElement elt)
	{

	}
	


	/** checks if the given SBML document uses the SBML-layout extension */
	private Model doModel()
	{
		Model model =new Model();

		model.setListOfSpecies(listOfSpecies);
		return model;
	}

}