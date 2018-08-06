package jplas.answer;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class JPLASAnswerTest {

	@Test
	public void testJPLASAnswer() {
		JPLASAnswer jpa = new JPLASAnswer();
		assertEquals("jplas.answer.JPLASAnswer", jpa.getClass().getName());
	}

	@Test
	public void testAddResourceFile() {
		JPLASAnswer jpa = new JPLASAnswer();
		try {
			jpa.addResource(new File("C:\\Users\\NobIsh\\AppData\\Local\\Temp\\20180806\\edy\\src\\jplas\\answer\\JPLASAnswer.java"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(true, jpa.resources.containsKey("JPLASAnswer.java"));
	}

	@Test
	public void testAddResourceStringInputStream() {
		JPLASAnswer jpa = new JPLASAnswer();
		try {
			File file = new File("C:\\Users\\NobIsh\\AppData\\Local\\Temp\\20180806\\edy\\src\\jplas\\answer\\JPLASAnswer.java");
			InputStream in = new FileInputStream(file);
			jpa.addResource("JPLASAnswer.java", in);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(true, jpa.resources.containsKey("JPLASAnswer.java"));
	}

	@Test
	public void testPrepareFolder() {
		JPLASAnswer jpa = new JPLASAnswer();
		File folder = null;
		try {
			File file = new File("C:\\Users\\NobIsh\\AppData\\Local\\Temp\\20180806\\edy\\src\\jplas\\answer\\JPLASAnswer.java");
			InputStream in = new FileInputStream(file);
			jpa.addResource("JPLASAnswer.java", in);
			folder = jpa.prepareFolder();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(true, folder.exists());
	}
	@Test
	public void testCompileAll() {
		JPLASAnswer jpa = new JPLASAnswer();
		String msg = "";
		try {
			File folder = File.createTempFile("jplas01", "000");
			folder.delete();
			folder.mkdir();
			File file = new File("C:\\Users\\NobIsh\\AppData\\Local\\Temp\\20180806\\edy\\src\\jplas\\answer\\JPLASAnswer.java");
			InputStream in = new FileInputStream(file);
			jpa.addResource("JPLASAnswer.java", in);
			msg = jpa.compileAll(folder);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(msg);
		assertEquals("", msg);
	}
	@Test
	public void testCompileAllwithFail() {
		JPLASAnswer jpa = new JPLASAnswer();
		String name = "X2.java";
		String msg = "";
		try {
			File folder = File.createTempFile("jplas01", "000");
			folder.delete();
			folder.mkdir();
			InputStream in = new ByteArrayInputStream("class A{\nint a\npublic static int a(){\n\n}\n}".getBytes());
			jpa.addResource(name, in);
			msg = jpa.compileAll(folder);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(msg);
		assertEquals(true, msg.contains(name+":"));
	}
	
	@Test
	public void testTest() {
		JPLASAnswer jpa = new JPLASAnswer();
		List <String> list = new ArrayList<String>();
		for(String s : "A.java,BTest.java,B.java".split(",")){
			File file = new File("C:\\Users\\NobIsh\\Dropbox\\Java\\sample.codes\\src\\cc31", s);
			try {
				jpa.addResource(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			list.add(s);
		}
		list.add("CPDWrapper.java");
		list.add("junit-4.12.jar");
		list.add("hamcrest-core-1.3.jar");
		list.add("commons-io-2.5.jar");
		list.add("jcommander-1.48.jar");
		list.add("pmd-core-6.5.0.jar");
		list.add("pmd-java-6.5.0.jar");
		jpa.completeList(list);
		String msg = "";
		File folder = null;
		try {
			folder = jpa.prepareFolder();
			msg = jpa.compile(folder);
			msg = jpa.test(folder);
			msg.contains("Code Clones are found.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(msg);
		assertEquals(true, folder.exists());
	}

}
