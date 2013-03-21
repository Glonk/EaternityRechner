package ch.eaternity.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import com.googlecode.objectify.annotation.Id;


public class Pair<A, B> implements IsSerializable {
	
	private static final long serialVersionUID = 323987462035191698L;

	@Id Long id;
	
	final public A first;
    final public B second;

    public Pair(A first, B second) {
    	this.first = first;
    	this.second = second;
    }

    public int hashCode() {
    	int hashFirst = first != null ? first.hashCode() : 0;
    	int hashSecond = second != null ? second.hashCode() : 0;

    	return (hashFirst + hashSecond) * hashSecond + hashFirst;
    }

    public boolean equals(Object other) {
    	if (other instanceof Pair) {
    		Pair otherPair = (Pair) other;
    		return 
    		((  this.first == otherPair.first ||
    			( this.first != null && otherPair.first != null &&
    			  this.first.equals(otherPair.first))) &&
    		 (	this.second == otherPair.second ||
    			( this.second != null && otherPair.second != null &&
    			  this.second.equals(otherPair.second))) );
    	}

    	return false;
    }

    public String toString()
    { 
           return "(" + first + ", " + second + ")"; 
    }
    
    public A first() {
		return first;
	}

	public B second() {
		return second;
	}
    
}