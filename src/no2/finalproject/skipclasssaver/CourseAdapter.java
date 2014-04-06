package no2.finalproject.skipclasssaver;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TextView;

public class CourseAdapter extends BaseAdapter {
	List<Course> courseList;
	Context context;
	HashMap<Integer, Boolean> state = new HashMap<Integer, Boolean>();
	public CourseAdapter(List<Course> courseList, Context ctx) {
	    this.courseList = courseList;
	    this.context = ctx;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return courseList.size();
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return courseList.get(position);
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public View getView(final int position, View convertView, ViewGroup parent) {
	     
	    // First let's verify the convertView is not null
	    if (convertView == null) {
	        // This a new view we inflate the new layout
	        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        convertView = inflater.inflate(R.layout.row_layout, parent, false);
	    }
	    // Now we can fill the layout with the right values
	    TextView tv = (TextView) convertView.findViewById(R.id.name);
	    TextView placeView = (TextView) convertView.findViewById(R.id.place);
        TextView timeView = (TextView) convertView.findViewById(R.id.time);
        RadioButton radio = (RadioButton) convertView.findViewById(R.id.radio);
        radio.setOnCheckedChangeListener(new OnCheckedChangeListener() {  
        	@Override  
        	public void onCheckedChanged(CompoundButton buttonView,  
        	boolean isChecked) {  
        	// TODO Auto-generated method stub  
        	if (isChecked) {  
        		state.put(position, isChecked);  
        		} else {  
        			state.remove(position);  
        		}  
        	}  
        });  
        Course c = courseList.get(position);
 
        tv.setText(c.getName());
        placeView.setText(c.getPlace());
        timeView.setText(c.getTime());
        radio.setChecked((state.get(position) == null ? false : true));
	    return convertView;
	}
}
