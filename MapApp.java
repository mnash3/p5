/////////////////////////////////////////////////////////////////////////////
// Semester:         CS367 Spring 2016 
// PROJECT:          p5
// FILE:             MapApp.java
//
// TEAM:    Team 196
// Authors: 
// Author1: (Mickey Nash,mnash3@wisc.edu,mnash3,002)
// Author2: (Aseel Albeshri,albeshri@wisc.edu,albeshri,lecture 02)
//
// ---------------- OTHER ASSISTANCE CREDITS 
// Persons: Identify persons by name, relationship to you, and email. 
// Describe in detail the the ideas and help they provided. 
// 
// Online sources: avoid web searches to solve your problems, but if you do 
// search, be sure to include Web URLs and description of 
// of any information you find. 
/////////////////////////////////////////////////////////////////////////////

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Driver class that reads/parses the input file and creates NavigationGraph
 * object.
 * 
 * @author CS367
 *
 */
public class MapApp {

	private NavigationGraph graphObject; // graph object to be created

	/**
	 * Constructs a MapApp object
	 * 
	 * @param graph
	 *            NaviagtionGraph object
	 */
	public MapApp(NavigationGraph graph) {
		this.graphObject = graph;
	}
	
	/**
	 * The main method that runs the program.
	 * 
	 * @param args
	 * 			 an array of Strings used for the command line arguments
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage: java MapApp <pathToGraphFile>");
			System.exit(1);
		}

		// read the filename from command line argument
		String locationFileName = args[0];
		try {
			NavigationGraph graph = createNavigationGraphFromMapFile(locationFileName);
			MapApp appInstance = new MapApp(graph);
			appInstance.startService();

		} catch (FileNotFoundException e) {
			System.out.println("GRAPH FILE: " + locationFileName + " was not found.");
			System.exit(1);
		} catch (InvalidFileException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}	
	}

	/**
	 * Displays options to user about the various operations on the loaded graph
	 */
	public void startService() {

		System.out.println("Navigation App");
		Scanner sc = new Scanner(System.in);

		int choice = 0;
		do {
			System.out.println();
			System.out.println("1. List all locations");
			System.out.println("2. Display Graph");
			System.out.println("3. Display Outgoing Edges");
			System.out.println("4. Display Shortest Route");
			System.out.println("5. Quit");
			System.out.print("Enter your choice: ");

			while (!sc.hasNextInt()) {
				sc.next();
				System.out.println("Please select a valid option: ");
			}
			choice = sc.nextInt();

			switch (choice) {
			case 1:
				System.out.println(graphObject.getVertices());
				break;
			case 2:
				System.out.println(graphObject.toString());
				break;
			case 3: {
				System.out.println("Enter source location name: ");
				String srcName = sc.next();
				Location src = graphObject.getLocationByName(srcName);

				if (src == null) {
					System.out.println(srcName + " is not a valid Location");
					break;
				}

				List<Path> outEdges = graphObject.getOutEdges(src);
				System.out.println("Outgoing edges for " + src + ": ");
				for (Path path : outEdges) {
					System.out.println(path);
				}
			}
				break;

			case 4:
				System.out.println("Enter source location name: ");
				String srcName = sc.next();
				Location src = graphObject.getLocationByName(srcName);

				System.out.println("Enter destination location name: ");
				String destName = sc.next();
				Location dest = graphObject.getLocationByName(destName);

				if (src == null || dest == null) {
					System.out.println(srcName + " and/or " + destName + " are not valid Locations in the graph");
					break;
				}

				if (src == dest) {
					System.out.println(srcName + " and " + destName + " correspond to the same Location");
					break;
				}

				System.out.println("Edge properties: ");
				// List Edge Property Names
				String[] propertyNames = graphObject.getEdgePropertyNames();
				for (int i = 0; i < propertyNames.length; i++) {
					System.out.println("\t" + (i + 1) + ": " + propertyNames[i]);
				}
				System.out.println("Select property to compute shortest route on: ");
				int selectedPropertyIndex = sc.nextInt() - 1;

				if (selectedPropertyIndex >= propertyNames.length) {
					System.out.println("Invalid option chosen: " + (selectedPropertyIndex + 1));
					break;
				}

				String selectedPropertyName = propertyNames[selectedPropertyIndex];
				List<Path> shortestRoute = graphObject.getShortestRoute(src, dest, selectedPropertyName);
				for(Path path : shortestRoute) {
					System.out.print(path.displayPathWithProperty(selectedPropertyIndex)+", ");
				}
				if(shortestRoute.size()==0) {
					System.out.print("No route exists");
				}
				System.out.println();

				break;

			case 5:
				break;

			default:
				System.out.println("Please select a valid option: ");
				break;

			}
		} while (choice != 5);
		sc.close();
	}

