// Author: Yuan Du (yd2234@columbia.edu)
// Minor Author: Akshai Sarma (as4107@columbia.edu)
// Date: Oct 28, 2012
// Function: do part 1 for Project 2. Database classification

import java.util.*;

public class Part1 {
	String cacheDir = "./cache/";
	String accountKey;
	TreeNode tree;
	
	public static void main(String[] args){
	
		String accountKey = "MWQrrA8YW+6ciAUTJh56VHz1vi/Mdqu0lSbzms3N7NY=";
		String site = "yahoo.com";
		double t_es = 0.6;
		int t_ec = 100;
		
		if (args.length == 5) {
			accountKey = args[1];
			t_es = Double.parseDouble(args[2]);
			t_ec = Integer.parseInt(args[3]);
			site = args[4];
		}
		
		System.out.println("\n\nClassifying...");
		Part1 p1 = new Part1(accountKey);
		// results stands for nodes (often only one node) of the category
		// e.g., Diseases(standing for Root/Health/Diseases) for cancer.org
		// For how to recursively get the path, refer to printClassification
		p1.classifyDB(site, t_es, t_ec);
		
		/* 
		 * Output content summaries of the part of the tree that was visited during 
		 * classification. Uses partial tree and does a post order traversal pushing up
		 * results. Knows which nodes were "visited" due to visited flag in each node.
		 */
		System.out.println("\n\nExtracting topic content summaries...");
		Part2 p2 = new Part2(accountKey, p1.tree, site);
		p2.outputContentSummaries();
	}
	
	Part1 (String accountKey) {
		this.accountKey = accountKey;
	}
	
	void createTree() {
		String schemeFile = "../resources/scheme.txt";
		SchemeTree tree = new SchemeTree(schemeFile);
		tree.queryDir = "../resources/";
		tree.addQueryList(tree.root);
		this.tree = tree.root;
	}

	void classifyDB(String site, double t_es, int t_ec){
		// create the scheme tree and add the query list
		createTree();
		
		// get the classification results
		ArrayList<TreeNode> results = getCategoryResults(this.tree, site, t_es, t_ec, 1.0);	

		// print the classification
		printClassification(results);
	}

	// print all the valid categorizations
	void printClassification(ArrayList<TreeNode> results){
		System.out.println("\n\nClassification:");
		for(int i = 0;i<results.size();i++){
			TreeNode node = results.get(i);
			String cate = node.name;
			node = node.parent;
			// go to get the ancestors for this root
			while(node!=null){
				cate = node.name+"/"+cate;
				node = node.parent;
			}
			System.out.println(cate);	
		}
	}

	// recursively visit the nodes from c to its children, and get the valid categories
	ArrayList<TreeNode> getCategoryResults(TreeNode c, String site, double t_es, int t_ec, double c_specificity){
		ArrayList<TreeNode> results = new ArrayList<TreeNode> ();
		// check if this is a leaf node
		if (c.isLeaf){
			results.add(c);
			return results;
		}

		ArrayList<TreeNode> childrenList = c.children;
		int size = childrenList.size();

		// store the coverage and specificity for each child
		int total_ec = 0;
		int [] ec_list = new int [size];

		// compute coverage for each of c's children
		for (int i = 0; i<size; i++){
			TreeNode childNode = childrenList.get(i);
			int ec = getCoverage(site, childNode);
			// put the ec value into ec_list, and count the total_ec
			ec_list[i] = ec;
			total_ec += ec;
		}

		// compute specificity for each of c's children (condition:tota_ec!=0) and recursively do for the children
		if (total_ec > 0){
			for (int i = 0; i<size; i++){
				TreeNode childNode = childrenList.get(i);
				double es = c_specificity*ec_list[i]/total_ec;
				System.out.println("Specificity for category:"+childNode.name+" is "+es);
				System.out.println("Coverage for category:"+childNode.name+" is "+ec_list[i]);
				// check if the specificity and coverage are above threshold
				if (es>=t_es && ec_list[i]>=t_ec){
					// System.out.println("es>=t_ec && ec_list[i]>=t_ec! for node="+childNode.name);
					// get the categorization results for this child, and add it to the results for node c
					childNode.visited = true;
					ArrayList<TreeNode> newResults = getCategoryResults(childNode, site, t_es, t_ec, es);
					results.addAll(newResults);
				}
			}
		}

		// check if results is still empty
		if (results.size()==0){
			c.visited = true;
			results.add(c);
			return results;
		}
		else
			return results;
	}

	// get the coverage of one node in the given site
	int getCoverage(String site, TreeNode node){
		int coverage = 0;

		BingSearch searcher = new BingSearch(accountKey);
		ArrayList<String> queryList = node.queryList;
		// try{
			for (int i = 0; i<queryList.size(); i++){
	            // Thread.sleep(100);
				String query = queryList.get(i);
				int number = searcher.getDocNum(site, query);
				coverage += number;
			}
		// } catch (InterruptedException e) {
		// 	System.err.println("Error: " + e);
		// }
		return coverage;
	}
}
