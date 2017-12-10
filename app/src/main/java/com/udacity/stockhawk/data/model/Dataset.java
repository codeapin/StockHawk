package com.udacity.stockhawk.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Dataset{

	@SerializedName("end_date")
	private String endDate;

	@SerializedName("data")
	private List<List<String>> data;

	@SerializedName("description")
	private String description;

	@SerializedName("newest_available_date")
	private String newestAvailableDate;

	@SerializedName("type")
	private String type;

	@SerializedName("dataset_code")
	private String datasetCode;

	@SerializedName("column_index")
	private int columnIndex;

	@SerializedName("frequency")
	private String frequency;

	@SerializedName("oldest_available_date")
	private String oldestAvailableDate;

	@SerializedName("transform")
	private Object transform;

	@SerializedName("premium")
	private boolean premium;

	@SerializedName("refreshed_at")
	private String refreshedAt;

	@SerializedName("database_id")
	private int databaseId;

	@SerializedName("database_code")
	private String databaseCode;

	@SerializedName("name")
	private String name;

	@SerializedName("limit")
	private Object limit;

	@SerializedName("id")
	private int id;

	@SerializedName("column_names")
	private List<String> columnNames;

	@SerializedName("collapse")
	private Object collapse;

	@SerializedName("start_date")
	private String startDate;

	@SerializedName("order")
	private Object order;

	public void setEndDate(String endDate){
		this.endDate = endDate;
	}

	public String getEndDate(){
		return endDate;
	}

	public void setData(List<List<String>> data){
		this.data = data;
	}

	public List<List<String>> getData(){
		return data;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	public void setNewestAvailableDate(String newestAvailableDate){
		this.newestAvailableDate = newestAvailableDate;
	}

	public String getNewestAvailableDate(){
		return newestAvailableDate;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	public void setDatasetCode(String datasetCode){
		this.datasetCode = datasetCode;
	}

	public String getDatasetCode(){
		return datasetCode;
	}

	public void setColumnIndex(int columnIndex){
		this.columnIndex = columnIndex;
	}

	public int getColumnIndex(){
		return columnIndex;
	}

	public void setFrequency(String frequency){
		this.frequency = frequency;
	}

	public String getFrequency(){
		return frequency;
	}

	public void setOldestAvailableDate(String oldestAvailableDate){
		this.oldestAvailableDate = oldestAvailableDate;
	}

	public String getOldestAvailableDate(){
		return oldestAvailableDate;
	}

	public void setTransform(Object transform){
		this.transform = transform;
	}

	public Object getTransform(){
		return transform;
	}

	public void setPremium(boolean premium){
		this.premium = premium;
	}

	public boolean isPremium(){
		return premium;
	}

	public void setRefreshedAt(String refreshedAt){
		this.refreshedAt = refreshedAt;
	}

	public String getRefreshedAt(){
		return refreshedAt;
	}

	public void setDatabaseId(int databaseId){
		this.databaseId = databaseId;
	}

	public int getDatabaseId(){
		return databaseId;
	}

	public void setDatabaseCode(String databaseCode){
		this.databaseCode = databaseCode;
	}

	public String getDatabaseCode(){
		return databaseCode;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setLimit(Object limit){
		this.limit = limit;
	}

	public Object getLimit(){
		return limit;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setColumnNames(List<String> columnNames){
		this.columnNames = columnNames;
	}

	public List<String> getColumnNames(){
		return columnNames;
	}

	public void setCollapse(Object collapse){
		this.collapse = collapse;
	}

	public Object getCollapse(){
		return collapse;
	}

	public void setStartDate(String startDate){
		this.startDate = startDate;
	}

	public String getStartDate(){
		return startDate;
	}

	public void setOrder(Object order){
		this.order = order;
	}

	public Object getOrder(){
		return order;
	}
}