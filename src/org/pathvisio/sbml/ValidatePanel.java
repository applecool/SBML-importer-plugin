package org.pathvisio.sbml;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.stream.XMLStreamException;

public class ValidatePanel extends JPanel implements ActionListener {

	JFileChooser fc;
	JButton openButton;
	JButton validateButton;
	final JLabel statusbar = new JLabel(
			"Output of your selection will appear here");
	static String filename;

	public ValidatePanel() {

		super(new BorderLayout());
		// create a file chooser
		fc = new JFileChooser();
		// filtering the files based on their extensions
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"SBML(Systems Biology Markup Language) (.sbml,.xml)", "sbml",
				"xml");
		fc.setFileFilter(filter);

		openButton = new JButton("Open");
		validateButton = new JButton("Validate the file");
		openButton.addActionListener(this);
		validateButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();

		buttonPanel.add(openButton);
		buttonPanel.add(validateButton);
		buttonPanel.add(statusbar);

		add(buttonPanel, BorderLayout.CENTER);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == openButton) {

			int returnVal = fc.showOpenDialog(ValidatePanel.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				filename = file.getPath();

				statusbar.setText("You chose" + " " + file.getName());
			} else {

				statusbar.setText("You cancelled.");
			}
		}

		else if (e.getSource() == validateButton) {
			SBMLValidator val = new SBMLValidator();

			try {
				val.validate();
			} catch (XMLStreamException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			} catch (IOException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}

	}
}
