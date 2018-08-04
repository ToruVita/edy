package cc31;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;

import codeClone01.CPDWrapper;

public class ATest {

	@Test
	public void testGetData() {
		A a = new A();
		assertEquals(10, a.getData());
	}
	@Test
	public void testGetData2() {
		B b = new B();
		assertEquals(20, b.getData());
	}

	@Test
	public void testA() {
		A a = new A();
		assertEquals(10, a.data);
	}
	@Test
	public void testB() {
		B b = new B();
		assertEquals(20, b.data);
	}

	@Test
	public void testGetData3() {
		B b = new B();
		b.data = 30;
		assertEquals(30, b.getData());
	}
	/**
	 * CPD check
	 */
	@Test
	public void testCPD() {
		CPDWrapper cw = new CPDWrapper();
		cw.setMinimumTileSize(7);
		cw.setTargetClass(this.getClass());
		cw.cpd();
		assertThat("Code Clones are found.", cw.matches, cw.hasClone(0));
	}

	/**
	 * Length check
	 */
	@Test
	public void testLength() {
		CPDWrapper cw = new CPDWrapper();
		cw.setMinimumTileSize(3);
		cw.setTargetClass(this.getClass());
		cw.cpd();
		assertThat("Make code shorter.", cw.getTotalLineOfCode(), new BaseMatcher(){
			int limit = 50;
			@Override
			public boolean matches(Object arg0) {
				return (limit>(int)arg0);
			}
			@Override
			public void describeTo(Description description) {
				description.appendText("shorter code less than "+limit);
			}
		});
	}
}