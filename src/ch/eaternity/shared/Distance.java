package ch.eaternity.shared;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class Distance {
	
	// String is currentLocation, List(SingeDistances) From Locations
	private MultiMap<String, List<SingleDistance>> distances;
	
	public Distance() {
		distances = HashMultimap.create();
	}
	
	public double getDistance(String from, String to) {}
	
	/*

		for(SingleDistance singleDistance : presenter.getDCO().cdata.distances){
			if(singleDistance.getFrom().contentEquals(getTopPanel().currentHerkunft) && 
					singleDistance.getTo().contentEquals(zutatSpec.getHerkunft().symbol)){
				
				zutatSpec.setDistance(singleDistance.getDistance());
				iterator.set(zutatSpec);
				break;
			}

		}
	}*/
}
