package jplas.codeClone;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Test;

public class TeacherUnitTest {

	@Test
	public void test() {
		TeacherUnit tu = new TeacherUnit();
		try {
			File f = new File("C:\\Users\\NobIsh\\201805\\htmlT\\src\\htmlT\\HtmlFrame.java");
			InputStream in = new FileInputStream(f);
			tu.addIn(f.getName(), in);
			File file = tu.prepare();
			System.out.println(file.getAbsolutePath());
			System.out.println(tu.tnoc(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void test2() {
		TeacherUnit tu = new TeacherUnit();
		try {
			File f = new File("C:\\Users\\NobIsh\\201805\\htmlT\\src\\htmlT\\HtmlFrame.java");
			InputStream in = new FileInputStream(f);
			tu.addIn(f.getName(), in);
			File file = tu.prepare();
			System.out.println(file.getAbsolutePath());
			System.out.println(tu.toString(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
