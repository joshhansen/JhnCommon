package jhn.trie;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StringTrieSetTest {
	private static final String[] strings = {"abc", "a", "ab", "bc", "def", "dabc", "dab"};
	private static final String[] nonStrings = {"d", "de", "abcd"};
	
	@Test
	public void test() {
		StringSet set = new StringTrieSet();
		
		for(int i = 0; i < strings.length; i++) {
			set.add(strings[i]);
			
			for(int j = 0; j < strings.length; j++) {
				if(j <= i) {
					assertTrue(set.contains(strings[j]));
				} else {
					assertFalse(set.contains(strings[j]));
				}
			}
			
			for(String non : nonStrings) {
				assertFalse(set.contains(non));
			}
		}
	}

}
