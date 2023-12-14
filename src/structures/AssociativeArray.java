package structures;

import static java.lang.reflect.Array.newInstance;

/**
 * A basic implementation of Associative Arrays with keys of type K
 * and values of type V. Associative Arrays store key/value pairs
 * and permit you to look up values by key.
 *
 * @author Wenfei Lin
 * @author Samuel A. Rebelsky
 */
public class AssociativeArray<K, V> {
  // +-----------+---------------------------------------------------
  // | Constants |
  // +-----------+

  /**
   * The default capacity of the initial array.
   */
  static final int DEFAULT_CAPACITY = 16;

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The size of the associative array (the number of key/value pairs).
   */
  int size;

  /**
   * The array of key/value pairs.
   */
  KVPair<K, V> pairs[];

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new, empty associative array.
   */
  @SuppressWarnings({ "unchecked" })
  public AssociativeArray() {
    // Creating new arrays is sometimes a PITN.
    this.pairs = (KVPair<K, V>[]) newInstance((new KVPair<K, V>()).getClass(),
        DEFAULT_CAPACITY);
    this.size = 0;
  } // AssociativeArray()

  // +------------------+--------------------------------------------
  // | Standard Methods |
  // +------------------+

  /**
   * Create a copy of this AssociativeArray.
   */
  public AssociativeArray<K, V> clone() {
    AssociativeArray<K,V> clonedAssociativeArray = 
        new AssociativeArray<K,V>();
    int numOfExpansions = this.pairs.length / DEFAULT_CAPACITY;

    // Expands the cloned array to match at least the 
    // size of the original array
    for (int i = 0; i < numOfExpansions; i++) {
      clonedAssociativeArray.expand();
    } // for

    for (int i = 0; i < this.pairs.length; i++) { 
      if (this.pairs[i] != null) {
        // As long as the entry is not null, create a new pair to insert in 
        // the cloned array at the same index currently in the original array,
        // and copy the key and value over to the new pair
        KVPair<K,V> newPair = (KVPair<K,V>) new KVPair<K,V>(null, null);
        newPair.key = this.pairs[i].key;
        newPair.value = this.pairs[i].value;
        clonedAssociativeArray.pairs[i] = newPair;
      } else {
        // Otherwise, you're looking at a null entry, so just set the spot in 
        // the cloned array to null 
        clonedAssociativeArray.pairs[i] = null;
      } // if/else
    } // for
    // Copy over the size
    clonedAssociativeArray.size = this.size;

    return clonedAssociativeArray;
  } // clone()

  /**
   * Convert the array to a string.
   */
  public String toString() {
    // Format: "{ key0: value0, key1: value1, ... keyn: valuen }"
    String keyValuePairs = "{"; 
    int numOfPairs = this.size;
    int pairsAdded = 0;
    int i = 0;

    // Only add information about key value pairs if the arr is not empty
    if (numOfPairs != 0) { 
      // Keep looping if not all pairs in arr have been seen
      while (pairsAdded != numOfPairs) { 
        if (this.pairs[i] != null) { 
          // If there is an non-null pair in the array,
          // start writing standard string format ( key: value)
          keyValuePairs += " " + this.pairs[i].key + ": " + 
              this.pairs[i].value;
          pairsAdded++;
        
          if (pairsAdded != numOfPairs) { 
            // If there are more pairs in the array, add a comma
            keyValuePairs += ",";
          } else { 
            // Spacing for after the commas 
            keyValuePairs += " ";
          } // if/else
        } // if
          i++;
      } // while
    } // if
    keyValuePairs += "}";
      
    return keyValuePairs;
  } // toString()

  // +----------------+----------------------------------------------
  // | Public Methods |
  // +----------------+

