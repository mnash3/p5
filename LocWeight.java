// Semester:         CS367 Spring 2017 
// PROJECT:          p5
// FILE:             LocWeight.java
//
// TEAM:    Team 196
// Author1: (Aseel Albeshri,albeshri@wisc.edu,albeshri,lecture 02)
// Author2: (Mickey Nash,mnash3@wisc.edu,mnash3,lecture002)

public class LocWeight implements Comparable<LocWeight>{
	private Location location;
	private double weight;
	public LocWeight (Location location, double weight) {
		this.location = location;
		this.weight = weight;
		
		
	}
	public Location getLocation() {
		return location;
	}
	public double getWeight() {
		return weight;
	}
	
	
	
	public boolean equals(LocWeight x) {
		if (this.location.equals(x.getLocation()))
			return true;
		return false;
		
	}
	
	public int compareTo(LocWeight x) {
	if (this.weight > x.getWeight())	
		return 1;
	if (this.weight < x.getWeight())
		return -1;
	else 
		return 0;
	}

}
