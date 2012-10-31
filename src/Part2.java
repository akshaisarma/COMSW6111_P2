import java.io.*;
import java.util.*;

/*
 *  Author: Akshai Sarma (as4107@columbia.edu)
 *  Implements Part 2 of Project 2. This class 
 *  will be used in Part 1.
 */

public class Part2 {
	TreeNode visitedTree;
	String site;
	HashSet<String> allURLS;
	BingSearch searcher;
	
	public Part2(String key, TreeNode tree, String site) {
		this.visitedTree = tree;
		this.site = site;
		this.allURLS = new HashSet<String>();
		this.searcher = new BingSearch(key);
	}
	
	public void outputContentSummaries() {
		postOrder(visitedTree);
	}
	
	private void addNodeInformation(TreeNode node, NodeInformation soFar) {
		System.out.println("Creating Content Summary for:" + node.name);
		int count = 0, len = soFar.queryResults.size() + node.queryList.size();
	
		/* 
		 * This loop simply prints all the visited descendants' Getting Page lines
		 * but doesn't actually retrieve anything (to match the reference 
		 * implementation's behavior).
		 */
		for (QueryResult qr : soFar.queryResults) {
			System.out.println(++count + "/" + len);
			for (String u : qr.urls)
				System.out.println("\n\nGetting page:" + u);
		}
		
		for (String query : node.queryList) {
			System.out.println(++count + "/" + len);
			HashSet<String> res = searcher.getTopFour(site, query);
			res.removeAll(allURLS);
			allURLS.addAll(res);
			QueryResult newResult = new QueryResult(query, res);
			soFar.queryResults.add(newResult);
			for (Iterator<String> it = res.iterator(); it.hasNext(); ) {
				String url = it.next();
				System.out.println ("\n\nGetting page:" + url);
				soFar.nodeSummary.mergeSummary(getWordsLynx.runLynx(url));
			}
		}		
	}
	
	private NodeInformation postOrder(TreeNode node) {
		NodeInformation result = new NodeInformation();
		if (!node.visited)
			return result;
		
		for (TreeNode child : node.children)
			result.mergeNode(postOrder(child));

		addNodeInformation(node, result);
		result.writeOut("sample-" + node.name + "-" + site + ".txt");
		return result;
	}
	
}

class QueryResult {
	String query;
	HashSet<String> urls;
	
	public QueryResult (String q, HashSet<String> u) {
		query = q;
		urls = u;
	}
	
	public boolean equals (Object o) {
		if (o == null)
			return false;
		if (o == this)
			return true;
		if (!(o instanceof QueryResult))
			return false;
		QueryResult ob = (QueryResult) o;
		if (query.equals(ob.query))
			return true;
		else 
			return false;
	}
}

class NodeInformation {
	ArrayList<QueryResult> queryResults;
	NodeSummary nodeSummary;
	
	public NodeInformation() {
		queryResults = new ArrayList<QueryResult>();
		nodeSummary = new NodeSummary();
	}
	
	public void mergeURLs(ArrayList<QueryResult> other) {
		queryResults.addAll(other);
	}
	
	public void mergeNode(NodeInformation other) {
		mergeURLs(other.queryResults);
		nodeSummary.mergeSummary(other.nodeSummary.summary);
	}
	
	public void writeOut(String fileName) {
		HashMap<String, Integer> summary = nodeSummary.summary;
		try {
			FileWriter f = new FileWriter(fileName);
			BufferedWriter o = new BufferedWriter(f);
			for (String s : summary.keySet())
				o.write(s + "#" + summary.get(s) + "\n");
			o.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
				
	}
}

class NodeSummary {
	HashMap<String, Integer> summary;
	
	public NodeSummary() {
		summary = new HashMap<String, Integer>();
	}
	
	public void mergeSummary(HashMap<String, Integer> otherTable) {
		for (String s : otherTable.keySet()) {
			Integer freq = otherTable.get(s);
			Integer v = summary.get(s);
			summary.put(s, v == null ? freq : v + freq);
		}
	}
}
