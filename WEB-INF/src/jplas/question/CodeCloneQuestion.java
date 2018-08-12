package jplas.question;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.hamcrest.Matcher;

import jplas.codeClone.CPDWrapper;

public class CodeCloneQuestion extends JPLASQuestion {
	File tempFolder;
	public CodeCloneQuestion(int qId) {
		super(qId);
		try {
			tempFolder = File.createTempFile("codeClone", String.format("%04d", qId));
			tempFolder.delete();
			tempFolder.mkdir();
			System.out.println("CREATE TEMP : "+tempFolder.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void appendSourceCode(String name, String code) {
		q.put(name, code);
		try {
			Files.write(Paths.get(tempFolder.getAbsolutePath(), name), code.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void generateQuest(int qId){
		CPDWrapper cpd = new CPDWrapper();
		cpd.setMinimumTileSize(7);
		cpd.setFolder(tempFolder);
		cpd.cpd();
		System.out.println(cpd.getTotalLineOfCode());
	}
	public void appendTestCode(String name, String code) {
		CPDWrapper cpd = new CPDWrapper();
		cpd.setMinimumTileSize(7);
		cpd.setFolder(tempFolder);
		cpd.getTotalLineOfCode();
		cpd.cpd();
		System.out.println(cpd.getTotalLineOfCode());
		q.put("totalTokens", ""+cpd.getTotalLineOfCode());
//		Matcher m = cpd.hasClone(0);
//		q.put(name, code);
	}

}
