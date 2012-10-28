// Author: Yuan Du (yd2234@columbia.edu)
// Date: Oct 27, 2012
// Function: build the categorization scheme tree and add the queries for each node

import java.io.*;
import java.util.*;

public class SchemeTree {
	TreeNode root;
	HashMap<String, TreeNode> nameMapNode;
	String queryDir;
	public static void main(String[] args){
		String schemeFile = "../resources/scheme.txt";
		SchemeTree tree = new SchemeTree(schemeFile);
		tree.queryDir = "../resources/";

		// System.out.println(tree.root.children.get(2).isLeaf);
		tree.addQueryList(tree.root);

		// print all the queries for soccer
		ArrayList<String> queryList = tree.nameMapNode.get("Soccer").queryList;
		for(int i = 0;i<queryList.size();i++)
			System.out.println(queryList.get(i));
	}

	// create the scheme tree by the file
	SchemeTree(String schemeFile){
		// map from name to the node, e.g., "Computers" -> the node
		nameMapNode = new HashMap<String, TreeNode>();

		// From the scheme file:
		// # This is the categorization scheme. All the sentences after '#' are ignored.
		// # The first line starting with '{' contains all the nodes (must be in {a,b,...,n}).
		// # Other lines are in the format of "<parent>:[<child1>,<child2>,...,<childn>]"
		String line;
		try {
			BufferedReader br = new BufferedReader(new FileReader(schemeFile));
			while ((line = br.readLine()) != null) {
				// ignore empty lines and comments
				if (line.trim().length()<=0 || line.startsWith("#")){
					continue;
				}
				// line with all the nodes: {a,b,...,n}, the first is root
				if (line.startsWith("{")){
					if (line.endsWith("}")){
						// get node-name list
						String[] nameList = line.substring(1,line.length()-1).split(",");
						for (int i = 0;i<nameList.length;i++){
							// System.out.println(nameList[i]);
							String name = nameList[i];
							TreeNode node = new TreeNode(name);
							nameMapNode.put(name, node);
							// set the root
							if (i==0)
								root = node;
						}
					}
				}
				else{
					// not empty/comments; not all node list
					// <parent>:[<child1>,<child2>,...,<childn>]
					int index = line.indexOf(":");
					if (index>=0){
						String parentName = line.substring(0, index);
						TreeNode parentNode = nameMapNode.get(parentName);
						// System.out.println(parentName);
						String[] childrenList = line.substring(index+2, line.length()-1).split(",");
							for (int i = 0;i<childrenList.length;i++){
							// System.out.println(childrenList[i]);
							String name = childrenList[i];
							TreeNode childNode = nameMapNode.get(name);
							if (parentNode!=null && childNode!=null){
								// set the parent for child; and add the child to parent children-list
								childNode.parent = parentNode;
								parentNode.addChild(childNode);
							}
						// set the parent not leaf
						if (parentNode!=null && parentNode.children.size()>0)
							parentNode.isLeaf = false;
						}
					}

				}
				// System.out.println(line);
			}
		}
		catch (IOException e) {
			System.err.println("Error: " + e);
		}
	}

	// add the queries
	void addQueryList(TreeNode node){
		// no need to add querylist if this is leaf
		if (node!=null && !node.isLeaf){
			// the query file is "root.txt" for Root
			String queryFile = queryDir+node.name.toLowerCase() + ".txt";
			String line;
			try {
				BufferedReader br = new BufferedReader(new FileReader(queryFile));
				while ((line = br.readLine()) != null) {
					// format: 
					// Computers cpu
					int index = line.indexOf(" ");
					String category = line.substring(0,index);
					String query = line.substring(index+1);
					// System.out.println(category+"::"+query);
					// add the query to category
					TreeNode cateNode = nameMapNode.get(category);
					if (cateNode!=null){
						cateNode.addQuery(query);
					}
				}
			}
			catch (IOException e) {
				System.err.println("Error: " + e);
			}

			// add query list for the children of current node
			ArrayList<TreeNode> childrenList = node.children;
			for (int i = 0;i<childrenList.size();i++){
				TreeNode childNode = childrenList.get(i);
				addQueryList(childNode);
			}
		}
	}
}

// node in this scheme tree
class TreeNode{
	String name; // category name for this node,e.g., Computers
	TreeNode parent = null;
	ArrayList<TreeNode> children;
	ArrayList<String> queryList;
	boolean isLeaf = true; // default is leaf, unless it has >=1 children
	// Please put other variables here

	TreeNode(String name){
		this.name = name;
		// initialization
		children = new ArrayList<TreeNode>();
		queryList = new ArrayList<String>();
	}

	void setParent(TreeNode parent){
		this.parent = parent;
	}

	void addChild(TreeNode child){
		this.children.add(child);
	}

	void addQuery(String query){
		this.queryList.add(query);
	}
}
