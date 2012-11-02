
/* A Driver class that just runs the Project parts, which exists as classes
 * Part1 and Part2, each doing what the Project parts correspond to
 */
public class Driver {

	public static void main(String[] args) {
		String accountKey = "MWQrrA8YW+6ciAUTJh56VHz1vi/Mdqu0lSbzms3N7NY=";
		String site = "yahoo.com";
		double t_es = 0.6;
		int t_ec = 100;

		if (args.length >= 4) {
			accountKey = args[0];
			t_es = Double.parseDouble(args[1]);
			t_ec = Integer.parseInt(args[2]);
			site = args[3];
		}

		System.out.println("\n\nClassifying...");
		Part1 p1 = new Part1(accountKey);
		/* This creates the tree with its information and classifies the site.
		 * It then prints the classification also.
		 */
		p1.classifyDB(site, t_es, t_ec);


		/* Output content summaries of the part of the tree that was visited during
		 * classification. Uses partial tree and does a post order traversal pushing up
		 * results. Knows which nodes were "visited" due to visited flag in each node.
		 */
		System.out.println("\n\nExtracting topic content summaries...");
		Part2 p2 = new Part2(accountKey, p1.tree, site);
		p2.outputContentSummaries();
	}

}
