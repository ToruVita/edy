package jplas.answer;

import static java.util.Arrays.asList;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

/**
 * @author NobIsh
 *
 */
public class JPLASAnswer {
	/**
	 * Resources : source codes (.java), libralies (.jar) be stocked with name as byte[] 
	 */
	HashMap <String, byte[]> resources = new HashMap <String, byte[]>(); 
	File workingFolder;
	public JPLASAnswer() {
	}
	public void addResource(File file) throws IOException{
		byte[] bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
		resources.put(file.getName(), bytes);
	}
	public void addResource(String name, InputStream in) throws IOException{
	    ByteArrayOutputStream bout = new ByteArrayOutputStream();
	    byte [] buffer = new byte[1024];
	    while(true) {
	        int len = in.read(buffer);
	        if(len < 0) {
	            break;
	        }
	        bout.write(buffer, 0, len);
	    }
	    resources.put(name, bout.toByteArray());
	}
	public void completeList(List <String> list){
		list.forEach(s->{
			if(!resources.containsKey(s)){
				appendPreparedFilde(s); 
			}
		});
	}
	HashMap <String, String> generateIndexOfJarFiles(File folder){
//		File folder = (targetFolder!=null)?targetFolder :new File("C:\\Users\\NobIsh\\Dropbox\\Java\\lib");
		HashMap <String, String> jarFilePaths = new HashMap <String, String>();
		for(File f : folder.listFiles()){
			if(f.isFile() && f.getName().toLowerCase().endsWith(".jar")){
				jarFilePaths.put(f.getName(), f.getAbsolutePath());
			}else if(f.isDirectory()){
				jarFilePaths.putAll(generateIndexOfJarFiles(f));
			}
		}
		return jarFilePaths;
	}
	void appendPreparedFilde(String filename){
		//TODO 		ファイル名で指定されるファイルがユーザから添付されなかった場合，サーバで準備したものを挿入する
		HashMap <String, String> jarMap = generateIndexOfJarFiles(new File("C:\\Users\\NobIsh\\Dropbox\\Java\\lib"));
		String baseFile = "C:\\Users\\NobIsh\\Dropbox\\Java\\lib";
		File file = null;
		if(filename.equals("CPDWrapper.java")){
			file = new File("C:\\Users\\NobIsh\\AppData\\Local\\Temp\\20180807\\edy\\src\\jplas\\codeClone", filename);
		}else if(filename.endsWith(".jar")){
			file = new File(jarMap.get(filename));
		}
		try {
			addResource(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public File prepareFolder(){
		File folder = defFolder();
		resources.keySet().forEach(name->{
			try {
				Files.write(Paths.get(folder.getAbsolutePath(), name), resources.get(name));
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		return folder;
	}
	private File defFolder(){
		File folder = null;
		try {
			folder = File.createTempFile("asdada", "");
			folder.delete();
			folder.mkdir();
		} catch (IOException e) {
		}
		return folder;
	}
	public String compile(File folder){
		String res = "";
		ArrayList <String> classPaths = new ArrayList <String> (); 
		classPaths.add(".");
		classPaths.add("..");
		searchFiles(classPaths, ".jar", folder);
		ArrayList <String> srcs = new ArrayList <String> (); 
//		searchFiles(srcs, ".java", folder);
//		for(String s : srcs){
//			String testCode = s.replace(folder.getAbsolutePath(), "").replace(File.separator, ".");
//			testCode = testCode.substring(1, testCode.length()-6);
			ArrayList <String> unitTestCommands = new ArrayList <String>();
			unitTestCommands.clear();
			unitTestCommands.add("javac");
			unitTestCommands.add("-cp");
			unitTestCommands.add(String.join(File.pathSeparator, classPaths));
			unitTestCommands.add("-d");
			unitTestCommands.add(folder.getAbsolutePath());
			unitTestCommands.add("*.java");
//			System.out.println(unitTestCommands);
			res = JplasRunner.jplasRun(folder, unitTestCommands);
			try {
				Files.write(Paths.get(folder.getAbsolutePath(), "compile.txt"), res.getBytes(), StandardOpenOption.CREATE);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//		}
		return res;
	}
	public String compileAll(File file){
		if(resources.size()>0){
//			File file = defFolder();
			List<JavaFileObject> srcs = new ArrayList<JavaFileObject>();
			resources.keySet().forEach(name->{
				JavaFileObject jfo = new OJavaFileObject(URI.create(name), Kind.SOURCE);
				((OJavaFileObject)jfo).setBytes(resources.get(name));
				srcs.add(jfo);
			});
			DiagnosticCollector<JavaFileObject> diags = new DiagnosticCollector<JavaFileObject>();
			List<String> options = asList("-d", file.getAbsolutePath());
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			JavaFileManager fileManager = compiler.getStandardFileManager(new DiagnosticCollector<JavaFileObject>(), null, null);
			JavaCompiler.CompilationTask compilerTask = compiler.getTask(null, fileManager, diags, options, null, srcs);
			compilerTask.call();
			StringBuffer bf = new StringBuffer();
			for (Diagnostic diag : diags.getDiagnostics()) {
				if(bf.length()>0){
					bf.append(System.lineSeparator());
				}
				bf.append(diag.toString());
			}
//*************ここから今後のこやし
			Set<JavaFileObject.Kind> kind = new HashSet<JavaFileObject.Kind>();
			kind.add(JavaFileObject.Kind.CLASS);
			try {
				Iterable<JavaFileObject> clist = fileManager.list(StandardLocation.CLASS_OUTPUT, "", kind, false);
				for(JavaFileObject f:clist){
					System.out.println(f.getName());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//*************ここまで
			return bf.toString().trim();
		}else{
			return "NO SOURCE";
		}
	}
	class OJavaFileObject extends SimpleJavaFileObject{
		byte[] bytes = null;
		protected OJavaFileObject(URI uri, Kind kind) {
			super(uri, kind);
		}
		public void setBytes(byte[] bytes){
			this.bytes = bytes;
		}
		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
			return new String(bytes);
		}
	}
	private void searchFiles(ArrayList <String> cps, String postFix, File folder){
		for(File f : folder.listFiles()){
			if(f.isDirectory()){
				searchFiles(cps, postFix, f);
			}else if(f.getName().endsWith(postFix)){
				cps.add(f.getAbsolutePath());
			}
		}
	}
	public String test(File folder){
		String res = folder.getAbsolutePath();
		ArrayList <String> classPaths = new ArrayList <String> (); 
		classPaths.add(".");
		classPaths.add("..");
		searchFiles(classPaths, ".jar", folder);
		ArrayList <String> tests = new ArrayList <String> (); 
		searchFiles(tests, "Test.class", folder);
		for(String s : tests){
			String testCode = s.replace(folder.getAbsolutePath(), "").replace(File.separator, ".");
			testCode = testCode.substring(1, testCode.length()-6);
			ArrayList <String> unitTestCommands = new ArrayList <String>();
			unitTestCommands.clear();
			unitTestCommands.add("java");
			unitTestCommands.add("-cp");
			unitTestCommands.add(String.join(File.pathSeparator, classPaths));
			unitTestCommands.add("org.junit.runner.JUnitCore");
			unitTestCommands.add(testCode);
			res = JplasRunner.jplasRun(folder, unitTestCommands);
			try {
				Files.write(Paths.get(folder.getAbsolutePath(), "Test.txt"), res.getBytes(), StandardOpenOption.CREATE);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return res;
	}
}
