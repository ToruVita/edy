package jplas.codeClone;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.cpd.CPDConfiguration;
import net.sourceforge.pmd.cpd.JavaLanguage;
import net.sourceforge.pmd.cpd.Match;
import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.Tokens;

public class CPDWrapper{
	public CPDConfiguration cpdConf;
	private CPD cpd;
	public Iterator<Match> matches;
	Tokenizer tokenizer;
	private int totalLineOfCode;

	public CPDWrapper(){
		cpdConf = new CPDConfiguration();
		JavaLanguage java = new JavaLanguage();
		cpdConf.setLanguage(java);
		cpd = new CPD(cpdConf);
		tokenizer = java.getTokenizer();
	}
	/**
	 * count tokens in each source and sum them
	 */
	private void countUpTokens(){
		totalLineOfCode=0;
		for(SourceCode src : cpd.getSources()){
			Tokens tokens = new Tokens();
			try {
				tokenizer.tokenize(src, tokens);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			totalLineOfCode+=tokens.size();
		}
	}
	/**
	 * @return the tLOC
	 */
	public int getTotalLineOfCode() {
		countUpTokens();
		return totalLineOfCode;
	}
	public Matcher hasClone(int i) {
		return new BaseMatcher<Iterator<Match>>(){
			StringBuffer bf = new StringBuffer();
			@Override
			public boolean matches(Object item) {
				Iterator<Match> wk = (Iterator<Match>)item;
				int ct = 0;
				while(wk.hasNext()){
					ct++;
					Match m = wk.next();
					bf.append(m.toString());
					m.forEach(mk->{
						if(bf.length()>0 && !bf.toString().endsWith(System.lineSeparator())){
							bf.append(System.lineSeparator());
						}
						bf.append(mk.getFilename()+"("+mk.getBeginLine()+")");
					});
					if(bf.length()>0 && !bf.toString().endsWith(System.lineSeparator())){
						bf.append(System.lineSeparator());
					}
					bf.append(m.getSourceCodeSlice());
				}
				return ct == 0;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText(" TO DELETE CODE CLONES BELLOW EITHER\n");
				description.appendText(bf.toString());
			}
			
		};
	}

	public void cpd() {
		cpd.go();
		matches = cpd.getMatches();
	}
	public void setTargetClass(Class targetTestClass){
		setFolder(new File(new File("").getAbsolutePath()));
	}
	public void setFolder(File folder) {
		for(File f : folder.listFiles()){
			if(f.isFile() && f.getName().endsWith(".java") && !f.getName().endsWith("Test.java") && !f.getName().equals("CPDWrapper.java")){
				try {
					cpd.add(f);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else if(f.isDirectory()){
				setFolder(f);
			}
		}
	}

	public void setMinimumTileSize(int minimumTokens) {
		cpdConf.setMinimumTileSize(minimumTokens);
	}
}
