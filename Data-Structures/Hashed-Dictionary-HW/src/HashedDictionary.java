import java.util.Iterator;
import java.util.NoSuchElementException;

public class HashedDictionary<K, V> implements DictionaryInterface<K, V> {
	private TableEntry<K, V>[] hashTable;
	private int numberOfEntries;
	private int locationsUsed; 
	private static final int DEFAULT_SIZE = 2477;
	private static final float MAX_LOAD_FACTOR = 0.5f;

	//performance monitoring
	private long avgTime;
	private long minTime;
	private long maxTime;
	private long collision;

	public long getAvgTime() {
		return avgTime;
	}

	public long getMinTime() {
		return minTime;
	}

	public long getMaxTime() {
		return maxTime;
	}

	public long getCollision() {
		return collision;
	}

	public HashedDictionary() {
		this(DEFAULT_SIZE); 
	}

	@SuppressWarnings("unchecked")
	public HashedDictionary(int tableSize) {
		this.collision = 0;
		this.avgTime =0;
		this.minTime = Long.MAX_VALUE;
		this.maxTime = Long.MIN_VALUE;

		int primeSize = getNextPrime(tableSize);
		hashTable = new TableEntry[primeSize];
		numberOfEntries = 0;
		locationsUsed = 0;
	}

	private boolean isPrime(int num) {
		/* Checks whether the number given is prime */
		for(int i = 2 ; i*i<=num; i++){
			if(num % i==0)
				return false;
		}
		return true;
	}

	private int getNextPrime(int num) {
		/* returns the next prime equals or bigger than the number given */
		for(int i = num; true ; i++) {
			if(isPrime(i))
				return i;
		}
	}

	public V add(K key, V value) {
		V oldValue; 
		if (isHashTableTooFull())
			rehash();
		
		int index = getHashIndex(key); //returns priority index
		index = probe(index, key); //returns first available index

		if ((hashTable[index] == null) || hashTable[index].isRemoved()) { 
			hashTable[index] = new TableEntry<K, V>(key, value);
			numberOfEntries++;
			locationsUsed++;
			oldValue = null;
		} else { 
			oldValue = hashTable[index].getValue();
			hashTable[index].setValue(value);
		} 
		return oldValue;
	}

	public V addCollision(K key, V value) {
		//not used in program. used for collision counting in the search txt
		V oldValue = null;
		if (isHashTableTooFull())
			rehash();

		int index = getHashIndex(key); //returns priority index
		index = probeCollision(index, key); //returns first available index

		if ((hashTable[index] == null) || hashTable[index].isRemoved()) {
			//hashTable[index] = new TableEntry<K, V>(key, value);
			//numberOfEntries++;
			//locationsUsed++;
			oldValue = null;
		} else {
			//oldValue = hashTable[index].getValue();
			//hashTable[index].setValue(value);
		}
		return oldValue;
	}


	private int SSF(K key) { //Returns hash code for given key according to Simple Summation Function
		int sumResult = 0;

		if(key instanceof String) {
			String text = (String) key;

			for(int i = 0 ; i<text.length(); i++){
				sumResult += text.charAt(i);
			}
			return sumResult;
		}else {
			int code = key.hashCode();
			if(code<0)
				code += hashTable.length;

			return code;
		}

	}

	private int PAF(K key) { //returns hash code for given key according to Polynomial Accumulation Function
		//
		int hashCode;
		if(key instanceof String) {
			hashCode = hornerRule((String) key,((String) key).length()-1);
		}else {
			hashCode = key.hashCode();
		}

		//the program has better results (lesser collision count) when overflow is ignored.
//
//		hashCode = hashCode%hashTable.length;
//
//		if(hashCode<0)
//			hashCode+= hashTable.length;

		return hashCode;
	}

