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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

import org.pathvisio.core.debug.Logger;
import org.pathvisio.core.util.ProgressKeeper;
import org.pathvisio.gui.ProgressDialog;
import org.pathvisio.sbml.SBMLPlugin;

import uk.ac.ebi.biomodels.ws.BioModelsWSClient;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * This class creates the content in the Dialog of the Search
 */
public class BioModelPanel extends JPanel {
	
	SBMLPlugin plugin;
	public static Border etch = BorderFactory.createEtchedBorder();
	JComboBox clientDropdown;

	JTable resultTable;
	int i = 0;

	private JTextField txtId;

	private JScrollPane resultspane;

	public int flag = 0;
	private JTextField pubXref;

	private JLabel tipLabel;

	public BioModelPanel(final SBMLPlugin plugin) {

		this.plugin = plugin;

		setLayout(new BorderLayout());

		pubXref = new JTextField();

		tipLabel = new JLabel(
				"Tip: use Biomodel name (e.g.:'Tyson1991 - Cell Cycle 6 var')");
		
		tipLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
		
		Action searchLiteratureAction = new AbstractAction("searchlit") {
			public void actionPerformed(ActionEvent e) {
				try {
					resultspane.setBorder(BorderFactory.createTitledBorder(
							etch, "Pathways"));
					search();
				} catch (Exception ex) {
					JOptionPane
							.showMessageDialog(BioModelPanel.this,
									ex.getMessage(), "Error",
									JOptionPane.ERROR_MESSAGE);
					Logger.log.error("Error searching Biomodels", ex);
				}
			}

		};

		pubXref.addActionListener(searchLiteratureAction);
		JPanel searchBox = new JPanel();
		FormLayout layoutf = new FormLayout(
				"p,3dlu,120px,2dlu,30px,fill:pref:grow,3dlu,fill:pref:grow,3dlu",
				"pref, pref, 4dlu, pref, 4dlu, pref");
		CellConstraints ccf = new CellConstraints();

		searchBox.setLayout(layoutf);
		searchBox.setBorder(BorderFactory.createTitledBorder(etch));

		JPanel searchOptBox = new JPanel();
		FormLayout layout = new FormLayout(
				"3dlu,p,3dlu,2dlu,30px,fill:pref:grow,2dlu",
				"pref, pref, 4dlu, pref, 4dlu, pref");
		CellConstraints cc = new CellConstraints();

		searchOptBox.setLayout(layout);
		searchOptBox.setBorder(BorderFactory.createTitledBorder(etch,
				"Search options"));

		searchOptBox.add(new JLabel("Biomodel name"), cc.xy(2, 1));
		searchOptBox.add(pubXref, cc.xyw(4, 1, 3));
		searchOptBox.add(tipLabel, cc.xyw(2, 2, 5));

		Vector<String> clients = new Vector<String>(plugin.getClients()
				.keySet());
		Collections.sort(clients);

		clientDropdown = new JComboBox(clients);
		clientDropdown.setSelectedIndex(0);
		clientDropdown.setRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(final JList list,
					final Object value, final int index,
					final boolean isSelected, final boolean cellHasFocus) {
				String strValue = SBMLPlugin.shortClientName(value.toString());
				return super.getListCellRendererComponent(list, strValue,
						index, isSelected, cellHasFocus);
			}
		});

		searchOptBox.add(clientDropdown, cc.xy(6, 1));

		if (plugin.getClients().size() < 2)
			clientDropdown.setVisible(false);
		searchBox.add(searchOptBox, ccf.xyw(1, 1, 8));

		add(searchBox, BorderLayout.NORTH);

		// Center contains table model for results
		resultTable = new JTable();
		resultspane = new JScrollPane(resultTable);

		add(resultspane, BorderLayout.CENTER);

		resultTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					JTable target = (JTable) e.getSource();
					int row = target.getSelectedRow();

					try {

						ResultTableModel model = (ResultTableModel) target
								.getModel();
						File tmpDir = new File(plugin.getTmpDir(), SBMLPlugin
								.shortClientName(model.clientName));
						tmpDir.mkdirs();
						plugin.openPathwayWithProgress(
								plugin.getClients().get(model.clientName),
								model.getValueAt(row, 0).toString(), 0, tmpDir);

					} catch (Exception ex) {
						JOptionPane.showMessageDialog(BioModelPanel.this,
								ex.getMessage(), "Error",
								JOptionPane.ERROR_MESSAGE);
						Logger.log.error("Error", ex);
					}
				}
			}
		});
	}

	private void search() throws RemoteException, InterruptedException,
			ExecutionException {
		final String query = pubXref.getText();

		if (!query.isEmpty()) {
			String clientName = clientDropdown.getSelectedItem().toString();
			final BioModelsWSClient client = plugin.getClients()
					.get(clientName);

			final ProgressKeeper pk = new ProgressKeeper();
			final ProgressDialog d = new ProgressDialog(
					JOptionPane.getFrameForComponent(this), "", pk, true, true);
			//final ArrayList<String> results2 = new ArrayList<String>();
			SwingWorker<String[], Void> sw = new SwingWorker<String[], Void>() {
				protected String[] doInBackground() throws Exception {
					pk.setTaskName("Searching Biomodels");
					String[] results = null;
					try {
						// getting the models id by name
						results = client.getModelsIdByName(query);
						

					} catch (Exception e) {
						throw e;
					} finally {
						pk.finished();
					}

					return results;
				}
			};

			sw.execute();
			d.setVisible(true);

			resultTable.setModel(new ResultTableModel(sw.get(), clientName));
			resultTable
					.setRowSorter(new TableRowSorter(resultTable.getModel()));
		} else {
			JOptionPane.showMessageDialog(null, "Please Enter a Search Query",
					"ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}

	private class ResultTableModel extends AbstractTableModel {
		String[] results;
		String[] columnNames = new String[] { "Name" };
		String clientName;

		public ResultTableModel(String[] results, String clientName) {
			this.clientName = clientName;
			this.results = results;

		}

		public int getColumnCount() {
			return 1;
		}

		public int getRowCount() {
			return results.length;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			String r = results[rowIndex];

			return r;
		}

		public String getColumnName(int column) {
			return columnNames[column];
		}
	}

}
