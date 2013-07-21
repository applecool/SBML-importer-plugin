package org.pathvisio.sbml;

import java.util.HashMap;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.NamedSBase;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.Species;

public class NavigationTree {
	public static final String COMPARTMENTS = "Compartments";
	public static final String SPECIES = "Species";
	public static final String REACTIONS = "Reactions";
	private Map<String, NamedSBase> objectMap;
	private Map<String, TreePath> objectPathMap;
	private DefaultTreeModel treeModel;

	public Map<String, NamedSBase> getObjectMap() {
		return this.objectMap;
	}

	public Map<String, TreePath> getObjectPathMap() {
		return this.objectPathMap;
	}

	public DefaultTreeModel getTreeModel() {
		return this.treeModel;
	}

	public NavigationTree() {
		this.objectMap = new HashMap();
		this.objectPathMap = new HashMap();
		
		this.treeModel = new DefaultTreeModel(
				new DefaultMutableTreeNode("sbml"));
	}

	public NavigationTree(SBMLDocument document) {
		this();

		Model model = document.getModel();
		String modelName = getModelNameFromModel(model);
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(modelName);
		this.treeModel = new DefaultTreeModel(top);
		addListOfCompartmentsToTreeModel(top, model.getListOfCompartments());
		addListOfSpeciesToTreeModel(top, model.getListOfSpecies());
		addListOfReactionsToTreeModel(top, model.getListOfReactions());
		
	}

	public NamedSBase getNamedSBaseById(String id) {
		NamedSBase nsb = null;
		if (this.objectMap.containsKey(id)) {
			return (NamedSBase) this.objectMap.get(id);
		}
		return nsb;
	}

	private void addListOfCompartmentsToTreeModel(DefaultMutableTreeNode top,
			ListOf<Compartment> compartmentList) {
		addListOfNamedSBaseToTreeModel(top,
				createTreeNodeForName("Compartments"), compartmentList);
	}

	private void addListOfSpeciesToTreeModel(DefaultMutableTreeNode top,
			ListOf<Species> speciesList) {
		addListOfNamedSBaseToTreeModel(top, createTreeNodeForName("Species"),
				speciesList);
	}

	private void addListOfReactionsToTreeModel(DefaultMutableTreeNode top,
			ListOf<Reaction> reactionList) {
		addListOfNamedSBaseToTreeModel(top, createTreeNodeForName("Reactions"),
				reactionList);
	}


	private void addListOfNamedSBaseToTreeModel(DefaultMutableTreeNode top,
			DefaultMutableTreeNode category,
			ListOf<? extends NamedSBase> namedSBaseList) {
		if (namedSBaseList.size() > 0) {
			top.add(category);
			for (NamedSBase namedSBase : namedSBaseList) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(
						namedSBase, false);
				category.add(node);
				String id = namedSBase.getId();
				TreePath path = new TreePath(node.getPath());
				this.objectMap.put(id, namedSBase);
				this.objectPathMap.put(id, path);
			}
		}
	}

	private DefaultMutableTreeNode createTreeNodeForName(String name) {
		return new DefaultMutableTreeNode(name, true);
	}

	private String getModelNameFromModel(Model model) {
		String name = model.getId();
		if (name.equals("")) {
			name = model.getName();
		}
		return name;
	}
}