	private int hornerRule(String word, int idx) {
		//Horner's rule for PAF function ()
		//returns hash code for given word only (no modulus operator)
		//overflows may occur. The program has better results in collision count when overflow is ignored in PAF.
		if(idx > 0) {
			return word.charAt(idx) + (31 * hornerRule(word, idx - 1));
			//(word.charAt(idx)%hashTable.length + (31 * hornerRule(word, idx - 1))%hashTable.length)%hashTable.length; can be used to find index directly without overflow
			// In my solution I prefer to ignore overflow. Because it has no effect on uniform distribution
		}
		return word.charAt(idx);
	}



	private int getHashIndex(K key) {
		int hashIndex = PAF(key) % hashTable.length;
		if (hashIndex < 0)
			hashIndex += hashTable.length;
		return hashIndex;
	}

	public boolean isHashTableTooFull() {
		float load_factor = (float) locationsUsed / hashTable.length;
		if (load_factor >= MAX_LOAD_FACTOR)
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	public void rehash() {
		TableEntry<K, V>[] oldTable = hashTable;
		int oldSize = hashTable.length;
		int newSize = getNextPrime(2 * oldSize);
		hashTable = new TableEntry[newSize]; 
		numberOfEntries = 0;  //it will be calculated again while re-adding
		locationsUsed = 0;

		//Rehash array from old to new hash table
		for (int index = 0; index < oldSize; index++) {
			if ((oldTable[index] != null) && oldTable[index].isIn())
				add(oldTable[index].getKey(), oldTable[index].getValue());
		}
	}

	private int probeCollision(int index, K key) {
		//used in performance monitoring only. copied from probe function
		//returns first available index.
		//if the given key exists does not insert to the table and store the count in value of entry
		// calculating hashCode here to prevent calculating it in while loop below
		//performance monitoring section (ctrl+f)
		int hashCode = PAF(key); //h1(key)
		int d2Code = dHashCode(hashCode); //d(k)


		boolean found = false;
		int removedStateIndex = -1;
		int j = 1;
		long start = System.nanoTime();
		while (!found && (hashTable[index] != null)) {
			if (hashTable[index].isIn()) {
				if (key.equals(hashTable[index].getKey())) {
					found = true; //value deki txt.name in countu burda artacak
				}
				else {
					this.collision++;
					index = (hashCode +  j*d2Code) % hashTable.length;
				}
			}
			else //to save removed entries in case the key does not found
			{
				if (removedStateIndex == -1)
					removedStateIndex = index;
				index = ( hashCode + j*d2Code )%hashTable.length;
			}
			if(index < 0)
				index+=hashTable.length;

			j=j+1;
		}
		long duration = System.nanoTime()-start;

		timeOpe(duration);

		if (found || (removedStateIndex == -1)) {

			return index;
		}
		else
			return removedStateIndex;
	}

	private void timeOpe(long duration) {
		if(minTime > duration)
			minTime=duration;
		if(maxTime<duration)
			maxTime = duration;
		this.avgTime+=duration;
	}

	private int probe(int index, K key) {
		//returns first available index.
		//if the given key exists does not insert to the table and store the count in value of entry

		// calculating hashCode here to prevent calculating it in while loop below
		int pafCode = PAF(key); //h1(key)
		int d2Code = dHashCode(pafCode); //d(k)


		boolean found = false;
		int removedStateIndex = -1;
		int j = 1;
		while (!found && (hashTable[index] != null)) {
			if (hashTable[index].isIn()) {
				if (key.equals(hashTable[index].getKey()))
					found = true; //value deki txt.name in countu burda artacak
				else {
					index = (pafCode + j * d2Code) % hashTable.length;
				}
			} 
			else //to save removed entries in case the key does not found
			{
				if (removedStateIndex == -1)
					removedStateIndex = index;
				index = ( pafCode + j*d2Code )%hashTable.length;
			}
			if(index < 0)
				index+=hashTable.length;

			j++;
		} 
		if (found || (removedStateIndex == -1))
			return index; 
		else
			return removedStateIndex; 
	}

	private int dHashCode(int pafCode) {
		return 31 - pafCode%31;
	}

	public V remove(K key) {
		V removedValue = null;
		int index = getHashIndex(key);
		index = locate(index, key);
		if (index != -1) { 
			removedValue = hashTable[index].getValue();
			hashTable[index].setToRemoved();
			numberOfEntries--;
		} 
		return removedValue;
	}

	//Follows the probe sequence that begins at index (key’s hash index) and returns either the index
	//of the entry containing key or -1, if no such entry exists.
	private int locate(int index, K key) {
		int pafCode = PAF(key); //h1(key)
		int d2Code = dHashCode(pafCode); //d(k)

		boolean found = false;
		int j = 1;
		while (!found && (hashTable[index] != null)) {
			if(hashTable[index].isIn() && key.equals(hashTable[index].getKey()))
				found = true;
			else {
				index = (pafCode + j * d2Code) % hashTable.length;
			}

			if(index < 0)
				index+=hashTable.length;
			j++;
		} 
		int result = -1;
		if (found)
			result = index;
		return result;
	}

	public V getValue(K key) {
		V result = null;
		int index = getHashIndex(key);
		index = locate(index, key);
		if (index != -1)
			result = hashTable[index].getValue(); 
		return result;
	}

	public boolean contains(K key) {
		int index = getHashIndex(key);
		index = locate(index, key);
		if (index != -1)
			return true;
		return false;
	}

	public boolean isEmpty() {
		return numberOfEntries == 0;
	}

	public int getSize() {
		return numberOfEntries;
	}

	public void clear() {
		while(getKeyIterator().hasNext()) {
			remove(getKeyIterator().next());		
		}
	}
	
	public Iterator<K> getKeyIterator() {
		return new KeyIterator();
	}

	public Iterator<V> getValueIterator() {
		return new ValueIterator();
	}

	private class TableEntry<S, T> {
		private S key;
		private T value;
		private boolean inTable;

		private TableEntry(S key, T value) {
			this.key = key;
			this.value = value;
			inTable = true;
		}

		private S getKey() {
			return key;
		}

		private T getValue() {
			return value;
		}

		private void setValue(T value) {
			this.value = value;
		}

		private boolean isRemoved() {
			return inTable == false;
		}

		private void setToRemoved() {
			inTable = false;
		}

		private void setToIn() {
			inTable = true;
		}

		private boolean isIn() {
			return inTable == true;
		}
	}

	private class KeyIterator implements Iterator<K> {
		private int currentIndex; 
		private int numberLeft; 

		private KeyIterator() {
			currentIndex = 0;
			numberLeft = numberOfEntries;
		} 

		public boolean hasNext() {
			return numberLeft > 0;
		} 

		public K next() {
			K result = null;
			if (hasNext()) {
				while ((hashTable[currentIndex] == null) || hashTable[currentIndex].isRemoved()) {
					currentIndex++;
				} 
				result = hashTable[currentIndex].getKey();
				numberLeft--;
				currentIndex++;
			} else
				throw new NoSuchElementException();
			return result;
		} 

		public void remove() {
			throw new UnsupportedOperationException();
		} 
	}
	
	private class ValueIterator implements Iterator<V> {
		private int currentIndex; 
		private int numberLeft; 

		private ValueIterator() {
			currentIndex = 0;
			numberLeft = numberOfEntries;
		} 

		public boolean hasNext() {
			return numberLeft > 0;
		} 

		public V next() {
			V result = null;
			if (hasNext()) {
				while ((hashTable[currentIndex] == null) || hashTable[currentIndex].isRemoved()) {
					currentIndex++;
				} 
				result = hashTable[currentIndex].getValue();
				numberLeft--;
				currentIndex++;
			} else
				throw new NoSuchElementException();
			return result;
		} 

		public void remove() {
			throw new UnsupportedOperationException();
		} 
	}
}
