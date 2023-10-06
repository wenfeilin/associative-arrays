import java.io.PrintWriter;
import java.math.BigInteger;

import structures.AssociativeArray;
import structures.KeyNotFoundException;

/**
 * Experiments with our AssociativeArray class.
 *
 * @author Your Name Here
 * @author Samuel A. Rebelsky
 */
public class AssociativeArrayExperiments {

  // +------+--------------------------------------------------------
  // | Main |
  // +------+

  /**
   * Run the experiments.
   */
  public static void main(String[] args) {
    PrintWriter pen = new PrintWriter(System.out, true);

    divider(pen);
    expreimentStringsToStrings(pen);
    divider(pen);
    experimentBigIntToBigInt(pen);
    divider(pen);
  } // main(String[])

  // +-------------+-------------------------------------------------
  // | Experiments |
  // +-------------+

  /**
   * Our first experiment: Associative arrays with strings as both keys 
   * and values.
   */
  public static void expreimentStringsToStrings(PrintWriter pen) {
    AssociativeArray<String,String> s2s = 
      new ReportingAssociativeArray<String,String>("s2s", pen);
    s2s.size(); // 0
    s2s.set("a", "apple");
    s2s.set("A", "aardvark");
    s2s.size(); // 2
    s2s.hasKey("a"); // true
    s2s.hasKey("A"); // true
    try { s2s.get("a"); } catch (Exception e) { } // apple
    try { s2s.get("A"); } catch (Exception e) { } // aardvark
    s2s.remove("a");
    s2s.size(); // 1
    try { s2s.get("a"); } catch (Exception e) { } // exception

    //fix this line:
    try { s2s.get("A"); } catch (Exception e) { } // aardvark
    s2s.remove("aardvark"); // does nothing
    s2s.size(); // 1
    try { s2s.get("a"); } catch (Exception e) { } // exception
    try { s2s.get("A"); } catch (Exception e) { } // exception
  } // expreimentStringsToStrings

  /**
   * Our second experiment: Associative arrays with big integers as
   * keys and values.
   */
  public static void experimentBigIntToBigInt(PrintWriter pen) {
    AssociativeArray<BigInteger,BigInteger> b2b =
      new ReportingAssociativeArray<BigInteger,BigInteger>("b2b", pen);

    // Set some values
    for (int i = 0; i < 11; i++) { // 0-10
      b2b.set(BigInteger.valueOf(i), BigInteger.valueOf(i*i));
    } // for

    // Then get them
    for (int i = 0; i < 11; i++) { // 0-10
      try { b2b.get(BigInteger.valueOf(i)); } catch (Exception e) { }
    } // for

    // Then remove some of them
    for (int i = 1; i < 11; i += 2) { // removed 1, 3, 5, 7, 9
      b2b.remove(BigInteger.valueOf(i));
    } // for

    // Then see what happens when we get them
    for (int i = 0; i < 11; i++) { // only 0, 2, 4, 6, 8, 10 still work
      try { b2b.get(BigInteger.valueOf(i)); } catch (Exception e) { }
    } // for

    // Then reset or set some values
    for (int i = 0; i < 11; i += 3) { // 0 -> 10, 3 -> 13, 6 -> 16, 9 -> 19
      b2b.set(BigInteger.valueOf(i), BigInteger.valueOf(i + 10));
    } // for

    // Then see what happens when we get them
    for (int i = 0; i < 11; i++) { 
      // 0, 10; 1, exception; 2, 4; 3, 13; 4, 16; 5, exception; 6, 16; 7, exception; 
          // 8, 64; 9, 19; 10, 100
      try { b2b.get(BigInteger.valueOf(i)); } catch (Exception e) { }
    } // for
  } // experimentBigIntToBigInt

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Print a divider.
   */
  public static void divider(PrintWriter pen) {
    pen.println();
    pen.println("------------------------------------------------");
    pen.println();
  } // divider(PrintWriter)



} // AssociativeArrayExperiments