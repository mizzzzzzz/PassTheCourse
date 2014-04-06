package no2.finalproject.skipclasssaver;

public class Course {
	int id;
	String name;
	String place;
	String time;
	
	public Course() {
		id = 0;
		name = null;
		place = null;
		time = null;
	}
	
	public Course(int id, String name, String place, String time) {
		this.id = id;
		this.name = name;
		this.place = place;
		this.time = time;
	}
	
	public String getName() {
		return name;
	}
	public String getPlace() {
		return place;
	}
	public String getTime() {
		return time;
	}
	public int getId() {
		return id;
	}
	
	public void setName(String s) {
		name = s;
	}
	public void setPlace(String s) {
		place = s;
	}
	public void setTime(String s) {
		time = s;
	}
	public void setId(int i) {
		id= i;
	}
}
