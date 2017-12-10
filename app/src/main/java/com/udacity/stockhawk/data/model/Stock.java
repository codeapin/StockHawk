package com.udacity.stockhawk.data.model;

import com.google.gson.annotations.SerializedName;

public class Stock {

	@SerializedName("dataset")
	private Dataset dataset;

	public void setDataset(Dataset dataset){
		this.dataset = dataset;
	}

	public Dataset getDataset(){
		return dataset;
	}
}