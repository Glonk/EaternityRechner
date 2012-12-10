package ch.eaternity.shared;

import java.util.List;
import java.util.Date;


public class Categories {
	
	// Map<String,Long> CategoryValue;
	public class CategoryValue {
		public String categoryName;
		public Long co2value;
		
		public CategoryValue(){
		}
		
		public CategoryValue(String name, Long value) {
			this.categoryName = name;
			this.co2value = value;
		}
	}
	
	public class CategoryValuesByDates {
		List<CategoryValue> category;
		List<Date> date;
		
		public CategoryValuesByDates(){
		}
		
		public CategoryValuesByDates(List<CategoryValue> category, List<Date> date){
			this.category = category;
			this.date = date;
		}
		
	}

}