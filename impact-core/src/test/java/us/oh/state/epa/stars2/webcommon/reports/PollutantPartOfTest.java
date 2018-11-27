package us.oh.state.epa.stars2.webcommon.reports;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

/**
 * The class <code>PollutantPartOfTest</code> contains tests for the class
 * {@link <code>PollutantPartOf</code>}
 *
 * @pattern JUnit Test Case
 *
 * @generatedBy CodePro at 6/20/14 1:47 PM
 *
 * @author mcolbert
 *
 * @version $Revision$
 */
public class PollutantPartOfTest extends TestCase {

	/**
	 * Construct new test instance
	 *
	 * @param name the test name
	 */
	public PollutantPartOfTest(String name) {
		super(name);
	}

	public void testMapSubsets() throws Exception
	{
        Map<String, Set<String>> direct = new HashMap<String, Set<String>>();
        direct.put("A", setOf("B","E"));
        direct.put("B", setOf("C"));
        direct.put("C", setOf("D"));
        
        PollutantPartOf partOf = new PollutantPartOf();
        Map<String, Set<String>> all = new HashMap<String, Set<String>>();
        for (String sup : direct.keySet()) {
        	partOf.mapTransitiveSubsets(direct, all, sup);
        }

        assertTrue(all.containsKey("A"));
        assertTrue(all.get("A").size() == 4);
        assertTrue(all.get("A").contains("B"));
        assertTrue(all.get("A").contains("C"));
        assertTrue(all.get("A").contains("D"));
        assertTrue(all.get("A").contains("E"));

        assertTrue(all.containsKey("B"));
        assertTrue(all.get("B").size() == 2);
        assertTrue(all.get("B").contains("C"));
        assertTrue(all.get("B").contains("D"));
        
        assertTrue(all.containsKey("C"));
        assertTrue(all.get("C").size() == 1);
        assertTrue(all.get("C").contains("D"));
	}

	public void testMapSubsetsCyclic()
	{
        Map<String, Set<String>> direct = new HashMap<String, Set<String>>();
        direct.put("A", setOf("B","E"));
        direct.put("B", setOf("C"));
        direct.put("C", setOf("D"));
        direct.put("D", setOf("A"));

        PollutantPartOf partOf = new PollutantPartOf();
        try {
			Map<String, Set<String>> all = new HashMap<String, Set<String>>();
			for (String sup : direct.keySet()) {
				partOf.mapTransitiveSubsets(direct, all, sup);
			}
			fail("Expected an exception.");
		} catch (Exception e) {
			//pass
			e.printStackTrace();
		}
	}
	
	private HashSet<String> setOf(String... strings) {
		HashSet<String> set = new HashSet<String>();
		Collections.addAll(set, strings);
		return set;
	}
	
	public void testMapSupersets() throws Exception
	{
        Map<String, HashSet<String>> direct = new HashMap<String, HashSet<String>>();
        direct.put("A", setOf("B","E"));
        direct.put("B", setOf("C"));
        direct.put("C", setOf("D"));
        
        PollutantPartOf partOf = new PollutantPartOf();
        Map<String, HashSet<String>> all = new HashMap<String, HashSet<String>>();
        for (String sub : direct.keySet()) {
        	partOf.mapTransitiveSupersets(direct, all, sub);
        }

        assertTrue(all.containsKey("A"));
        assertTrue(all.get("A").size() == 4);
        assertTrue(all.get("A").contains("B"));
        assertTrue(all.get("A").contains("C"));
        assertTrue(all.get("A").contains("D"));
        assertTrue(all.get("A").contains("E"));

        assertTrue(all.containsKey("B"));
        assertTrue(all.get("B").size() == 2);
        assertTrue(all.get("B").contains("C"));
        assertTrue(all.get("B").contains("D"));
        
        assertTrue(all.containsKey("C"));
        assertTrue(all.get("C").size() == 1);
        assertTrue(all.get("C").contains("D"));
	}

	public void testMapSupersetsCyclic()
	{
        Map<String, HashSet<String>> direct = new HashMap<String, HashSet<String>>();
        direct.put("A", setOf("B","E"));
        direct.put("B", setOf("C"));
        direct.put("C", setOf("D"));
        direct.put("D", setOf("A"));

        PollutantPartOf partOf = new PollutantPartOf();
        try {
			Map<String, HashSet<String>> all = new HashMap<String, HashSet<String>>();
			for (String sub : direct.keySet()) {
				partOf.mapTransitiveSupersets(direct, all, sub);
			}
			fail("Expected an exception.");
		} catch (Exception e) {
			//pass
			e.printStackTrace();
		}
	}
}

/*$CPS$ This comment was generated by CodePro. Do not edit it.
 * patternId = com.instantiations.assist.eclipse.pattern.testCasePattern
 * strategyId = com.instantiations.assist.eclipse.pattern.testCasePattern.junitTestCase
 * additionalTestNames = 
 * assertTrue = false
 * callTestMethod = true
 * createMain = false
 * createSetUp = false
 * createTearDown = false
 * createTestFixture = false
 * createTestStubs = false
 * methods = verifySubpartTotals(QEmissions;!QHashMap<QString;QEmissions;>;)
 * package = us.oh.state.epa.stars2.webcommon.reports
 * package.sourceFolder = impact-core/src/test/java
 * superclassType = junit.framework.TestCase
 * testCase = PollutantPartOfTest
 * testClassType = us.oh.state.epa.stars2.webcommon.reports.PollutantPartOf
 */