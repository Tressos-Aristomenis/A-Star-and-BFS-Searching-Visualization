package algorithms;

import entity.Node;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import parser.DataParser;

import java.util.*;


public class PathFinder {
	private static List<Node> nodes;		// stores all the nodes.
	private static Graph graph;
	private static DataParser parser;		// parser used to parse the file with the graph data.
	private long DELAY = 1500;				// sets visualization delay

    public List<Node> initializeGraph() {
		setupNodes();
		
		// set up graph and its properties.
		graph = new SingleGraph("PathFinder");

		graph.setAutoCreate(true);
		graph.setStrict(false);
		graph.display();
		
		graph.addAttribute("ui.quality");
		graph.addAttribute("ui.antialias");
		graph.addAttribute("ui.stylesheet", "url('resources/style.css')");
		
		// set up graph nodes
		setupGraphNodes();

		return nodes;
    }
	
	private static void setupNodes() {
		parser = new DataParser();		// start the parsing process.

		nodes = parser.getNodes();		// retrieve the node list.
	}
	
    public void runAStar(String nodeName) {
    	clearNodeColors();						// reset all node colors.
		Node startNode = getNode(nodeName);		// get selected node
		resetNode(startNode);					// reset source field, if exists (avoid errors)
		AStarSearch(startNode, getGoalNode());	// goal = Bucharest.
	}

	public void runBreadthFirst(String nodeName) {
    	clearNodeColors();								// reset all node colors.
		Node startNode = getNode(nodeName);				// get selected node
		resetNode(startNode);							// reset source field, if exists (avoid errors)
		BreadthFirstSearch(startNode, getGoalNode()); 	// goal = Bucharest.
	}

	private void setupGraphNodes() {
		for (Node node : nodes) {
    		// create node if it doesn't already exist on the graph.
			if (graph.getNode(node.getName()) == null) {
				// add node to the graph.
				org.graphstream.graph.Node graphNode = graph.addNode(node.getName());
				graphNode.addAttribute("ui.label", node.getName() + "	[" + node.getHeuristic() + "]");
				
				sleep(DELAY);		// add a little delay.
			}
			
			// iterate through neighbor map.
			for (Map.Entry<Node, Integer> entry : node.getNeighbors().entrySet()) {
				Node neighbor = entry.getKey();
				Integer cost = entry.getValue();

				// create neighbor and its edge if it doesn't already exist.
				if (graph.getNode(neighbor.getName()) == null) {
					org.graphstream.graph.Node graphNeighbor = graph.addNode(neighbor.getName());
					graphNeighbor.addAttribute("ui.label", neighbor.getName() + "	[" + neighbor.getHeuristic() + "]");

					// create edge id.
					StringBuilder edgeName = new StringBuilder();
					edgeName.append(node.getName());
					edgeName.append(neighbor.getName());
					
					// add edge to the graph.
					Edge edge = graph.addEdge(edgeName.toString(), node.getName(), neighbor.getName());
					edge.addAttribute("ui.label", cost.toString());
					
					sleep(DELAY);		// add a little delay.
				}
				else {
					// create edge between current node and its neighbor if not found.
					if (graph.getNode(node.getName()).getEdgeBetween(neighbor.getName()) == null) {
						// create edge id.
						StringBuilder edgeName = new StringBuilder();
						edgeName.append(node.getName());
						edgeName.append(neighbor.getName());
						
						// add edge to the graph.
						Edge edge = graph.addEdge(edgeName.toString(), node.getName(), neighbor.getName());
						edge.addAttribute("ui.label", cost.toString());
						
						sleep(DELAY);		// add a little delay.
					}
				}
			}
		}
	}
	
