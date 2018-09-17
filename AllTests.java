import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import java.lang.StringBuffer;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

/*
NOTES

/*


/*
Tests are at the bottom.  They look like this:

	@Test(timeout=60000) public void distinctSizeNoDups()
	{
		p = 9;

		assertTrue("distinctSize() returns wrong value for tree with no duplicates", bst0.distinctSize() == bstRef0.distinctSize());
	}

	The test fails iff the last argument to the assert is false.

	JUnit is not meant for tracking point totals, so we are forced to use crude methods.  We use p to track the point value of the current
	question; in the TestWatcher instance, we reset p to 9000 after each question so we can detect if we forget to set p in the next question.

	We track both lost points and earned points so when we output we can check whether the totals are correct.

*/

public class AllTests {

    // general functionality
    static int totalPoints = 330; //disincluding efficiency, which will be added later

    static int p = 9001;
    static int partial = 0;
    static int totalEarned = 0;
    static int totalLost = 0;
    static StringBuilder builder = new StringBuilder();

    // test-specific data
    static int effVerticesCount = 500;
    static ArrayList<String> effVertices = new ArrayList<String>();

    static int effVertexSetCount = 100;
    static ArrayList<String> effVertexSet = new ArrayList<String>();

    static long shortestPathIts = effVerticesCount * effVerticesCount / 16;
    static long influenceIts = effVerticesCount / 20;

    // correctness data for efficiency calculations
    static int shortestPathCorrectness = 0;
    static int distanceSetCorrectness = 0;
    static int influenceCorrectness = 0;
    static int influenceSetCorrectness = 0;

    static int shortestPathCorrectnessTot = 10;
    static int distanceSetCorrectnessTot = 20;
    static int influenceCorrectnessTot = 20;
    static int influenceSetCorrectnessTot = 20;

    // timing data
    static long shortestPathDelta = Long.MAX_VALUE;
    static long distanceSetDelta = Long.MAX_VALUE;
    static long influenceDelta = Long.MAX_VALUE;
    static long influenceSetDelta = Long.MAX_VALUE;

    static long shortestPathDeltaRef = Long.MAX_VALUE;
    static long distanceSetDeltaRef = Long.MAX_VALUE;
    static long influenceDeltaRef = Long.MAX_VALUE;
    static long influenceSetDeltaRef = Long.MAX_VALUE;

    // crawler stuff
    static List<Pair<String, String>>[] crawlerEdges;
    static String BASE_URL = "http://web.cs.iastate.edu/~pavan";
    static String CRAWLERSEEDS[] = { "/wiki/A.html", "/wiki/A2.html", "/wiki/A1.html", "/wiki/D.html", "/wiki/D1.html",
            "/wiki/D2.html", "/wiki/G.html", "/wiki/G1.html", "/wiki/G2.html", "/wiki/J.html", "/wiki/AA.html",
            "/wiki/L.html", "/wiki/L1.html", "/wiki/L2.html", "/wiki/L3.html", "/wiki/L.html", "/wiki/L1.html" };
    static String OUTPUTFILE = "temp.txt";

    // @Rule
    // public Timeout timeout = Timeout.seconds(60);

    @Rule
    public TestWatcher watcher = new TestWatcher() {
        @Override
        protected void succeeded(Description description) {

            if (p > totalPoints) {
                System.out.println("ERROR: POINT VALUES NOT RESET!"); // this ensures that we don't miss resetting point values
            }

            totalEarned += p;
            p = 9000;
        }

        @Override
        protected void failed(Throwable e, Description description) {

            if (p > totalPoints) {
                System.out.println("ERROR: POINT VALUES NOT RESET!"); // this ensures that we don't miss resetting point values
            }

            if (partial > 0) {
                builder.append("test " + description + " partially failed with exception " + e + " (-" + (p - partial)
                        + ");\n");

                totalLost += p - partial;
                totalEarned += partial;
                partial = 0;
            } else {
                builder.append("test " + description + " failed with exception " + e + " (-" + p + "); \n");
                totalLost += p;
            }

            p = 9000;
        }
    };

