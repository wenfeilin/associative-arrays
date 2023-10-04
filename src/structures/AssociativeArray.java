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
    AssociativeArray<K,V> clonedAssociativeArray = new AssociativeArray<K,V>();
    clonedAssociativeArray.pairs = this.pairs;
    clonedAssociativeArray.size = this.size;

    return clonedAssociativeArray;
  } // clone()

  /**
   * Convert the array to a string.
   */
  public String toString() {
    //"{ key0: value0, key1: value1, ... keyn: valuen }"
    
    String keyValuePairs = "{";
    int numOfPairs = this.size;
    int pairsAdded = 0;
    int i = 0;

    if (numOfPairs != 0) { // only add information about key value pairs if the arr is not empty
      while (pairsAdded != numOfPairs) {
        if (this.pairs[i] != null) {
          keyValuePairs += " " + this.pairs[i].key + ": " + this.pairs[i].value;
          pairsAdded++;
        
          if (pairsAdded != numOfPairs) {
            keyValuePairs += ",";
          } else {
            keyValuePairs += " ";
          }
        }
          i++;
      }
    }
    keyValuePairs += "}";
      
    return keyValuePairs;
  } // toString()

  // +----------------+----------------------------------------------
  // | Public Methods |
  // +----------------+

  /**
   * Set the value associated with key to value. Future calls to
   * get(key) will return value.
   */
  public void set(K key, V value) {
    int numOfPairs = this.size;

    if (numOfPairs == 0) {
      // If the array is empty, then add one element to the first index 
      KVPair<K,V> newPair = (KVPair<K,V>) new KVPair<K,V>(key, value);
      this.pairs[0] = newPair;
      this.size++;
    } else if (numOfPairs == this.pairs.length) {
      // If the array is full, then expand the array before adding the new entry
      KVPair<K,V> newPair = (KVPair<K,V>) new KVPair<K,V>(key, value);
      int nextEntryIndex = numOfPairs;
      this.expand();
      this.pairs[nextEntryIndex] = newPair;
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
        // If an exception was caught, then there is current entry in the array
        // with the specified key, so add a new entry to the array
        KVPair<K,V> newPair = (KVPair<K,V>) new KVPair<K,V>(key, value);
        int i = 0;

        while (this.pairs[i] != null) {
          ++i;
        }
        this.pairs[i] = newPair;
        this.size++;
      }
    }
  } // set(K,V)

  /**
   * Get the value associated with key.
   *
   * @throws KeyNotFoundException
   *                              when the key does not appear in the associative
   *                              array.
   */
  public V get(K key) throws KeyNotFoundException {
    int keyIndex = this.find(key);
    return this.pairs[keyIndex].value;
  } // get(K)

  /**
   * Determine if key appears in the associative array.
   */
  public boolean hasKey(K key) {
    try {
      // If the key is found in the array, then return true
      this.find(key); 
      return true;
    } catch (KeyNotFoundException knfe) {
      // If the key cannot be found in the array, then prepare to 
      // catch the KeyNotFoundException it will throw
    } 
    // Return false to indicate there is no such key in the array
    return false;      
  } // hasKey(K)

  /**
   * Remove the key/value pair associated with a key. Future calls
   * to get(key) will throw an exception. If the key does not appear
   * in the associative array, does nothing.
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
    }
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
    this.pairs = java.util.Arrays.copyOf(this.pairs, this.pairs.length * 2);
  } // expand()

  /**
   * Find the index of the first entry in `pairs` that contains key.
   * If no such entry is found, throws an exception.
   */
  public int find(K key) throws KeyNotFoundException {
    int numOfPairs = this.size;
    if (numOfPairs == 0) {
      // If the array is empty, there is no key to be found
      throw new KeyNotFoundException();
    }
    // Otherwise, the array has entries in it
    int i = 0;
    int keyIndex = -1;
    int pairsTraversed = 0;

    // Keep traversing the array while the end has not been reached and 
    // the key has not been found!
    while (keyIndex == -1 && pairsTraversed != numOfPairs) {
      if (this.pairs[i] != null) {
        // Only look at the entries in the array (not null parts of the array)
        if (this.pairs[i].key == key) {
          // If the current entry's key matches the one being searched for,
          // save that entry's index
          keyIndex = i;
        }
        ++pairsTraversed;
      }
      ++i;
    }

    if (keyIndex != -1) {
      return keyIndex;
    } else {
      throw new KeyNotFoundException();
    }
  } // find(K)

} // class AssociativeArray