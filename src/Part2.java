/*
 *  Author: Akshai Sarma (as4107@columbia.edu)
 *  Implements Part 2 of Project 2. 
 */
import java.util.*;

public class Part2 {
	TreeNode visitedTree;
	String site;
	BingSearch searcher;
	
	public Part2(String key, TreeNode tree, String site) {
		this.visitedTree = tree;
		this.site = site;
		this.searcher = new BingSearch(key);
	}
	
	public void outputContentSummaries() {
		postOrder(visitedTree);
	}
	
	private void removeDuplicates (ArrayList<String> urls, HashSet<String> allURLs) {
		for (Iterator<String> it = urls.iterator(); it.hasNext(); ) {
			String url = it.next();
			if (allURLs.contains(url))
				it.remove();
		}
	}
	
	private void generateContentSummary(TreeNode node, NodeInformation soFar) {
		System.out.println("Creating Content Summary for:" + node.name);
		ArrayList<String> queryList = node.getQueries();
		int count = 0, len = soFar.queryResults.size() + queryList.size();

		/* 
		 * This loop simply prints all the visited descendants' Getting Page lines
		 * but doesn't actually retrieve anything (to match the reference 
		 * implementation's behavior).
		 */
		for (QueryResult qr : soFar.queryResults) {
			System.out.println(++count + "/" + len);;
			for (String u : qr.urls)
				System.out.println("\n\nGetting page:" + u);
		}
		
		HashSet<String> allURLs = node.getChildSamples();
		
		for (String query : queryList) {
			System.out.println(++count + "/" + len);
			ArrayList<String> res = searcher.getTopFour(site, query);
			removeDuplicates(res, allURLs);
			allURLs.addAll(res);
			QueryResult newResult = new QueryResult(query, res);
			soFar.queryResults.add(newResult);
			for (String url : res) {
				System.out.println("\n\nGetting page:" + url);
				node.summary.addWords(GetWordsLynx.runLynx(url));
			}
		}
		node.samples.addAll(allURLs);
		node.mergeChildSummaries();
	}
	
	private NodeInformation postOrder(TreeNode node) {
		NodeInformation result = new NodeInformation();
		/* If "unvisited" or is a leaf, return as no need to construct summary */
		if (!node.visited || node.isLeaf)
			return result;
		
		for (TreeNode child : node.children)
			result.mergeNode(postOrder(child));

		generateContentSummary(node, result);
		node.summary.writeOut(node.name + "-" + site + ".txt");
		return result;
	}
	
}

/* Helper class for propagating queries made at children upward in order to not walk tree
 * over and over.
 */
class NodeInformation {
	ArrayList<QueryResult> queryResults;
	
	public NodeInformation() {
		queryResults = new ArrayList<QueryResult>();
	}
	
	public void mergeNode(NodeInformation other) {
		queryResults.addAll(other.queryResults);
	}
}

class QueryResult {
	String query;
	ArrayList<String> urls;
	
	public QueryResult (String q, ArrayList<String> u) {
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
