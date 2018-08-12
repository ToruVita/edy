package jplas.codeClone;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import org.apache.tomcat.util.http.fileupload.IOUtils;

public class TeacherUnit {
	HashMap <String, byte[]> ress = new HashMap <String, byte[]>();
	public TeacherUnit() {
		
	}
	public void addIn(String fnm, InputStream in){
		ByteArrayOutputStream bf = new ByteArrayOutputStream();
		try {
			IOUtils.copy(in, bf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ress.put(fnm, bf.toByteArray());
	}
	public File prepare(){
		try {
			File file = File.createTempFile("Teachers", "TEST");
			file.delete();
			file.mkdir();
			ress.keySet().forEach(f->{
				System.out.println(f);
				try {
					Files.write(Paths.get(file.getAbsolutePath(), f), ress.get(f));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			return file;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	public int tnoc(File folder){
		CPDWrapper cw = new CPDWrapper();
		cw.setMinimumTileSize(3);
		cw.setFolder(folder);
		cw.cpd();
		return cw.getTotalLineOfCode();
	}
	public String testTest(File folder){
		StringBuffer bf = new StringBuffer();
		CPDWrapper cw = new CPDWrapper();
		cw.setMinimumTileSize(3);
		cw.setFolder(folder);
		cw.cpd();
		cw.hasClone(0);
//		assertThat("Code Clones are found.", cw.matches, cw.hasClone(0));
		return bf.toString();
	}
	public String toString(){
		StringBuffer bf = new StringBuffer();
		ress.keySet().forEach(k->{
			bf.append(k);
		});
		return bf.toString();
	}
	public String toShorterString(File folder){
		StringBuffer bf = new StringBuffer();
		String str = toString(folder);
		int ct = str.split("Match: ").length-1;
		bf.append("There are "+ct+" code clones matches.");
		if(ct>1){
			String[] lines = str.split("Match: ")[1].split(System.lineSeparator());
			for(String s : lines){
				if(s.startsWith("tokenCount = ")){
					bf.append(System.lineSeparator());
					bf.append("Maximum code clone token size is "+ s.split("=")[1]+".");
				}else if(s.startsWith("marks = ")){
					bf.append(System.lineSeparator());
					bf.append(""+ s.split("=")[1]+" places");
				}
			}
		}
		return bf.toString();
	}
	public String toString(File folder){
		CPDWrapper cw = new CPDWrapper();
		cw.setMinimumTileSize(3);
		cw.setFolder(folder);
		cw.cpd();
		cw.hasClone(0);
		StringBuffer bf = new StringBuffer();
		cw.matches.forEachRemaining(m->{
			if(bf.length()>0)
				bf.append(System.lineSeparator());
			bf.append(m.toString());
			bf.append(System.lineSeparator());
			int l = m.getSourceCodeSlice().split(System.lineSeparator()).length;
			m.forEach(mk->{
				if(mk.getFilename().startsWith(folder.getAbsolutePath())){
					bf.append((new File(mk.getFilename()).getName())+":"+mk.getBeginLine()+"-"+(mk.getBeginLine()+l-1));
				}else{
					bf.append(mk.getFilename()+"("+mk.getBeginLine()+")");
				}
				bf.append(System.lineSeparator());
			});
			bf.append(m.getSourceCodeSlice());
			bf.append(System.lineSeparator());
		});
		return bf.toString();
	}
}
