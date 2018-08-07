package jplas.question;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class JPLASQuestion {
	int qId;
	int uId;
	HashMap <String, String> q = new HashMap <String, String>();
	public JPLASQuestion(int qId) {
		this.qId = qId;
		generateQuest(qId);
	}
	public String list(){
		StringBuffer bf = new StringBuffer();
		for(String s : q.keySet()){
			if(bf.length()>0)
				bf.append(System.lineSeparator());
			bf.append(s);
		}
		return bf.toString();
	}
	public String quest(String flg){
		return q.get(flg);
	}
	private void generateQuest(int qId){
		q.put("statement", "以下のコードA.javaとB.javaには，コードクローンと呼ばれるほぼ同じコード片が含まれる．\n（問題では黄色でマークアップされた部分）．\nこのコードクローンを除去し，全体のコード量を50エレメント未満に減量しなさい．\nHINT:クラスAをクラスBの親クラスとして継承を定義し，B内で定義されていたコードクローンを除去する．");
		q.put("A.java", "package cc31;\n\npublic class A {\n\tint data = 10;\n\tpublic int getData() {\n\t\treturn data;\n\t}\n\tpublic A() {\n\t\t// TODO Auto-generated constructor stub\n\t}\n\n}");
		q.put("B.java", "package cc31;\n\npublic class B {\n\tint data = 20;\n\tpublic int getData() {\n\t\treturn data;\n\t}\n\tpublic B() {\n\t\t// TODO Auto-generated constructor stub\n\t}\n\n}");
		q.put("answer", "class B extends A{\n\tB(){\n\t\tdata=20;\n\t}\n}\t}");
	}
	private void generateQuest(File file){
		try {
			String code = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
			q.put(file.getName(), code);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
