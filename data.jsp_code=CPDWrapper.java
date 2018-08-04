package codeClone01;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.cpd.CPDConfiguration;
import net.sourceforge.pmd.cpd.JavaLanguage;
import net.sourceforge.pmd.cpd.Match;
import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.Tokens;

/**
 * CPD wrapper class
 * @author n.ishihara
 *
 */
public class CPDWrapper{
	public CPDConfiguration cpdConf;
	private CPD cpd;
	public Iterator<Match> matches;
	Tokenizer tokenizer;
	private int totalLineOfCode;
	/**
	 * Constructor
	 * prepare configuration, set JAVA
	 * prepare cpd
	 */
	public CPDWrapper(){
		cpdConf = new CPDConfiguration();
		JavaLanguage java = new JavaLanguage();
		cpdConf.setLanguage(java);
		cpd = new CPD(cpdConf);
		tokenizer = java.getTokenizer();
//		cpd.getSources();
	}
	public File[] getTargetFiles(){
		List<String> filePaths = cpd.getSourcePaths();
		File[] files = new File[filePaths.size()];
		for(int i = 0; i < filePaths.size(); i++){
			files[i] = new File(filePaths.get(i));
		}
		return files;
	}
	public String toString(){
		StringBuffer bf = new StringBuffer();
		while(matches.hasNext()){
			Match m = matches.next();
			bf.append(m);
		}
		return bf.toString();
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
	/**
	 * append a target java file 
	 * @param targetFile
	 */
	void addTargetFile(File targetFile){
		if(targetFile.exists()){
			try {
				cpd.add(targetFile);
			} catch (IOException e) {
//				System.out.println("*"+targetFile.getAbsolutePath());
			}
		}else{
//			System.out.println("X");
		}
//			System.out.println(targetFolder.getName());
	}
	/**
	 * @return current Folder (bin)
	 */
	private File getCurrentBinFolder(){
		String[] pathBins = System.getProperty("java.class.path").split(File.pathSeparator);
		int i = 0;
		while(i < pathBins.length && !pathBins[i].endsWith("bin")){
			i++;
		}
		return new File(pathBins[i]);
	}
	/**
	 * search linked remote folder for "src" from .project file
	 * @param projFile
	 * @return
	 */
	private String getPathSrcFromProjXml(File projFile){
		String pathSrc=null;
		Document document = load(projFile.getAbsolutePath());
//		Node root = document.getDocumentElement();
//		System.out.println(root.getNodeName());
		XPathFactory xpfactory = XPathFactory.newInstance();
		XPath xpath = xpfactory.newXPath();
		try {
			pathSrc = xpath.evaluate("/projectDescription/linkedResources/link[name=\"src\"]/location", document);
		} catch (XPathExpressionException e) {
		}
		return pathSrc;
	}
	/**
	 * alternative file setter
	 * @param targetTestClass
	 */
	public void setTargetClass(Class targetTestClass){
		String packageName = targetTestClass.getPackage().getName();
		File pathBin = getCurrentBinFolder();
		File dmy = new File(pathBin.getParent(),"src");
		if(!dmy.exists()){
			File projFile = new File(pathBin.getParent(), ".project");
			dmy = new File(getPathSrcFromProjXml(projFile));
		}
		setTargetFolder(new File(dmy, packageName));
	}
	/**
	 * xml loader for analyze .project file
	 * @param fileName
	 * @return
	 */
	private Document load(String fileName) {
		Document document = null;
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			if(new File(fileName).exists()){
				FileInputStream fileInputStream = new FileInputStream(fileName);
				document = documentBuilder.parse(fileInputStream);
				fileInputStream.close();
			}else{
				URL url = new URL(fileName);
				document = documentBuilder.parse(url.openStream());
			}
		} catch (ParserConfigurationException e) {
		} catch (FileNotFoundException e) {
		} catch (SAXException e) {
		} catch (IOException e) {
		}
		return document;
	}
	/**
	 * files setter by folder (src folder)
	 * @param targetFolder
	 */
	void setTargetFolder(File targetFolder){
		for(File file : targetFolder.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				if(name.equals("package-info.java")){
					return false;
				}else if(name.endsWith("Test.java")){
					return false;
				}else if(!name.endsWith(".java")){
					return false;
				}else{
					return true;
				}
			}
		})){
			addTargetFile(file);
		}
	}
	public void setMinimumTileSize(int minimumTokens){
		cpdConf.setMinimumTileSize(minimumTokens);
	}
	/**
	 * go and response Iterator<Match>
	 */
	public void cpd(){
		cpd.go();
		matches = cpd.getMatches();
	}
	ArrayList <SourceCode> getSources(){
		ArrayList <SourceCode> wk = new ArrayList <SourceCode>();
		for(SourceCode src : cpd.getSources()){
			wk.add(src);
		}
		return wk;
	}
	String retrieveSrc(SourceCode code){
		return code.toString();
	}
	public Matcher hasClone(int n) {
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
	public static String getTestResultXML(){
		
		return "C:\\Users\\n.ishihara\\runtime-EclipseApplication\\.metadata\\.plugins\\org.eclipse.jdt.junit.core\\history";
	}
	public static void main(String[] args){
		System.out.println(getTestResultXML());
		Keeper kp = new Keeper();
		System.out.println(kp.historyFolder.getAbsolutePath());
		for(File file : kp.historyFolder.listFiles()){
			kp.keep(file, "log");
		}
		kp.keep(new File("C:\\Users\\n.ishihara\\Dropbox\\Java\\sample.codes\\src\\cc24\\CodeClone05.java"));
		System.out.println(kp.getZip());
	}
}
class Keeper{
	File zip = new File("C:\\Users\\n.ishihara\\Desktop\\wk.zip");
	File root = null;
	File projectRoot = null;
	File historyFolder = null;
	ArrayList <File> filesToKeep = new ArrayList <File>();
	Keeper(){
		String[] pathBins = System.getProperty("java.class.path").split(File.pathSeparator);
		int i = 0;
		while(i < pathBins.length && !pathBins[i].endsWith("bin")){
			i++;
		}
		File binFolder = new File(pathBins[i]);
		projectRoot = binFolder.getParentFile();
		root = projectRoot.getParentFile();
		historyFolder = new File(root, ".metadata/.plugins/org.eclipse.jdt.junit.core/history");
	}
	void keep(File file, String flg){
	}
	void keep(File file){
		filesToKeep.add(file);
	}
	void make() throws IOException{
		ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zip)));
		filesToKeep.forEach(file->{
		    try {
				ZipEntry entry = new ZipEntry(file.getName());
				zos.putNextEntry(entry);
				InputStream in = new FileInputStream(file);
				IOUtils.copy(in, zos);
				in.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		zos.flush();
		zos.close();
	}
	File getZip(){
		try {
			if(zip==null){
				zip = File.createTempFile("jplasResult", ".zip");
			}
			make();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return zip;
	}
}