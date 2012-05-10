package jhn.trie;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StringShortTrieImplTest {
	private static final String[] keys = {"abc", "a", "ab", "bc", "def", "dabc", "dab"};
	private static final short[] values = {0, 1, -1, 14009, -22849, Short.MAX_VALUE, Short.MIN_VALUE+1};

	@Test
	public void testGetPutContainsKey() {
		StringShortTrie sst = new StringShortTrieImpl();
		
		for(String key : keys) {
			assertEquals(sst.get(key), StringShortTrie.defaultValue);
		}
		
		for(int i = 0; i < keys.length; i++) {
			sst.put(keys[i], values[i]);
			
			for(int j = 0; j < keys.length; j++) {
				if(j <= i) {
					assertTrue(sst.containsKey(keys[j]));
					assertEquals(sst.get(keys[j]), values[j]);
				} else {
					assertFalse(sst.containsKey(keys[j]));
					assertEquals(sst.get(keys[j]), StringShortTrie.defaultValue);
				}
			}
		}
		
		

	}
	
	@Test
	public void testIncrement() {
		StringShortTrie sst = new StringShortTrieImpl();
		for(int i = 0; i < 5; i++) {
			sst.put(keys[i], values[i]);
		}
		
		for(int i = 0; i < 5; i++) {
			sst.increment(keys[i], (short) i);
			assertEquals(sst.get(keys[i]), (short) (values[i]+i) );
		}
		
		// Incrementing a nonexistent key results in a count of 1
		sst.increment("newkey");
		assertEquals(sst.get("newkey"), (short) 1);
		
		// Incrementing a maxed-out key results in a count of Short.MAX_VALUE (rather than wrapping around)
		sst.put("newkey2", Short.MAX_VALUE);
		sst.increment("newkey2", (short) 30000);
		assertEquals(sst.get("newkey2"), Short.MAX_VALUE);
	}
}
