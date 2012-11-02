/*
 *  Author: Akshai Sarma (as4107@columbia.edu)
 *  Implements Part 2 of Project 2.
 */
import java.util.*;

/* Class that walks the tree and generates the content summaries */
public class Part2 {
	TreeNode visitedTree; // Is the entire tree but only interested in visited nodes
	String site;
	BingSearch searcher;

	public Part2(String key, TreeNode tree, String site) {
		this.visitedTree = tree;
		this.site = site;
		this.searcher = new BingSearch(key);
	}

	/* Outside interface to class */
	public void outputContentSummaries() {
		postOrder(visitedTree);
	}

	/* Simple function to do urls set difference allURLs */
	private void removeDuplicates (ArrayList<String> urls, HashSet<String> allURLs) {
		for (Iterator<String> it = urls.iterator(); it.hasNext(); ) {
			String url = it.next();
			if (allURLs.contains(url))
				it.remove();
		}
	}

	/* Core of the class. For each node, using the accumulated information soFar
	 * in the post order traversal of the tree, generates the content summary for
	 * the node. Only has to look at the immediate children as by post order
	 * definition, all the values below it should have been percolated up
	 */
	private void generateContentSummary(TreeNode node, NodeInformation soFar) {
		System.out.println("\nCreating Content Summary for:" + node.name);
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
		/* Get the accumulated samples from all visited levels below at immediate children
		 * and make queries, making sure to remove duplicates and accumulate the information
		 * in soFar so that this node's ancestors know what to print. For each url, the
		 * the document frequency is obtained and merged together for the URLs of the queries
		 */
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
		/* Finally, store the completed allURLs (with queries at this node) and merge all
		 * the content summaries of children into this node's summary.
	 	 */
		node.samples.addAll(allURLs);
		node.mergeChildSummaries();
	}

	/* Performs a post order traversal of the visited part of the tree
	 * percolating printed URL information upward (NodeInformation). By the
	 * nature of content summaries of parents including their children's summaries,
	 * this is the natural way of approaching it.
	 */
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

/* Wrapper for storing a query and its set urls. */
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
