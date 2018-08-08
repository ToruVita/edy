import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.Patch;

public class Test {
	String s1 = "abcdef\nghijklmn";
	String s2 = "abcdEf\nghijklmn";
	public Test() {
		try {
			Patch pch = DiffUtils.diff(s1, s2);
			System.out.println(pch.toString());
		} catch (DiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		new Test();
	}
}