    @AfterClass
    public static void printResults() {
        if (totalEarned + totalLost != totalPoints) {
            System.out.println(
                    "\n\nERROR!  Earned " + totalEarned + " but lost " + totalLost + "; should add to " + totalPoints);
            System.out.println("missing " + (totalPoints - totalEarned - totalLost));
        }

        float shortestPathRatio = (float) shortestPathCorrectness / (float) shortestPathCorrectnessTot;
        float distanceSetRatio = (float) distanceSetCorrectness / (float) distanceSetCorrectnessTot;
        float influenceRatio = (float) influenceCorrectness / (float) influenceCorrectnessTot;
        float influenceSetRatio = (float) influenceSetCorrectness / (float) influenceSetCorrectnessTot;

        float shortestPathEff = 30f * shortestPathRatio
                * Math.min(1f, 3.0f * (float) shortestPathDeltaRef / (float) shortestPathDelta);
        float distanceSetEff = 30f * distanceSetRatio
                * Math.min(1f, 3.0f * (float) distanceSetDeltaRef / (float) distanceSetDelta);
        float influenceEff = 30f * influenceRatio
                * Math.min(1f, 3.0f * (float) influenceDeltaRef / (float) influenceDelta);
        float influenceSetEff = 30f * influenceSetRatio
                * Math.min(1f, 3.0f * (float) influenceSetDeltaRef / (float) influenceSetDelta);

        float pointsEff = shortestPathEff + distanceSetEff + influenceEff + influenceSetEff;
        float pointsCorr = totalEarned;

        int total = (int) Math.ceil(pointsEff + pointsCorr);

        System.out.println("\n================================================================");
        System.out.println("points before report: " + Math.min(total, 450) + "; comments below ");
        System.out.println("================================================================");
        System.out.println("[total points: " + Math.min(total, 450) + "/450]");
        System.out.println("");
        System.out
                .println("[correctness: " + pointsCorr + "/330; correctness-scaled efficiency: " + pointsEff + "/120]");
        System.out.println("[correctness deductions:");
        System.out.println(builder.toString());
        System.out.println("]");
        System.out.println("");
        System.out.println(
                "[We calculated several efficiency categories; for each, we scaled by correctness and compared student runtime to a reference implementation:");
        System.out.println(
                "efficiencyPoints = pointsPossible * correctnessPoints / correctnessPointsPossible * min(1, 3.0 * referenceTime / studentTime).");
        System.out.println("(Units are nanoseconds)]");
        System.out.println("[shortestPath (" + shortestPathEff + "/30): " + shortestPathDelta + " student, "
                + shortestPathDeltaRef + " ref]");
        System.out.println("[distance (from set) (" + distanceSetEff + "/30): " + distanceSetDelta + " student, "
                + distanceSetDeltaRef + " ref]");
        System.out.println("[influence (of vertex) (" + influenceEff + "/30): " + influenceDelta + " student, "
                + influenceDeltaRef + " ref]");
        System.out.println("[influence (of set) (" + influenceSetEff + "/30): " + influenceSetDelta + " student, "
                + influenceSetDeltaRef + " ref]");
        System.out.println("================================================================");

    }