  /**
   * Set the value associated with key to value. Future calls to
   * get(key) will return value.
   * 
   * @param key key to be added or have its value replaced
   * @param value value to be added or to replace the previous value 
   */
  public void set(K key, V value) {
    int numOfPairs = this.size;

    if (numOfPairs == 0) {
      // If the array is empty, then add one element to the first index 
      KVPair<K,V> newPair = (KVPair<K,V>) new KVPair<K,V>(key, value);
      this.pairs[0] = newPair;
      this.size++;
    } else {
      // Determine if a new entry will be added (anywhere in the array that is
      // null) or if the value of the entry with the specified key will be 
      // replaced by a new value
      try {
        int keyIndex = this.find(key);
        // If no exception was caught from the line above, that means there 
        // already exists an entry with the specified key, so just update that
        // entry's value
        this.pairs[keyIndex].value = value;
      } catch (KeyNotFoundException knfe) {
        // If an exception was caught, then there isn't a current entry in 
        // the array with the specified key, so add a new entry to the array
        KVPair<K,V> newPair = (KVPair<K,V>) new KVPair<K,V>(key, value);

        // Check if associative array is full first
        if (numOfPairs == this.pairs.length) {
          int nextEntryIndex = numOfPairs;

          // Expand array, insert new pair in expanded array, and increase 
          // the size field of the expanded array
          this.expand();
          this.pairs[nextEntryIndex] = newPair; 
          this.size++; 
        } else {
          // Since associative array is not full
          int i = 0;

          // Insert new pair at the first null space in the array
          while (this.pairs[i] != null) {
            ++i;
          } // while
          this.pairs[i] = newPair;
          this.size++;
        } // if/else
      } // try/catch
    } // if/else
  } // set(K,V)

  /**
   * Get the value associated with key.
   *
   * @param key the key to reference for the value
   * @throws KeyNotFoundException
   *                              when the key does not appear in the 
   *                              associative array.
   */
  public V get(K key) throws KeyNotFoundException {
    // Find (if there it exists) the index of the pair with the specified key 
    // in the array
    int keyIndex = this.find(key);
    // Reference that key's value
    return this.pairs[keyIndex].value;
  } // get(K)

  /**
   * Determine if key appears in the associative array.
   * 
   * @param key the key to check if its in the associative array
   */
  public boolean hasKey(K key) {
    try {
      // If the key is found in the array, then return true
      this.find(key); 
      return true;
    } catch (KeyNotFoundException knfe) {
      // If the key cannot be found in the array, then prepare to 
      // catch the KeyNotFoundException it will throw
      
      // Return false to indicate there is no such key in the array
      return false;
    } // try/catch
  } // hasKey(K)

  /**
   * Remove the key/value pair associated with a key. Future calls
   * to get(key) will throw an exception. If the key does not appear
   * in the associative array, does nothing.
   * 
   * @param key the key (with its referenced value) to be removed 
   */
  public void remove(K key) {
    try {
      // This will replace the pair with null at the index where the key 
      // of the pair is found
      int keyIndex = this.find(key);
      this.pairs[keyIndex] = null;
      this.size--;
    } catch (KeyNotFoundException knfe) {
      // Does nothing since there is no entry with specified key
    } // try/catch
  } // remove(K)

  /**
   * Determine how many values are in the associative array.
   */
  public int size() {
    return this.size;
  } // size()

  // +-----------------+---------------------------------------------
  // | Private Methods |
  // +-----------------+

  /**
   * Expand the underlying array.
   */
  public void expand() {
    // Double the size of the current array
    this.pairs = java.util.Arrays.copyOf(this.pairs, this.pairs.length * 2);
  } // expand()

  /**
   * Find the index of the first entry in `pairs` that contains key.
   * If no such entry is found, throws an exception.
   * 
   * @param key the key to be found in the associative array 
   * @throws KeyNotFoundException
   *                              when the key does not appear in the 
   *                              associative array.
   */
  public int find(K key) throws KeyNotFoundException {
    int numOfPairs = this.size;

    if (numOfPairs == 0) {
      // If the array is empty, there is no key to be found
      throw new KeyNotFoundException();
    } // if

    // Otherwise, the array has entries in it
    int i = 0;
    int keyIndex = -1;
    int pairsTraversed = 0;

    // Keep traversing the array while the end has not been reached and 
    // the key has not been found
    while (keyIndex == -1 && pairsTraversed != numOfPairs) {
      // Only look at the entries in the array with a KVPair<K,V> to 
      // account for null keys
      if (this.pairs[i] instanceof KVPair<K, V>) {
        // To account for when the key is null:
        if (this.pairs[i].key == null) {
          if (key == null) {
            keyIndex = i;
          } // if
        } else if (this.pairs[i].key.equals(key)) {
          keyIndex = i;
        } // if/else
        ++pairsTraversed;
      } // if 
      ++i;
    } // while

    if (keyIndex != -1) { 
      // If the key was found, return the index at which it was found
      return keyIndex;
    } else { 
      // Otherwise, throw a KeyNotFoundException (the key doesn't exist)
      throw new KeyNotFoundException();
    } // if/else
  } // find(K)
} // class AssociativeArray