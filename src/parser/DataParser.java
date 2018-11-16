package parser;


import entity.Node;
import exception.FileFormatException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataParser {
    private List<Node> nodes;		                                         // saves the nodes read from the file.
	public static String DATASET = "";													// path to file that contains the graph data (nodes, links).
    private final String LINE_SPLITTER_IN_FILE = "---";		               // after this point, all nodes are read and we start reading the links.

    public DataParser() {
        nodes = new ArrayList<>();
        readDataFromFile();
    }

    private void readDataFromFile() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(DATASET));
            String line = null;

            int lineNumber = 0;		             // counter that keeps track of the line numbers.
            boolean linkNodesFormat = false;	 // when true: type of line extraction changes.

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.equals(LINE_SPLITTER_IN_FILE)) {		// if splitter found, start extracting the links.
                    linkNodesFormat = true;
                    continue;
                }

                if (!linkNodesFormat) {
                    Node newNode = extractNodeFromLine(line);		// extract node data from current line.
                    nodes.add(newNode);		                        // add node to the list.
                } else {
                    extractLinkFromLine(line, lineNumber);		    // extract link data from current line.
                }
            }

            reader.close();
        } catch (IOException | FileFormatException ex) {
			ex.printStackTrace();
        }
    }

    private void extractLinkFromLine(String line, int lineNumber) throws FileFormatException {
        String[] linkData = line.split(" ");

        String nodeName1 = linkData[0];
        String nodeName2 = linkData[1];
        int dist = Integer.parseInt(linkData[2]);

        Node node1 = null, node2 = null;

		// search for these two nodes in the node list and save them temporarily.
        for (Node node : nodes) {
            if (node.getName().equals(nodeName1)) {
                node1 = node;
            } else if (node.getName().equals(nodeName2)) {
                node2 = node;
            }
        }

        if (node1 == null || node2 == null) {
            throw new FileFormatException(line, lineNumber);		// if either node1 or node2 not found in the list. Throw error.
        }

		// node1 connects with node2.
        node1.addNeighbor(node2, dist);		// node2 = neighbor of node1.
        node2.addNeighbor(node1, dist);		// node1 = neighbor of node2.
    }

    private Node extractNodeFromLine(String line) {
        String[] nodeData = line.split(" ");
        return new Node(nodeData[0], Integer.parseInt(nodeData[1]));
    }

	// retrieve the node list.
    public List<Node> getNodes() {
        return nodes;
    }

    public void setFilePath(String filepath) {
        this.DATASET = filepath;
    }
}