	/**
	 * Reads and parses the input file passed as argument create a
	 * NavigationGraph object. The edge property names required for
	 * the constructor can be gotten from the first line of the file
	 * by ignoring the first 2 columns - source, destination. 
	 * Use the graph object to add vertices and edges as
	 * you read the input file.
	 * 
	 * @param graphFilepath
	 *            path to the input file
	 * @return NavigationGraph object
	 * @throws FileNotFoundException
	 *             if graphFilepath is not found
	 * @throws InvalidFileException
	 *             if header line in the file has < 3 columns or 
	 *             if any line that describes an edge has different number of properties 
	 *             	than as described in the header or 
	 *             if any property value is not numeric 
	 */

	public static NavigationGraph createNavigationGraphFromMapFile(String graphFilepath) 
			throws FileNotFoundException, InvalidFileException {
			// read/parse the input file graphFilepath and create
			// NavigationGraph with vertices and edges
		
		NavigationGraph graphObject; 
		String[] headerArray;	// items from header in an array
		int headerCols = 0;		// number of columns in header
		String[] currLine;		// items from current line in an array
		int edgeCols;			// number of columns in edge
		ArrayList<String> locationList = new ArrayList<String>();	// ArrayList of all the location names
		String[] edgePropNames;	// edge property names 
		ArrayList<Double> pathPropList;	// List of the path properties of single path
		ArrayList<List<Double>> pathList = new ArrayList<List<Double>>();	
																		// ArrayList of all the path properties
		
		// creates new file from input 
		File file = new File(graphFilepath);
		Scanner scanner = new Scanner(file);
		
		// reads in header line and checks number of columns
		headerArray = scanner.nextLine().split(" ");
		for (int i = 0; i < headerArray.length; i++) {
			headerCols++;
		}
		
		// gets the edge property names into an array for the constructor
		edgePropNames = new String[headerCols-2];
		for (int i = 2; i < headerCols; i++) {
			edgePropNames[i-2] = headerArray[i];
		}
		
		// creates graph
		graphObject = new NavigationGraph(edgePropNames);
		
		// throws exception if not enough columns
		if (headerCols < 3) {
			throw new InvalidFileException("Invalid File");
		}
		
		// reads in all of the files lines
		while (scanner.hasNextLine()) {
			edgeCols = 0;
			currLine = scanner.nextLine().split(" ");
			
			// checks srcs and dests to see which vertexes need to be added
			for (int i = 0; i < 2; i++) {
				Location loc = new Location(currLine[i]);
		
				if (locationList.isEmpty()) {
					locationList.add(loc.getName());
					graphObject.addVertex(loc);
				}
				else if (!locationList.contains(loc.getName())) {
							locationList.add(loc.getName());
							graphObject.addVertex(loc);
				}
			}
			
			// checks for columns in the edge line, if not same as header, throws exception
			for (int i = 0; i < currLine.length; i++) {
				edgeCols++;
			}			
			if (edgeCols != headerCols) {
				throw new InvalidFileException("Invalid File");
			}
			
			// checks to see if properties are numeric and greater than 0
			pathPropList = new ArrayList<Double>();
			for (int i = 2; i < currLine.length; i++) {
				try {
					Double currProp = Double.parseDouble(currLine[i]);
	
					if (currProp > 0) {
						pathPropList.add(currProp);
					}
					else {
						pathPropList.add(1.0);
					}
					
				} catch (NumberFormatException e) {
					throw new InvalidFileException("Invalid File -- Not Numeric");
				}
			}
			
			// creates src locations, dest locations for the path object
			// creates the path object
			Location srcLoc = new Location(currLine[0]);
			Location destLoc = new Location(currLine[1]);
			Path path = new Path(srcLoc, destLoc, pathPropList);
			
			
			// adds edge if no edges exist
			if (pathList.isEmpty()) {
				pathList.add(path.getProperties());
				graphObject.addEdge(srcLoc, destLoc, path);
			}
			
			// adds edge if not already added
			else if (!pathList.contains(path.getProperties())) {
				pathList.add(path.getProperties());
				graphObject.addEdge(srcLoc, destLoc, path);
			}
		}
		return graphObject;
	}

}
