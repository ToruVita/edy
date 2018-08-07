package jplas.question;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

public class JPLASQuestionTest {

	@Test
	public void testJPLASQuestion() {
		JPLASQuestion jpq = new JPLASQuestion(1);
		assertEquals(1, jpq.qId);
	}

	@Test
	public void testList() {
		JPLASQuestion jpq = new JPLASQuestion(1);
		ArrayList <String> list = new ArrayList <String>();
		for(String s : jpq.list().split("\n")){
			list.add(s.trim());
		}
		assertEquals(4, list.size());
		System.out.println(list.contains("answer"));
	}

	@Test
	public void testQuest() {
		JPLASQuestion jpq = new JPLASQuestion(1);
		assertEquals("s", jpq.quest("statement"));
	}

}
