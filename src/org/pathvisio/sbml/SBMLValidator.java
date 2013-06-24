package org.pathvisio.sbml;
import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
public class SBMLValidator {
	public void validate () throws XMLStreamException, IOException
	{
		
		String selectFile =  ValidatePanel.filename;
		
		System.out.println("the file is "+ selectFile);
		SBMLReader reader     = new SBMLReader();
		SBMLDocument document;
		long start, stop;

		start    = System.currentTimeMillis();
		document = reader.readSBML(selectFile);
		stop     = System.currentTimeMillis();

		if (document.getErrorCount() > 0)
		{
			print("Encountered the following errors while reading the SBML file:\n");
			document.printErrors(System.out);
			print("\nFurther consistency checking and validation aborted.\n");
			System.exit(1);
		}
		else
		{
			long errors = document.checkConsistency();
			long size   = new File(selectFile).length();

			println("            filename: " + selectFile);
			println("           file size: " + size);
			println("      read time (ms): " + (stop - start));
			println(" validation error(s): " + errors);

			if (errors > 0)
			{
				document.printErrors(System.out);
				System.exit(1);
			}
		}
	}


	static void print (String msg)
	{
		System.out.print(msg);
	}

	static void println (String msg)
	{
		System.out.println(msg);
	}
}
