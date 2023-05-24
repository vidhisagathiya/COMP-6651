package algo_project;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.VertexFactory;
import org.jgrapht.generate.RandomGraphGenerator;
import org.jgrapht.graph.*;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;

/**
 * 
 * A class to visualize and analyze k-colorable graphs. Uses Swing to create a
 * graphical user interface (GUI) and JGraphT to generate and manipulate graphs.
 * Provides functionality to generate a random k-colorable graph, remove
 * triangles from the graph, apply a greedy coloring algorithm to the graph,
 * compute the competitive ratio of the coloring algorithm, and compute the
 * average number of colors used over multiple random graphs.
 */

public class KColorableGraphGUI {

	/**
	 * Main method that starts the GUI on the Event Dispatch Thread (EDT).
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> createAndShowGUI());
	}

	/**
	 * Creates and shows the GUI.
	 */
	private static void createAndShowGUI() {

		JFrame frame = new JFrame("K-Colorable Graph");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(700, 500);
		frame.setLocationRelativeTo(null);

		JPanel panel = new JPanel(new BorderLayout());
		JPanel inputPanel = new JPanel(new BorderLayout());
		JPanel fieldsPanel = new JPanel(new BorderLayout());
		JPanel labelsPanel = new JPanel(new BorderLayout());
		JPanel textFieldsPanel = new JPanel(new BorderLayout());

		JLabel nLabel = new JLabel("Number of vertices (n):");
		JTextField nField = new JTextField();
		JLabel kLabel = new JLabel("Number of colors (k):");
		JTextField kField = new JTextField();
		JLabel pLabel = new JLabel("Edge probability (p):");
		JTextField pField = new JTextField();

		labelsPanel.add(nLabel, BorderLayout.NORTH);
		labelsPanel.add(kLabel, BorderLayout.CENTER);
		labelsPanel.add(pLabel, BorderLayout.SOUTH);
		textFieldsPanel.add(nField, BorderLayout.NORTH);
		textFieldsPanel.add(kField, BorderLayout.CENTER);
		textFieldsPanel.add(pField, BorderLayout.SOUTH);

		fieldsPanel.add(labelsPanel, BorderLayout.WEST);
		fieldsPanel.add(textFieldsPanel, BorderLayout.CENTER);

		inputPanel.add(fieldsPanel, BorderLayout.CENTER);

		JButton runButton = new JButton("Run FirstFit");
		inputPanel.add(runButton, BorderLayout.SOUTH);

		JButton avgColorsButton = new JButton("Compute Average Colors Used");
		inputPanel.add(avgColorsButton, BorderLayout.NORTH);

		panel.add(inputPanel, BorderLayout.NORTH);

		mxGraphComponent graphComponent = new mxGraphComponent(new mxGraph());
		panel.add(graphComponent, BorderLayout.CENTER);

		JTextArea outputArea = new JTextArea();
		outputArea.setEditable(false);

		// Event listener for computing average colors used
		avgColorsButton.addActionListener(e -> {
			try {
				int n = Integer.parseInt(nField.getText());
				int k = Integer.parseInt(kField.getText());
				double p = Double.parseDouble(pField.getText());
				int numGraphs = Integer.parseInt(JOptionPane.showInputDialog("Enter number of graphs to generate:"));

				double avgColorsUsed = computeAverageColorsUsed(n, k, p, numGraphs);
				outputArea.setText(String.format("Average Colors Used: %.2f", avgColorsUsed));

			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid numbers.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		});

		// Event listener for running FirstFit algorithm
		runButton.addActionListener(e -> {
			try {
				int n = Integer.parseInt(nField.getText());
				int k = Integer.parseInt(kField.getText());
				double p = Double.parseDouble(pField.getText());

				Graph<Integer, DefaultEdge> graph = generateGraph(n, p);
				removeTriangles(graph);

				mxGraph mxGraph = new mxGraph();

				Object parent = mxGraph.getDefaultParent();
				mxGraph.getModel().beginUpdate();

				try {
					// Draw vertices
					Map<Integer, Object> vertexMap = new HashMap<>();
					List<List<Integer>> adjacencyList = new ArrayList<>();

					for (int i = 0; i < n; i++) {
						List<Integer> neighbors = Graphs.neighborListOf(graph, i);
						adjacencyList.add(neighbors);
					}

					List<Integer>[] adjacencyArray = new List[n];
					for (int i = 0; i < n; i++) {
						adjacencyArray[i] = adjacencyList.get(i);
					}

					Map<Integer, Integer> vertexColors = KColorableGraph.greedyColoring(adjacencyArray, n);
					for (int i = 0; i < n; i++) {
						int color = vertexColors.get(i);
						String style = "shape=ellipse;fillColor=" + getColorHex(color) + ";";
						vertexMap.put(i, mxGraph.insertVertex(parent, null, String.valueOf(i), 0, 0, 20, 20, style));
					}

					// Draw edges
					for (DefaultEdge edge : graph.edgeSet()) {
						Integer source = graph.getEdgeSource(edge);
						Integer target = graph.getEdgeTarget(edge);
						String edgeStyle = "elbowEdgeStyle";
						mxGraph.insertEdge(parent, null, "", vertexMap.get(source), vertexMap.get(target), edgeStyle);
					}

					// Apply layout to the graph
					mxHierarchicalLayout layout = new mxHierarchicalLayout(mxGraph);
					layout.execute(parent);
					graphComponent.setGraph(mxGraph);

					int colorsUsed = 0;
					for (Integer color : vertexColors.values()) {
						colorsUsed = Math.max(colorsUsed, color);
					}

					float cr = KColorableGraph.competitive_ratio(colorsUsed, k);

					StringBuilder output = new StringBuilder();
					output.append(String.format("Colors used: %d\nCompetitive ratio: %.2f\n", colorsUsed + 1, cr));
					output.append("Vertex colors:\n");
					for (Map.Entry<Integer, Integer> entry : vertexColors.entrySet()) {
						output.append(String.format("Vertex %d: Color %d\n", entry.getKey(), entry.getValue()));
					}
					outputArea.setText(output.toString());

					// Coloring the vertices in the mxGraph
					for (Map.Entry<Integer, Integer> entry : vertexColors.entrySet()) {
						int vertex = entry.getKey();
						int color = entry.getValue();
						Object vertexObject = vertexMap.get(vertex);
						mxGraph.setCellStyles(mxConstants.STYLE_FILLCOLOR, getColorHex(color),
								new Object[] { vertexObject });
					}
					mxGraph.refresh();
				} finally {
					mxGraph.getModel().endUpdate();
				}

			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(null, "Invalid input. Please enter valid numbers.", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		});

		panel.add(new JScrollPane(outputArea), BorderLayout.SOUTH);

		frame.getContentPane().add(panel);

		frame.setVisible(true);
	}

	/**
	 * hexadecimal color code for a given color index. The color index is used to
	 * look up the color from a pre-defined array of colors.
	 * 
	 * @param colorIndex the index of the color to retrieve
	 * @return a hexadecimal color code string for the given color index
	 */
	private static String getColorHex(int colorIndex) {
		Color[] colors = { Color.LIGHT_GRAY, Color.WHITE, Color.PINK, Color.YELLOW, Color.ORANGE, Color.MAGENTA,
				Color.CYAN };
		if (colorIndex >= 0 && colorIndex < colors.length) {
			return String.format("#%02x%02x%02x", colors[colorIndex].getRed(), colors[colorIndex].getGreen(),
					colors[colorIndex].getBlue());
		} else {
			return "#000000"; // Default to black
		}
	}

	/**
	 * Generates a random graph with the specified number of vertices and edge
	 * probability.
	 * 
	 * @param n the number of vertices
	 * @param p the edge probability
	 * @return a random graph with the specified number of vertices and edge
	 *         probability
	 */
	private static Graph<Integer, DefaultEdge> generateGraph(int n, double p) {
		Graph<Integer, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);

		RandomGraphGenerator<Integer, DefaultEdge> generator = new RandomGraphGenerator<>(n,
				(int) Math.round(p * n * (n - 1) / 2));

		// Call createVertex method to initialize vertexCount
		new IntegerVertexFactory().createVertex();

		generator.generateGraph(graph, new IntegerVertexFactory(), null);

		// Print graph details
		System.out.println("Generated graph: " + graph.toString());

		return graph;
	}

	/**
	 * Removes all triangles in the specified graph by iteratively removing edges
	 * until no triangles remain.
	 * 
	 * @param graph the graph from which to remove triangles
	 */
	private static void removeTriangles(Graph<Integer, DefaultEdge> graph) {
		boolean triangleFound;
		do {
			triangleFound = false;
			for (Integer v1 : graph.vertexSet()) {
				for (Integer v2 : Graphs.neighborListOf(graph, v1)) {
					for (Integer v3 : Graphs.neighborListOf(graph, v2)) {
						if (graph.containsEdge(v1, v3)) {
							graph.removeEdge(v1, v3);
							triangleFound = true;
							break;
						}
					}
					if (triangleFound)
						break;
				}
				if (triangleFound)
					break;
			}
		} while (triangleFound);
	}

	/**
	 * A factory for creating integer vertices.
	 */
	private static class IntegerVertexFactory implements VertexFactory<Integer> {
		private int vertexCount = 0;

		@Override
		public Integer createVertex() {
			return vertexCount++;
		}
	}

	/**
	 * Computes the average number of colors used by the FirstFit algorithm over a
	 * given number of random graphs.
	 * 
	 * @param n         the number of vertices in the graphs
	 * @param k         the number of colors to use
	 * @param p         the edge probability of the graphs
	 * @param numGraphs the number of random graphs to generate and color
	 * @return the average number of colors used by the FirstFit algorithm over the
	 *         specified number of random graphs
	 */
	public static double computeAverageColorsUsed(int n, int k, double p, int numGraphs) {
		int totalColorsUsed = 0;
		for (int i = 0; i < numGraphs; i++) {
			Graph<Integer, DefaultEdge> graph = generateGraph(n, p);
			removeTriangles(graph);
			List<Integer>[] adjacencyArray = new ArrayList[n];
			for (int j = 0; j < n; j++) {
				adjacencyArray[j] = Graphs.neighborListOf(graph, j);
			}
			Map<Integer, Integer> vertexColors = KColorableGraph.greedyColoring(adjacencyArray, n);
			int colorsUsed = 0;
			for (int color : vertexColors.values()) {
				colorsUsed = Math.max(colorsUsed, color);
			}
			totalColorsUsed += colorsUsed + 1;
		}
		return (double) totalColorsUsed / numGraphs;
	}
}
