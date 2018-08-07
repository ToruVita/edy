package jplas.question;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

import jplas.codeClone.CPDWrapper;

public class CodeCloneQuestionTest {
	String sampleCode ="C:\\Users\\N.Ishihara\\Dropbox\\Java\\sample.codes\\src\\cc31";
	@Test
	public void testCodeCloneQuestion() {
		CodeCloneQuestion ccq = new CodeCloneQuestion(0);
		File file = ccq.tempFolder;
		assertEquals(true, file.exists());
	}

	@Test
	public void testAppendSourceCode() {
		String ajava = "A.java";
		CodeCloneQuestion ccq = new CodeCloneQuestion(0);
		try {
			byte[] bytes = Files.readAllBytes(Paths.get(sampleCode, ajava));
			ccq.appendSourceCode(ajava, new String(bytes));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File file = new File(ccq.tempFolder, ajava);
		assertEquals(true, file.exists());
	}
	@Test
	public void testGenerateQuest() {
		String ajava = "A.java";
		CodeCloneQuestion ccq = new CodeCloneQuestion(13);
		try {
			byte[] bytes = Files.readAllBytes(Paths.get(sampleCode, ajava));
			ccq.appendSourceCode(ajava, new String(bytes));
			ccq.generateQuest(32);

		} catch (IOException e) {
			e.printStackTrace();
		}
//		assertEquals("12", ccq.q.get("totalTokens"));
	}

	@Test
	public void testJPLASQuestion() {
		CodeCloneQuestion ccq = new CodeCloneQuestion(0);
		File file = ccq.tempFolder;
		assertEquals(true, file.exists());
	}

	@Test
	public void testList() {
		String ajava = "A.java";
		CodeCloneQuestion ccq = new CodeCloneQuestion(0);
		try {
			byte[] bytes = Files.readAllBytes(Paths.get(sampleCode, ajava));
			ccq.appendSourceCode(ajava, new String(bytes));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(ajava, ccq.list());
	}

	@Test
	public void testQuest() {
		fail("Not yet implemented");
	}

}
