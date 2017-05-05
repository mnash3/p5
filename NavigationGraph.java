// Semester:         CS367 Spring 2017 
// PROJECT:          p5
// FILE:             NavigationGraph.java
//
// TEAM:    Team 196
// Author1: (Aseel Albeshri,albeshri@wisc.edu,albeshri,lecture 002)
// Author2: (Mickey Nash,mnash3@wisc.edu,mnash3,lecture 002)
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
This class implements GraphADT and creates a graph of Locations connected by Paths
 */
public class NavigationGraph implements GraphADT<Location, Path> {

	
	private ArrayList<Location> locations; //stores locations
	private ArrayList<ArrayList<Path>> paths; //stores paths
	private String[] edgePropertyNames;	//stores property names


	public NavigationGraph(String[] edgePropertyNames) {
		//initializes the fields
		locations = new ArrayList<Location>();
		paths = new ArrayList<ArrayList<Path>>();
		this.edgePropertyNames = edgePropertyNames;

	}


	/**
	 * Returns a Location object given its name
	 * 
	 * @param name
	 *            name of the location
	 * @return Location object
	 */
	public Location getLocationByName(String name) {
		for (Location x : locations) {
			if (x.getName().equals(name))
				return x;
		}
		return null; 
	}


	@Override

	/**
	 * Adds a vertex to the Graph
	 * 
	 * @param vertex
	 *            vertex to be added
	 */
	public void addVertex(Location vertex) {
		locations.add(vertex);
		paths.add(new ArrayList<Path>());		
	}


	/**
	 * Creates a directed edge from src to dest
	 * 
	 * @param src
	 *            source vertex from where the edge is outgoing
	 * @param dest
	 *            destination vertex where the edge is incoming
	 * @param edge
	 *            edge between src and dest
	 */
	@Override
	public void addEdge(Location src, Location dest, Path edge) {
		int i  =  locations.indexOf(src);
		paths.get(locations.indexOf(src)).add(edge);
	}

	/**
	 * Getter method for the vertices
	 * 
	 * @return List of vertices of type V
	 */
	@Override
	public List<Location> getVertices() {
		return locations;
	}


	/**
	 * Returns edge if there is one from src to dest vertex else null
	 * 
	 * @param src
	 *            Source vertex
	 * @param dest
	 *            Destination vertex
	 * @return Edge of type E from src to dest
	 */
	@Override
	public Path getEdgeIfExists(Location src, Location dest) {
		for (Path p : paths.get(locations.indexOf(src))) 
			if (p.getDestination().equals(dest))
				return p;

		return null;

	}

	/**
	 * Returns the outgoing edges from a vertex
	 * 
	 * @param src
	 *            Source vertex for which the outgoing edges need to be obtained
	 * @return List of edges of type E
	 */
	@Override
	public List<Path> getOutEdges(Location src) {
		return 	paths.get(locations.indexOf(src));
	}

	/**
	 * Returns neighbors of a vertex
	 * 
	 * @param vertex
	 *            vertex for which the neighbors are required
	 * @return List of vertices(neighbors) of type V
	 */
	@Override
	public List<Location> getNeighbors(Location vertex) {

		ArrayList<Location> neighbors = new ArrayList<Location>();

		for (Path p : getOutEdges(vertex))
			neighbors.add(p.getDestination());

		return neighbors;


	}

	/**
	 * Calculate the shortest route from src to dest vertex using
	 * edgePropertyName
	 * 
	 * @param src
	 *            Source vertex from which the shortest route is desired
	 * @param dest
	 *            Destination vertex to which the shortest route is desired
	 * @param edgePropertyName
	 *            edge property by which shortest route has to be calculated
	 * @return List of edges that denote the shortest route by edgePropertyName
	 */

	@Override
	public List<Path> getShortestRoute(Location src, Location dest, String edgePropertyName) {
		//array to keep track of visited vertexes
		boolean[] visited = new boolean[locations.size()];
		//array to store weights
		double[] weight = new double[locations.size()];
		//array to store predecessors
		Location[] pre = new Location[locations.size()];
		//creates priority queue
		PriorityQueue<LocWeight> pq = new PriorityQueue<LocWeight>();
		// initializes every vertex misted mark to false
		for (int i = 0; i < visited.length; i++)
			visited[i] = false;
		// initializes every vertex's total weight to infinity
		for (int i = 0; i < weight.length; i++)
			weight[i] = Double.POSITIVE_INFINITY;
		// initializes every vertex's predecessor to null
		for (int i = 0; i < pre.length; i++)
			pre[i] = null;
		//sets start vertex's total weight to 0
		weight[locations.indexOf(src)] = 0;
		//insert start vertex in priroty queue
		pq.add(new LocWeight(src, 0.0));
		
		String[] edgeNames = getEdgePropertyNames();
		int indexProp = 0;
		for (int i = 0; i < edgeNames.length; i++) {
			if (edgeNames[i].equalsIgnoreCase(edgePropertyName))
				indexProp = i;
		}
		while (!pq.isEmpty()) {
			LocWeight c = pq.remove();
			//set C's visited mark to true
			visited[locations.indexOf(c)] = true;

			List<Location> neighbors = getNeighbors(c.getLocation());
			//for each unvisited successor adjacent to C
			for (int i = 0; i < neighbors.size(); i++) {
				if (visited[locations.indexOf(neighbors.get(i))] == false) {
					//change successor's total weight to equal C's weight + edge weight from C to successor
					weight[locations.indexOf(neighbors.get(i))] = c.getWeight() + getEdgeIfExists(c.getLocation(), neighbors.get(i)).getProperties().get(indexProp);
					pre[locations.indexOf(neighbors.get(i))] = c.getLocation();
					LocWeight succ = new LocWeight(neighbors.get(i), weight[locations.indexOf(neighbors.get(i))]);
					//if successor is already in pq, update its total weight
					if (pq.contains(succ)) {
						pq.remove(succ);
					}
					//if not already there, add
					pq.add(succ);

				}

			}
			
			


		}
		
		ArrayList<Path> path = new ArrayList<Path>();
		//find predecessor of each vertex and construct shortest path then return it
		Location curr1 = dest;
		while (pre[locations.indexOf(curr1)] != null) {
			path.add(getEdgeIfExists(pre[locations.indexOf(curr1)], curr1));
			curr1 = pre[locations.indexOf(curr1)];
		}
		//to show path from the start
		for (int i = path.size() - 1; i >= 0; i--)
			path.add(path.remove(i));


		return path;
	}

	/**
	 * Getter method for edge property names
	 * 
	 * @return array of String that denotes the edge property names
	 */
	@Override
	public String[] getEdgePropertyNames() {
		return edgePropertyNames;
	}
	/**
	 * Return a string representation of the graph
	 * @return String representation of the graph
	 */
	
	@Override
	public String toString() {
		String path = "";
	for (int i = 0; i < paths.size(); i ++) {
		for (int j = 0; j < paths.get(i).size(); j++)
			path += paths.get(i).get(j) + ", ";
		path += "\n";
	}
	return path;
		
	}

}