    // ================================================================
    // SETUP
    // ================================================================
    @BeforeClass
    public static void setUp() throws Exception {
        boolean success = true;

        // Init wiki crawler solutions.
        try {
            crawlerEdges = new List[17];
            for (int i = 1; i <= 15; i++) {
                int max = 3;
                if (i == 15)
                    max = 4;
                String path = "./data/graph" + i + "-" + "max" + max + "-notopics.txt";
                crawlerEdges[i - 1] = parseGraph(path);
            }
            String path = "./data/graph" + 12 + "-" + "max" + 4 + "-2topics.txt";
            crawlerEdges[15] = parseGraph(path);
            path = "./data/graph" + 13 + "-" + "max" + 4 + "-2topics.txt";
            crawlerEdges[16] = parseGraph(path);
        } catch (Exception e) {
            success = false;
        }

        if (!success) {
            System.out.println("WARNING: graph setup failed for at least one test graph");
        }

        // Init something else.
        try {
            effVertices = new ArrayList<String>();
            for (char i = 'A'; i <= 'Z'; i++) {
                for (char j = 'A'; j <= 'Z'; j++) {
                    if (effVertices.size() >= effVerticesCount)
                        break;
                    effVertices.add("" + i + j);

                    if (effVertexSet.size() < effVertexSetCount) {
                        effVertexSet.add("" + i + j);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("SETUP ERROR: couldn't setup efficiency vertices");
            success = false;
        }

    }

    public static boolean strListEquals(List<String> a, List<String> b) {
        if (a.size() != b.size())
            return false;
        for (int i = 0; i < a.size(); i++) {
            if (!a.get(i).equals(b.get(i)))
                return false;
        }
        return true;
    }

    // ================================================================
    // SHORTESTPATH AND DISTANCE TESTS
    // ================================================================

    //
    // @Test(timeout = 10000)
    // public void outDegree1() throws Exception {
    //     p = 0;
    //     NetworkInfluence ni = new NetworkInfluence("inf_graphs/short-dist-3.txt");
    //     int answer = 2;
    //     assertTrue("outDegree test 1 returned incorrect value", ni.outDegree("A") == answer);
    // }

    // //
    // @Test(timeout = 10000)
    // public void outDegree2() throws Exception {
    //     p = 0;
    //     NetworkInfluence ni = new NetworkInfluence("inf_graphs/short-dist-3.txt");
    //     int answer = 0;
    //     assertTrue("outDegree test 2 returned incorrect value", ni.outDegree("E") == answer);
    // }

    //
    @Test(timeout = 10000)
    public void shortestPath1() throws Exception {
        p = 3;
        NetworkInfluence ni = new NetworkInfluence("inf_graphs/short-dist-1.txt");
        List<String> answer = new ArrayList<>(4);
        answer.add("A");
        answer.add("B");
        answer.add("C");
        answer.add("D");
        List<String> result = ni.shortestPath("A", "D");
        assertTrue("shortestPath test 1 returned incorrect value", strListEquals(result, answer));
        shortestPathCorrectness += p;
    }

    //
    @Test(timeout = 10000)
    public void shortestPath2() throws Exception {
        p = 3;
        NetworkInfluence ni = new NetworkInfluence("inf_graphs/short-dist-2.txt");
        List<String> answer1 = new ArrayList<>(4);
        answer1.add("A");
        answer1.add("B");
        answer1.add("D");
        answer1.add("F");
        List<String> answer2 = new ArrayList<>(4);
        answer2.add("A");
        answer2.add("C");
        answer2.add("E");
        answer2.add("F");
        List<String> result = ni.shortestPath("A", "F");
        assertTrue("shortestPath test 2 returned incorrect value",
                strListEquals(result, answer1) || strListEquals(result, answer2));
        shortestPathCorrectness += p;
    }

    //
    @Test(timeout = 10000)
    public void shortestPath3() throws Exception {
        p = 4;
        NetworkInfluence ni = new NetworkInfluence("inf_graphs/short-dist-3.txt");
        List<String> answer = new ArrayList<>(4);
        answer.add("A");
        answer.add("F");
        answer.add("G");
        answer.add("E");
        List<String> result = ni.shortestPath("A", "E");
        assertTrue("shortestPath test 3 returned incorrect value", strListEquals(result, answer));
        shortestPathCorrectness += p;
    }

    //
    @Test(timeout = 10000)
    public void distance1() throws Exception {
        p = 3;
        NetworkInfluence ni = new NetworkInfluence("inf_graphs/short-dist-1.txt");
        int answer = 3;
        int result = ni.distance("A", "D");
        assertTrue("distance test 1 returned incorrect value", result == answer);
    }

    //
    @Test(timeout = 10000)
    public void distance2() throws Exception {
        p = 3;
        NetworkInfluence ni = new NetworkInfluence("inf_graphs/short-dist-2.txt");
        int answer = 3;
        int result = ni.distance("A", "F");
        assertTrue("distance test 2 returned incorrect value", result == answer);
    }

    //
    @Test(timeout = 10000)
    public void distance3() throws Exception {
        p = 4;
        NetworkInfluence ni = new NetworkInfluence("inf_graphs/short-dist-3.txt");
        int answer = 3;
        int result = ni.distance("A", "E");
        assertTrue("distance test 3 returned incorrect value", result == answer);
    }

    //
    @Test(timeout = 10000)
    public void distanceSet1() throws Exception {
        p = 6;
        NetworkInfluence ni = new NetworkInfluence("inf_graphs/short-dist-4.txt");
        int answer = 3;
        ArrayList<String> set = new ArrayList<>(3);
        set.add("A");
        set.add("B");
        set.add("C");
        int result = ni.distance(set, "T");
        assertTrue("distanceSet test 1 returned incorrect value", result == answer);
        distanceSetCorrectness += p;
    }

    //
    @Test(timeout = 10000)
    public void distanceSet2() throws Exception {
        p = 6;
        NetworkInfluence ni = new NetworkInfluence("inf_graphs/short-dist-5.txt");
        int answer = 3;
        ArrayList<String> set = new ArrayList<>(3);
        set.add("A");
        set.add("B");
        set.add("C");
        int result = ni.distance(set, "T");
        assertTrue("distanceSet test 2 returned incorrect value", result == answer);
        distanceSetCorrectness += p;
    }

    //
    @Test(timeout = 10000)
    public void distanceSet3() throws Exception {
        p = 8;
        NetworkInfluence ni = new NetworkInfluence("inf_graphs/short-dist-6.txt");
        int answer = -1;
        ArrayList<String> set = new ArrayList<>(3);
        set.add("A");
        set.add("B");
        set.add("C");
        int result = ni.distance(set, "T");
        assertTrue("distanceSet test 3 returned incorrect value", result <= answer);
        distanceSetCorrectness += p;
    }

    boolean setEquality(ArrayList<String> a, ArrayList<String> b) {
        return a.containsAll(b) && b.containsAll(a);
    }

    // // ================================================================
    // // TEST INFLUENCE SETS
    // // ================================================================

    private boolean influenceOkay(float A, float B) {
        float tolerance = 0.01f;
        float diff = A - B;
        if (Math.abs(diff) > tolerance)
            return false;
        else
            return true;
    }

    @Test(timeout = 20000)
    public void inf1() throws Exception {
        p = 10;

        float answer = 7.0f;
        float theirs = new NetworkInfluence("inf_graphs/inf1.txt").influence("S");

        assertTrue("Influence test 1 failed.", influenceOkay(answer, theirs) || influenceOkay(answer, theirs + 1.0f));
        influenceCorrectness += p;
    }

    @Test(timeout = 20000)
    public void inf2() throws Exception {
        p = 10;

        float answer = 3.75f;
        float theirs = new NetworkInfluence("inf_graphs/inf2.txt").influence("S");

        assertTrue("Influence test 2 failed.", influenceOkay(answer, theirs) || influenceOkay(answer, theirs + 1.0f));
        influenceCorrectness += p;
    }

    @Test(timeout = 20000)
    public void infSet1() throws Exception {
        p = 4;

        float answer = 7.00f;
        ArrayList<String> l = new ArrayList<>();
        l.add("X2");
        l.add("X1");
        float theirs = new NetworkInfluence("inf_graphs/infSet1.txt").influence(l);

        assertTrue("Influence Set test 1 failed.",
                influenceOkay(answer, theirs) || influenceOkay(answer, theirs + 2.0f));
        influenceSetCorrectness += p;
    }

    @Test(timeout = 20000)
    public void infSet2() throws Exception {
        p = 4;

        float answer = 7.0f;
        ArrayList<String> l = new ArrayList<>();
        l.add("X1");
        l.add("X2");
        float theirs = new NetworkInfluence("inf_graphs/infSet2.txt").influence(l);

        assertTrue("Influence Set test 2 failed.",
                influenceOkay(answer, theirs) || influenceOkay(answer, theirs + 2.0f));
        influenceSetCorrectness += p;
    }

    @Test(timeout = 20000)
    public void infSet3() throws Exception {
        p = 4;

        float answer = 4.0f;
        ArrayList<String> l = new ArrayList<>();
        l.add("X2");
        l.add("X1");
        float theirs = new NetworkInfluence("inf_graphs/infSet3.txt").influence(l);

        assertTrue("Influence Set test 3 failed.",
                influenceOkay(answer, theirs) || influenceOkay(answer, theirs + 2.0f));
        influenceSetCorrectness += p;
    }

    @Test(timeout = 20000)
    public void infSet4() throws Exception {
        p = 4;

        float answer = 4.125f;
        ArrayList<String> l = new ArrayList<>();
        l.add("X2");
        l.add("X1");
        float theirs = new NetworkInfluence("inf_graphs/infSet4.txt").influence(l);

        assertTrue("Influence Set test 4 failed.",
                influenceOkay(answer, theirs) || influenceOkay(answer, theirs + 2.0f));
        influenceSetCorrectness += p;
    }

    @Test(timeout = 20000)
    public void infSet5() throws Exception {
        p = 4;

        float answer = 4.25f;
        ArrayList<String> l = new ArrayList<>();
        l.add("X1");
        l.add("X2");
        float theirs = new NetworkInfluence("inf_graphs/infSet5.txt").influence(l);

        assertTrue("Influence Set test 5 failed.",
                influenceOkay(answer, theirs) || influenceOkay(answer, theirs + 2.0f));
        influenceSetCorrectness += p;
    }

    void testMostInfDeg(String path, ArrayList<String> vertices, int points) throws Exception {
        p = points;

        NetworkInfluence n = new NetworkInfluence("inf_graphs/" + path);
        assertTrue("mostInfluentialDegree returns incorrect set",
                setEquality(n.mostInfluentialDegree(vertices.size()), vertices));
    }

    @Test(timeout = 20000)
    public void mostInfDeg0() throws Exception {
        ArrayList<String> vertices = new ArrayList<String>();
        vertices.add("A");
        vertices.add("B");
        vertices.add("C");
        testMostInfDeg("mostInfDeg0", vertices, 5);
    }

    @Test(timeout = 20000)
    public void mostInfDeg1() throws Exception {
        ArrayList<String> vertices = new ArrayList<String>();
        vertices.add("A");
        vertices.add("B");
        testMostInfDeg("mostInfDeg1", vertices, 5);
    }

    void testMostInfMod(String path, ArrayList<String> vertices, int points) throws Exception {
        p = points;

        NetworkInfluence n = new NetworkInfluence("inf_graphs/" + path);
        assertTrue("mostInfluentialModular returns incorrect set",
                setEquality(n.mostInfluentialModular(vertices.size()), vertices));
    }

    @Test(timeout = 20000)
    public void mostInfMod0() throws Exception {
        ArrayList<String> vertices = new ArrayList<String>();
        vertices.add("A");
        vertices.add("B");
        testMostInfMod("mostInfMod0", vertices, 10);
    }

    @Test(timeout = 20000)
    public void mostInfMod1() throws Exception {
        ArrayList<String> vertices = new ArrayList<String>();
        vertices.add("A");
        vertices.add("B");
        testMostInfMod("mostInfMod1", vertices, 5);
    }

    @Test(timeout = 20000)
    public void mostInfMod2() throws Exception {
        ArrayList<String> vertices = new ArrayList<String>();
        vertices.add("A");
        vertices.add("B");
        vertices.add("C");
        testMostInfMod("mostInfMod2", vertices, 5);
    }

    void testMostInfSubMod(String path, ArrayList<String> vertices, int points) throws Exception {
        p = points;

        NetworkInfluence n = new NetworkInfluence("inf_graphs/" + path);
        assertTrue("mostInfluentialSubModular returns incorrect set",
                setEquality(n.mostInfluentialSubModular(vertices.size()), vertices));
    }

    @Test(timeout = 20000)
    public void mostInfSubMod0() throws Exception {
        ArrayList<String> vertices = new ArrayList<String>();
        vertices.add("A");
        vertices.add("C");
        testMostInfSubMod("mostInfSubMod0", vertices, 10);
    }

    @Test(timeout = 20000)
    public void mostInfSubMod1() throws Exception {
        ArrayList<String> vertices = new ArrayList<String>();
        vertices.add("A");
        vertices.add("C");
        testMostInfSubMod("mostInfSubMod1", vertices, 5);
    }

    @Test(timeout = 20000)
    public void mostInfSubMod2() throws Exception {
        ArrayList<String> vertices = new ArrayList<String>();
        vertices.add("A");
        vertices.add("C");
        testMostInfSubMod("mostInfSubMod2", vertices, 5);
    }

    // ================================================================
    // EFFICIENCY TEST DOUBLING
    // ================================================================

    // We have to call each test twice; otherwise Java's JIT compilation affects the results
    // Note that each test overwrites the appropriate timing variable, so only the last-run test counts

    // @Test(timeout = 30000)
    // public void testShortestPathEfficiencyA() throws Exception {
    //     testShortestPathEfficiency();
    // }

    // @Test(timeout = 30000)
    // public void testShortestPathEfficiencyRefA() throws Exception {
    //     testShortestPathEfficiencyRef();
    // }

    // @Test(timeout = 30000)
    // public void testShortestPathEfficiencyB() throws Exception {
    //     testShortestPathEfficiency();
    // }

    // @Test(timeout = 30000)
    // public void testShortestPathEfficiencyRefB() throws Exception {
    //     testShortestPathEfficiencyRef();
    // }

    // @Test(timeout = 30000)
    // public void testDistanceSetEfficiencyA() throws Exception {
    //     testDistanceSetEfficiency();
    // }

    // @Test(timeout = 30000)
    // public void testDistanceSetEfficiencyRefA() throws Exception {
    //     testDistanceSetEfficiencyRef();
    // }

    // @Test(timeout = 30000)
    // public void testDistanceSetEfficiencyB() throws Exception {
    //     testDistanceSetEfficiency();
    // }

    // @Test(timeout = 30000)
    // public void testDistanceSetEfficiencyRefB() throws Exception {
    //     testDistanceSetEfficiencyRef();
    // }

    // @Test(timeout = 30000)
    // public void testInfluenceEfficiencyA() throws Exception {
    //     testInfluenceEfficiency();
    // }

    // @Test(timeout = 30000)
    // public void testInfluenceEfficiencyRefA() throws Exception {
    //     testInfluenceEfficiencyRef();
    // }

    // @Test(timeout = 30000)
    // public void testInfluenceEfficiencyB() throws Exception {
    //     testInfluenceEfficiency();
    // }

    // @Test(timeout = 30000)
    // public void testInfluenceEfficiencyRefB() throws Exception {
    //     testInfluenceEfficiencyRef();
    // }

    // @Test(timeout = 30000)
    // public void testInfluenceSetEfficiencyA() throws Exception {
    //     testInfluenceSetEfficiency();
    // }

    // @Test(timeout = 30000)
    // public void testInfluenceSetEfficiencyRefA() throws Exception {
    //     testInfluenceSetEfficiencyRef();
    // }

    // @Test(timeout = 30000)
    // public void testInfluenceSetEfficiencyB() throws Exception {
    //     testInfluenceSetEfficiency();
    // }

    // @Test(timeout = 30000)
    // public void testInfluenceSetEfficiencyRefB() throws Exception {
    //     testInfluenceSetEfficiencyRef();
    // }



    // // ================================================================
    // // TEST SHORTESTPATH EFFICIENCY
    // // ================================================================

    // public void testShortestPathEfficiency() throws Exception {
    //     p = 0;

    //     long startTime = System.nanoTime();

    //     NetworkInfluence n = new NetworkInfluence("eff_graphs/effGraph");
    //     int count = 0;
    //     for (String u : effVertices) {
    //         for (String v : effVertices) {
    //             ArrayList<String> path = n.shortestPath(u, v);
    //             count++;
    //         }
    //         if (count >= shortestPathIts)
    //             break;
    //     }

    //     shortestPathDelta = System.nanoTime() - startTime;
    // }

    // public void testShortestPathEfficiencyRef() throws Exception {
    //     p = 0;

    //     long startTime = System.nanoTime();

    //     ref.NetworkInfluence n = new ref.NetworkInfluence("eff_graphs/effGraph");
    //     int count = 0;
    //     for (String u : effVertices) {
    //         for (String v : effVertices) {
    //             ArrayList<String> path = n.shortestPath(u, v);
    //             count++;
    //         }
    //         if (count >= shortestPathIts)
    //             break;
    //     }

    //     shortestPathDeltaRef = System.nanoTime() - startTime;
    // }

    // // ================================================================
    // // TEST DISTANCE SET EFFICIENCY
    // // ================================================================

    // public void testDistanceSetEfficiency() throws Exception {
    //     p = 0;

    //     long startTime = System.nanoTime();

    //     NetworkInfluence n = new NetworkInfluence("eff_graphs/effGraph");
    //     for (String v : effVertices) {
    //         int distance = n.distance(effVertexSet, v);
    //     }

    //     distanceSetDelta = System.nanoTime() - startTime;
    // }

    // public void testDistanceSetEfficiencyRef() throws Exception {
    //     p = 0;

    //     long startTime = System.nanoTime();

    //     ref.NetworkInfluence n = new ref.NetworkInfluence("eff_graphs/effGraph");
    //     for (String v : effVertices) {
    //         int distance = n.distance(effVertexSet, v);
    //     }

    //     distanceSetDeltaRef = System.nanoTime() - startTime;
    // }

    // // ================================================================
    // // TEST INFLUENCE EFFICIENCY
    // // ================================================================

    // public void testInfluenceEfficiency() throws Exception {
    //     p = 0;

    //     long startTime = System.nanoTime();

    //     for (int i = 0; i < influenceIts; i++) {
    //         NetworkInfluence n = new NetworkInfluence("eff_graphs/effGraph");
    //         for (String v : effVertices) {
    //             float influence = n.influence(v);
    //         }
    //     }

    //     influenceDelta = System.nanoTime() - startTime;
    // }

    // public void testInfluenceEfficiencyRef() throws Exception {
    //     p = 0;

    //     long startTime = System.nanoTime();

    //     for (int i = 0; i < influenceIts; i++) {
    //         ref.NetworkInfluence n = new ref.NetworkInfluence("eff_graphs/effGraph");
    //         for (String v : effVertices) {
    //             float influence = n.influence(v);
    //         }
    //     }

    //     influenceDeltaRef = System.nanoTime() - startTime;
    // }

    // // ================================================================
    // // TEST INFLUENCE SET EFFICIENCY
    // // ================================================================

    // public void testInfluenceSetEfficiency() throws Exception {
    //     p = 0;

    //     long startTime = System.nanoTime();

    //     for (int i = 0; i < influenceIts; i++) {
    //         NetworkInfluence n = new NetworkInfluence("eff_graphs/effGraph");
    //         float influence = n.influence(effVertexSet);
    //     }

    //     influenceSetDelta = System.nanoTime() - startTime;
    // }

    // public void testInfluenceSetEfficiencyRef() throws Exception {
    //     p = 0;

    //     long startTime = System.nanoTime();

    //     for (int i = 0; i < influenceIts; i++) {
    //         ref.NetworkInfluence n = new ref.NetworkInfluence("eff_graphs/effGraph");
    //         float influence = n.influence(effVertexSet);
    //     }

    //     influenceSetDeltaRef = System.nanoTime() - startTime;
    // }

    // ================================================================
    // WikiCrawler tests
    // ================================================================

    @Test
    public void crawlerTest1() throws Exception {
        p = 6;
        int testnum = 1;

        testCrawlerGraph(testnum, CRAWLERSEEDS[testnum - 1], 3, new ArrayList<>(0));
    }

    @Test(timeout = 300000)
    public void crawlerTest2() throws Exception {
        p = 6;
        int testnum = 2;

        testCrawlerGraph(testnum, CRAWLERSEEDS[testnum - 1], 3, new ArrayList<>(0));
    }

    @Test(timeout = 300000)
    public void crawlerTest3() throws Exception {
        p = 6;
        int testnum = 3;

        testCrawlerGraph(testnum, CRAWLERSEEDS[testnum - 1], 3, new ArrayList<>(0));
    }

    @Test(timeout = 300000)
    public void crawlerTest4() throws Exception {
        p = 6;
        int testnum = 4;

        testCrawlerGraph(testnum, CRAWLERSEEDS[testnum - 1], 3, new ArrayList<>(0));
    }

    @Test(timeout = 300000)
    public void crawlerTest5() throws Exception {
        p = 7;
        int testnum = 5;

        testCrawlerGraph(testnum, CRAWLERSEEDS[testnum - 1], 3, new ArrayList<>(0));
    }

    @Test(timeout = 300000)
    public void crawlerTest6() throws Exception {
        p = 7;
        int testnum = 6;

        testCrawlerGraph(testnum, CRAWLERSEEDS[testnum - 1], 3, new ArrayList<>(0));
    }

    @Test(timeout = 300000)
    public void crawlerTest7() throws Exception {
        p = 7;
        int testnum = 7;

        testCrawlerGraph(testnum, CRAWLERSEEDS[testnum - 1], 3, new ArrayList<>(0));
    }

    @Test(timeout = 300000)
    public void crawlerTest8() throws Exception {
        p = 7;
        int testnum = 8;

        testCrawlerGraph(testnum, CRAWLERSEEDS[testnum - 1], 3, new ArrayList<>(0));
    }

    @Test(timeout = 300000)
    public void crawlerTest9() throws Exception {
        p = 7;
        int testnum = 9;

        testCrawlerGraph(testnum, CRAWLERSEEDS[testnum - 1], 3, new ArrayList<>(0));
    }

    @Test(timeout = 300000)
    public void crawlerTest10() throws Exception {
        p = 7;
        int testnum = 10;

        testCrawlerGraph(testnum, CRAWLERSEEDS[testnum - 1], 3, new ArrayList<>(0));
    }

    /*
    @Test public void crawlerTest11() throws Exception
    {
        p = 6;
        int testnum = 11;
    
        testCrawlerGraph(testnum, CRAWLERSEEDS[testnum-1], 3, new ArrayList<>(0));
    }
    */

    @Test(timeout = 300000)
    public void crawlerTest12() throws Exception {
        p = 13;
        int testnum = 12;

        testCrawlerGraph(testnum, CRAWLERSEEDS[testnum - 1], 3, new ArrayList<>(0));
    }

    @Test(timeout = 300000)
    public void crawlerTest13() throws Exception {
        p = 14;
        int testnum = 13;

        testCrawlerGraph(testnum, CRAWLERSEEDS[testnum - 1], 3, new ArrayList<>(0));
    }

    @Test(timeout = 300000)
    public void crawlerTest14() throws Exception {
        p = 14;
        int testnum = 14;

        testCrawlerGraph(testnum, CRAWLERSEEDS[testnum - 1], 3, new ArrayList<>(0));
    }

    @Test(timeout = 300000)
    public void crawlerTest15() throws Exception {
        p = 14;
        int testnum = 15;

        testCrawlerGraph(testnum, CRAWLERSEEDS[testnum - 1], 4, new ArrayList<>(0));
    }

    @Test(timeout = 300000)
    public void crawlerTest16() throws Exception {
        p = 39;
        int testnum = 16;

        ArrayList<String> l = new ArrayList<>(2);
        l.add("trent");
        l.add("hugh");
        testCrawlerGraph(testnum, CRAWLERSEEDS[testnum - 1], 4, l);
    }

    @Test(timeout = 300000)
    public void crawlerTest17() throws Exception {
        p = 40;
        int testnum = 17;

        ArrayList<String> l = new ArrayList<>(2);
        l.add("trent");
        l.add("hugh");
        testCrawlerGraph(testnum, CRAWLERSEEDS[testnum - 1], 4, l);
    }

    private void testCrawlerGraph(int testnum, String seed, int max, ArrayList<String> topics) throws Exception {
        Path path = Paths.get(OUTPUTFILE);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {

        }
        WikiCrawler wc = new WikiCrawler(seed, max, topics, OUTPUTFILE);
        wc.crawl();
        List<Pair<String, String>> theirs = parseGraph(OUTPUTFILE);
        double js = jaccardSimilarity(crawlerEdges[testnum - 1], theirs);
        if (js < 1)
            partial = (int) (p * (js / 2));
        assertTrue("Incorrect output from wiki crawler test " + testnum + ".", js == 1);
    }

    // Helpers.
    private static double jaccardSimilarity(Collection x, Collection y) {
        Set a = new HashSet(x);
        Set b = new HashSet(y);
        Set c = new HashSet();
        c.addAll(a);
        c.addAll(b);
        double unionSize = c.size();
        c.retainAll(a);
        c.retainAll(b);
        double intersectionSize = c.size();
        return intersectionSize / unionSize;
    }

    private static List<Pair<String, String>> parseGraph(String graphData) throws Exception {
        Path path = Paths.get(graphData);

        List<Pair<String, String>> edges;
        try {
            // Read in file.
            List<String> lines = Files.readAllLines(path);

            // Get the alleged number of vertices and initialize.
            int numVerts = Integer.parseInt(lines.get(0));
            edges = new ArrayList<>(numVerts);

            // Loop through lines in the file.
            for (int i = 1; i < lines.size(); i++) {
                String[] verts = lines.get(i).split(" ");
                if (verts.length < 2) {
                    System.out.println("Ignoring erroneous line in graph output.");
                    continue;
                }
                Pair<String, String> edge = new Pair<>(verts[0], verts[1]);
                edges.add(edge);
            }
        } catch (Exception e) {
            if (graphData.equalsIgnoreCase(OUTPUTFILE))
                throw new Exception("Failed to parse user's output graph.");
            System.out.println("Exception reading or parsing file (this probably"
                    + " happened during setup for tests and should be investigated): " + e.getMessage());
            edges = new ArrayList<>(0);
        }

        return edges;
    }

    private static class Pair<A, B> {
        private A a;
        private B b;

        public Pair(A a, B b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Pair<?, ?> pair = (Pair<?, ?>) o;
            return Objects.equals(a, pair.a) && Objects.equals(b, pair.b);
        }

        @Override
        public int hashCode() {

            return Objects.hash(a, b);
        }
    }

}
