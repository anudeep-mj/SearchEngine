package edu.asu.irs13;

public class CL {

	private int[] links;
	private int[] citations;

	public CL(int[] links, int[] citations) {
		// TODO Auto-generated constructor stub
		this.links = links;
		this.citations = citations;
	}

	public int[] getLinks() {
		return links;
	}

	public void setLinks(int[] links) {
		this.links = links;
	}

	public int[] getCitations() {
		return citations;
	}

	public void setCitations(int[] citations) {
		this.citations = citations;
	}

}