		//					ALGORITHMS					\\
	private void AStarSearch(Node start, Node goal) {
		int NODES_EXPANDED = 0;
		Map <Node, Integer> nodeQueue = new HashMap<>();		// works as a priority queue. First index: node with minimum total score.
		List<Node> expanded = new ArrayList<>();				// keeps track of the nodes which are expanded.

		int startScore = start.getHeuristic() + 0;				// initialize start node total score.
		nodeQueue.put(start, startScore);
		Node current = start;									// current keeps track of the node that is currently being processed.

		graph.getNode(current.getName()).addAttribute("ui.class", "start");		// sets green color to start node.
		sleep(DELAY);																		// add a little delay.
		
		// while current node is not the goal node.
		while (!current.getName().equals(goal.getName())) {
			// iterate through the neighbors of the current node.
			for (Node neighbor : current.getNeighbors().keySet()) {
				// if this neighbor is already expanded, ignore it.
				if (expanded.contains(neighbor)) {
					continue;
				}
				
				// color neighbor node red, unless it is the start node which should remain green.
				if (!start.getName().equals(neighbor.getName())) {
					graph.getNode(neighbor.getName()).addAttribute("ui.class", "neighbor");
					sleep(DELAY);		// add a little delay.
				}
				
				// if the queue contains the neighbor.
				if (nodeQueue.containsKey(neighbor)) {
					// find the total score of the neighbor from the current node.
					int tempNeighborScore = nodeQueue.get(current)
											- current.getHeuristic()
											+ current.getNeighbors().get(neighbor)
											+ neighbor.getHeuristic();

					int existedNodeScore = nodeQueue.get(neighbor);		// get neighbor's total score from the queue.

					if (tempNeighborScore < existedNodeScore) {			// if the new distance is less than the one in the queue: 
						neighbor.setSource(current);					// neighbor's new source = current.
						nodeQueue.put(neighbor, tempNeighborScore);		// and update neighbor's total score in the queue.
					}
				} else {								// the map-queue does not contain the neighbor node.
					neighbor.setSource(current);		// neighbor's source = current.
					// calculate neighbor's total score.
					int neighborScore = nodeQueue.get(neighbor.getSource())
										- neighbor.getSource().getHeuristic()
										+ neighbor.getSource().getNeighbors().get(neighbor)
										+ neighbor.getHeuristic();
					
					// add the neighbor with its score to the queue-map.
					nodeQueue.put(neighbor, neighborScore);
				}
			}

			// clear neighbor colors, unless it's the start node which remains green.
			// OR if it is already visited, which remains grey.
			for (Node node : current.getNeighbors().keySet()) {
				if (!node.getName().equals(start.getName()) && !graph.getNode(node.getName()).getAttribute("ui.class").equals("visited")) {
					graph.getNode(node.getName()).removeAttribute("ui.class");
				}
			}
			
			NODES_EXPANDED++;
			expanded.add(current);			// current has been expanded, so add him to the expanded list.
			nodeQueue.remove(current);		// we remove from the queue the current node. (which had the minimum total score)
			
			// draw the expanded nodes grey, unless it's the start node which remains green.
			if (!start.getName().equals(current.getName())) {
				graph.getNode(current.getName()).addAttribute("ui.class", "visited");
			}

			current = getMinValue(nodeQueue);		// get the node with the minimum total score and make it the current one.
			
			// color the new current, unless it's the start node which remains green.
			if (!start.getName().equals(current.getName())) {
				graph.getNode(current.getName()).addAttribute("ui.class", "current");
				sleep(DELAY);		// add a little delay.
			}
		}
		
		// when goal found, draw it orange.
		graph.getNode(current.getName()).addAttribute("ui.class", "goal");
		sleep(DELAY);		// add a little delay.

		Node tracker = current;			// used to trace the path back.
		
		System.out.println("NODES EXPANDED DURING A STAR ALGORITHM: " + NODES_EXPANDED);
		System.out.print("Path: " + goal.getName());
		
		while (tracker.getSource() != null) {
			System.out.print(" <-- " + tracker.getSource().getName());
			tracker = tracker.getSource();
			graph.getNode(tracker.getName()).addAttribute("ui.class", "start");
			sleep(DELAY);
		}

		System.out.println();
	}

	private void BreadthFirstSearch(Node start, Node goal) {
		// some comments are mutual with AStarSearch algorithm so I won't repeat myself.
		
		int NODES_EXPANDED = 0;
		Queue<Node> nodeQueue = new LinkedList<>();		// queue that strores nodes to be expanded.
        List<Node> expanded = new ArrayList<>();

        Node current = start;
        graph.getNode(current.getName()).addAttribute("ui.class", "start");
        sleep(DELAY);

        while (!current.getName().equals(goal.getName())) {
            for (Node neighbor : current.getNeighbors().keySet()) {
				// if the neighbor is already expanded OR the neighbor is already in the queue to be expanded in the future, ignore the neighbor.
                if (expanded.contains(neighbor) || nodeQueue.contains(neighbor)) {
                    continue;
                }

                neighbor.setSource(current);
                nodeQueue.add(neighbor);

                graph.getNode(neighbor.getName()).addAttribute("ui.class", "neighbor");
                sleep(DELAY);
            }
			
			NODES_EXPANDED++;
            expanded.add(current);
            current = nodeQueue.remove();

            graph.getNode(current.getName()).addAttribute("ui.class", "current");
            sleep(DELAY);
        }

        graph.getNode(current.getName()).addAttribute("ui.class", "goal");
        sleep(DELAY);

        Node tracker = current;
		
		System.out.println("NODES EXPANDED DURING BFS ALGORITHM: " + NODES_EXPANDED);
        System.out.print("Path: " + goal.getName());

        while (tracker.getSource() != null) {
            System.out.print(" <-- " + tracker.getSource().getName());
            tracker = tracker.getSource();

            graph.getNode(tracker.getName()).addAttribute("ui.class", "start");
            sleep(DELAY);
        }

        System.out.println();
	}
		//												\\
	
	private Node getMinValue(Map<Node, Integer> treeMap) {
		Node minNode = null;
		int min = Integer.MAX_VALUE;

		for (Map.Entry<Node,Integer> entry : treeMap.entrySet()) {
			Node node = entry.getKey();
			Integer value = entry.getValue();

			if (value < min) {
				min = value;
				minNode = node;
			}
		}
		return minNode;
	}
	
	private Node getGoalNode() {
		for (Node current : nodes) {
			if (current.getHeuristic() == 0) {
				return current;
			}
		}
		
		return null;
	}
	
	// get specific node from list given its name
	private Node getNode(String nodeName) {
		for (Node node : nodes) {
			if (node.getName().equals(nodeName)) {
				return node;
			}
		}
		return null;
	}
	
	private void clearNodeColors() {
		for (org.graphstream.graph.Node node : graph.getNodeSet()) {
			node.removeAttribute("ui.class");
		}
	}

	// set nodes's source field null
	private void resetNode(Node startNode) {
		if (startNode.getSource() != null) {
			startNode.setSource(null);
		}
	}
	
	private void sleep(long ms) {
		try { Thread.sleep(ms); } catch (Exception e) {e.printStackTrace();}
	}

	public void setVisualizationDelay(int value) {
		this.DELAY = value;
	}

	// change graph font size
	public void setGraphFontSize(int fontSize) {
		for (org.graphstream.graph.Node node : graph.getNodeSet()) {
			node.addAttribute("ui.style", "size: " + fontSize + "px;");
			node.addAttribute("ui.style", "text-size: " + (fontSize - 5) + "px;");
		}

		for (Edge edge : graph.getEdgeSet()) {
			edge.addAttribute("ui.style", "text-size: " + (fontSize - 5) + "px;");
		}
	}
	
	public void setFilePath() {
		
	}
}