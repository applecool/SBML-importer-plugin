// PathSBML Plugin
// SBML Plugin for PathVisio.
// Copyright 2013 developed for Google Summer of Code
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
import javax.swing.JButton;
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

import uk.ac.ebi.biomodels.ws.BioModelsWSClient;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import org.pathvisio.sbml.SBMLPlugin;
/**
 * This class creates the search panel for searching bio models and
 * content in the dialog of the Search.
 * This class enables us to search the bio models by various terms like
 * bio model name,publication title/id,person/encoder name,uniprot id,
 * go id and taxonomy id.
 * @author applecool
 */
public class BioModelPanel extends JPanel {
	
	SBMLPlugin plugin;
	
	public static Border etch = BorderFactory.createEtchedBorder();	
	private JComboBox clientDropdown;
	private JTable resultTable;
	private JScrollPane resultspane;
	
	private JTextField bioModelName;
	private JTextField pubTitId;
	private JTextField chebiId;
	private JTextField personName;	
	private JTextField uniprotId;
	private JTextField goId;
	private JTextField taxonomyId;
	private JButton search;
	
	public BioModelPanel(final SBMLPlugin plugin) {

		this.plugin = plugin;
		setLayout(new BorderLayout());
		
		// biomodel search terms
		bioModelName = new JTextField();
		chebiId = new JTextField();
		uniprotId = new JTextField();
		pubTitId = new JTextField();
		personName = new JTextField();
		goId = new JTextField();
		taxonomyId = new JTextField();
		
		//tooltips for all the search boxes
		bioModelName.setToolTipText("Tip:Use Biomodel name (e.g.:'Tyson1991 - Cell Cycle 6 var')");
		pubTitId.setToolTipText("Tip:Use publication name(e.g.:'sbml')");
		chebiId.setToolTipText("Tip:Use Chebi id (e.g.:'24996')");
		personName.setToolTipText("Tip:Use person/encoder name (e.g.:'Rainer','Nicolas')");
		uniprotId.setToolTipText("Tip:Use Uniprot id (e.g.:'P04637','P10113')");
		goId.setToolTipText("Tip:Use GO id (e.g.:'0006915')");
		taxonomyId.setToolTipText("Tip:Use Taxonomy id (e.g.:'9606')");
		
		//search biomodel action 
		Action searchBioModelAction = new AbstractAction("searchBioModels") {
			public void actionPerformed(ActionEvent e) {
				try {
					resultspane.setBorder(BorderFactory.createTitledBorder(etch, "BioModels"));
					search();
				} catch (Exception ex) {
					JOptionPane
							.showMessageDialog(BioModelPanel.this,
									ex.getMessage(), "Error",
									JOptionPane.ERROR_MESSAGE);
					Logger.log.error("Error while searching for Biomodels", ex);
				}
			}

		};

	
		//layout for the search box in the biomodel panel.
		JPanel searchBox = new JPanel();
		FormLayout layoutf = new FormLayout(
				"p,3dlu,120px,2dlu,30px,fill:pref:grow,3dlu,fill:pref:grow,3dlu",
				"pref, pref, 14dlu, pref, 4dlu, pref,pref,pref");
		CellConstraints ccf = new CellConstraints();

		searchBox.setLayout(layoutf);
		searchBox.setBorder(BorderFactory.createTitledBorder(etch));

		JPanel searchOptBox = new JPanel();
		FormLayout layout = new FormLayout(
				"3dlu,p,3dlu,2dlu,30px,fill:pref:grow,2dlu",
				"pref, pref, 14dlu, 14dlu, 14dlu, pref,pref,pref,pref,pref");
		CellConstraints cc = new CellConstraints();

		searchOptBox.setLayout(layout);
		searchOptBox.setBorder(BorderFactory.createTitledBorder(etch,"Search options"));
		
		//labels for all the search boxes.
		searchOptBox.add(new JLabel("Biomodel Name:"), cc.xy(2, 1));					
		searchOptBox.add(new JLabel("Publication Title/ID:"), cc.xy(2, 3));
		searchOptBox.add(new JLabel("Chebi ID:"),cc.xy(2, 4));
		searchOptBox.add(new JLabel("Person Name:"),cc.xy(2, 5));
		searchOptBox.add(new JLabel("Uniprot ID:"),cc.xy(2,6));
		searchOptBox.add(new JLabel("GO ID:"),cc.xy(2,7));
		searchOptBox.add(new JLabel("Taxonomy ID:"),cc.xy(2,8));
		
		search= new JButton("search");
		searchOptBox.add(bioModelName, cc.xyw(4, 1, 3));
		searchOptBox.add(pubTitId,cc.xyw(4, 3,3));
		searchOptBox.add(chebiId,cc.xyw(4, 4, 3));
		searchOptBox.add(personName,cc.xyw(4, 5, 3));
		searchOptBox.add(uniprotId,cc.xyw(4, 6,3));
		searchOptBox.add(goId,cc.xyw(4, 7,3));
		searchOptBox.add(taxonomyId,cc.xyw(4, 8,3));
		searchOptBox.add(search,cc.xyw(4,9,3));

		search.addActionListener(searchBioModelAction);
		Vector<String> clients = new Vector<String>(plugin.getClients().keySet());
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
		
		//this enables us to import(open) the bio-models with two mouse clicks 
		//on the results which appear in the result table.
		resultTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				//double click
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
	
	/**
	 * Search method for - searching bio-models by name,publication title/id,
	 * person/encoder name,uniprot id,go id and taxonomy id.
	 * @throws RemoteException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void search() throws RemoteException, InterruptedException,
			ExecutionException {
		
		// search terms typed in the search boxes are trimmed and stored in the following respective variables.
		final String sbmlName = bioModelName.getText().trim();
		final String sbmlPub = pubTitId.getText().trim();
		final String sbmlChebi = chebiId.getText().trim();
		final String sbmlPerson = personName.getText().trim();
		final String sbmlUniprot = uniprotId.getText().trim();
		final String sbmlGo = goId.getText().trim();
		final String sbmlTaxonomy = taxonomyId.getText().trim();
		
		if (!(sbmlPub.isEmpty()&&sbmlName.isEmpty()&&sbmlChebi.isEmpty()&&sbmlPerson.isEmpty()&&sbmlUniprot.isEmpty()&&sbmlGo.isEmpty()&&sbmlTaxonomy.isEmpty())) {
			String clientName = clientDropdown.getSelectedItem().toString();
			final BioModelsWSClient client = plugin.getClients().get(clientName);

			final ProgressKeeper pk = new ProgressKeeper();
			final ProgressDialog d = new ProgressDialog(
					JOptionPane.getFrameForComponent(this), "", pk, true, true);
			final ArrayList<String> results = new ArrayList<String>();
			
			SwingWorker<String[], Void> sw = new SwingWorker<String[], Void>() {
				
				protected String[] doInBackground() throws Exception {
					pk.setTaskName("Searching Biomodels");
					String[] results1 = null;
					String[] results2 = null;
					String[] results3 = null;
					String[] results4 = null;
					String[] results5 = null;
					String[] results6 = null;
					String[] results7 = null;
					try {
						// getting the models id by name.
						if(!bioModelName.getText().equalsIgnoreCase(""))
						{
						results1 = client.getModelsIdByName(sbmlName);
						
						if(results1!=null){
						 for (int i = 0; i < results1.length; i++) {
								
								results.add(results1[i]);
							}
						}
						}
						
						//getting the models id by publication title or id.
						if(!pubTitId.getText().equalsIgnoreCase(""))
						{
						results2= client.getModelsIdByPublication(sbmlPub);
						if(results2!=null){ 
						for (int i = 0; i < results2.length; i++) {
								
								results.add(results2[i]);
							}
						}
						}
						
						//getting models id by chebi id.
						if(!chebiId.getText().equalsIgnoreCase(""))
						{
						results3= client.getModelsIdByChEBIId(sbmlChebi);
						if(results3!=null){
						for (int i = 0; i < results3.length; i++) {
								
								results.add(results3[i]);
							}
						}
						}
						
						//getting models id by person or encoder or author name.
						if(!personName.getText().equalsIgnoreCase(""))
						{
						results4= client.getModelsIdByPerson(sbmlPerson);
						if(results4!=null){ 
						for (int i = 0; i < results4.length; i++) {
								
								results.add(results4[i]);
							}
						}
						}
						
						//getting models id by uniprot id.
						if(!uniprotId.getText().equalsIgnoreCase(""))
						{
						results5= client.getModelsIdByUniprot(sbmlUniprot);
						if(results5!=null){ 
						for (int i = 0; i < results5.length; i++) {
								
								results.add(results5[i]);
							}
						}
						}
						
						//getting models id by go id.
						if(!goId.getText().equalsIgnoreCase(""))
						{
						results6 = client.getModelsIdByGOId(sbmlGo);
						
						if(results6!=null){
						 for (int i = 0; i < results6.length; i++) {
								
								results.add(results6[i]);
							}
						}
						}
						
						//getting models id by taxonomy id.
						if(!taxonomyId.getText().equalsIgnoreCase(""))
						{
						results7 = client.getModelsIdByTaxonomyId(sbmlTaxonomy);
						
						if(results7!=null){
						 for (int i = 0; i < results7.length; i++) {
								
								results.add(results7[i]);
							}
						}
						}

					} catch (Exception e) {
						throw e;
					} finally {
						pk.finished();
					}
					
					
					 String[] finalresults = new String[results.size()];
					 
						results.toArray(finalresults);
						
					return finalresults;
					
					
				}
				  
				 protected void done() {
			           if(!pk.isCancelled())
			             {
			              if(results.size()==0)
			              {
			                 JOptionPane.showMessageDialog(null,"0 results found");
			              }
			             }
			             else if(pk.isCancelled())
			             {
			               pk.finished();
			             }
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
	
	/**
	 * This class creates the result table model
	 * based on the results.
	 * @author applecool
	 *
	 */
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
