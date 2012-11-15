package ch.eaternity.client;

import junit.framework.TestSuite;
import junit.framework.Test;
import com.google.gwt.junit.tools.GWTTestSuite;

/* This is the TestSuite, running all test cases automatically which
 * are added here for testing CO2 Calculator.
 * 
 * 
 */
public class EaternityTestSuite extends GWTTestSuite {
	
	// keep adding your TestCases here, otherwise the won't be run automatically
	public static Test suite() {
	    TestSuite suite = new TestSuite("Testing the whole CO2 Calculator.");
	    suite.addTestSuite(TestXmlImport.class); 
	    suite.addTestSuite(TestCo2Calculation.class);
	    
	    
	    
	    return suite;
	}
	
}