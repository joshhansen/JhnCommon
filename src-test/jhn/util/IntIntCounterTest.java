package jhn.util;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class IntIntCounterTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		IntIntCounter c = new IntIntCounter();
		
		c.set(1, 10);
		c.set(2, 5);
		c.set(3, 50);
		c.set(4, 25);
		c.set(5, 0);
		
		List<Entry<Integer,Integer>> top10 = c.topN(10);
		assertEquals(top10.size(), 5);
		assertEquals(top10.get(0).getKey().intValue(), 3);
		assertEquals(top10.get(1).getKey().intValue(), 4);
		assertEquals(top10.get(2).getKey().intValue(), 1);
		assertEquals(top10.get(3).getKey().intValue(), 2);
		assertEquals(top10.get(4).getKey().intValue(), 5);
		
		assertEquals(top10.get(0).getValue().intValue(), 50);
		assertEquals(top10.get(1).getValue().intValue(), 25);
		assertEquals(top10.get(2).getValue().intValue(), 10);
		assertEquals(top10.get(3).getValue().intValue(), 5);
		assertEquals(top10.get(4).getValue().intValue(), 0);
		
		List<Entry<Integer,Integer>> top0 = c.topN(0);
		assertEquals(top0.size(), 0);
		
		List<Entry<Integer,Integer>> top1 = c.topN(1);
		assertEquals(top1.size(), 1);
		
		List<Entry<Integer,Integer>> top3 = c.topN(3);
		assertEquals(top3.size(), 3);
		
		List<Entry<Integer,Integer>> top5 = c.topN(5);
		assertEquals(top5.size(), 5);
	}

}
