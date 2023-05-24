package algo_project;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KColorableGraph {

	/**
	 * Computes a greedy coloring of a graph using the FirstFit algorithm.
	 * 
	 * @param adjacencyArray an array of adjacency lists representing the graph
	 * @param n              the number of vertices in the graph
	 * @return a mapping of each vertex to its assigned color
	 */
	public static Map<Integer, Integer> greedyColoring(List<Integer>[] adjacencyArray, int n) {
		Map<Integer, Integer> vertexColors = new HashMap<>();
		boolean[] availableColors = new boolean[n];

		for (int u = 0; u < n; u++) {
			Arrays.fill(availableColors, true);

			for (int neighbor : adjacencyArray[u]) {
				if (vertexColors.containsKey(neighbor)) {
					int color = vertexColors.get(neighbor);
					availableColors[color] = false;
				}
			}
			int color;
			for (color = 0; color < n; color++) {
				if (availableColors[color]) {
					break;
				}
			}
			vertexColors.put(u, color);
		}
		return vertexColors;
	}

	/**
	 * Computes the competitive ratio of a coloring algorithm by dividing the number
	 * of colors used by the maximum number of colors allowed.
	 * 
	 * @param colorsUsed the number of colors used by the algorithm
	 * @param k          the maximum number of colors allowed
	 * @return the competitive ratio of the coloring algorithm
	 */
	public static float competitive_ratio(int colorsUsed, int k) {
		return (float) (colorsUsed + 1) / k;
	}

}
