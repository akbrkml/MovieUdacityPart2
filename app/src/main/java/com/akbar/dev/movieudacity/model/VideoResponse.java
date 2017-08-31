package com.akbar.dev.movieudacity.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class VideoResponse{

	@SerializedName("id")
	private int id;

	@SerializedName("results")
	private List<Video> results;

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setResults(List<Video> results){
		this.results = results;
	}

	public List<Video> getResults(){
		return results;
	}
}