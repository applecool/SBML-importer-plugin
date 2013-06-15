package org.pathvisio.sbml;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.pathvisio.core.model.ConverterException;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.sbml.peer.PeerModel;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.xml.stax.SBMLReader;

public class SbmlImportHelper
{
	private PeerModel br;
	
	
	public Pathway doImport(File file) throws ConverterException
	{
		try {
			SBMLDocument doc = new SBMLReader().readSBML(file.getAbsolutePath());

			br = PeerModel.createFromDoc(doc, file);			
			return br.getPathway();
		} 
		catch (IOException ex) 
		{
			throw new ConverterException (ex);
		}
		catch (XMLStreamException ex) 
		{
			throw new ConverterException (ex);
		}
	}

	public SBMLDocument getDocument()
	{
		return br.getDoc();
	}
	
}
