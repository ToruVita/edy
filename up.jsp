<%@ page import="jplas.*" %>
<%@ page import="java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>
<%@ page import="org.apache.commons.fileupload.*" %>
<%@ page import="org.apache.commons.fileupload.disk.*" %>
<%@ page import="org.apache.commons.fileupload.servlet.*" %>
<%@ page import="org.apache.commons.io.output.*" %>
<%@ page import="org.apache.commons.io.*" %>
<%
//	Up up = new Up();

	String filePath = "C:\\Users\\NobIsh\\Desktop\\20180811";
	String contentType = request.getContentType();
	if ((contentType.indexOf("multipart/form-data") >= 0)) {
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(1024);
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax( -1 );
		try{
			List fileItems = upload.parseRequest(request);
			Iterator i = fileItems.iterator();
			while ( i.hasNext () ) {
				FileItem fi = (FileItem)i.next();
				if ( !fi.isFormField () ) {
					String fileName = new String(fi.getName().getBytes("Shift-JIS"), "UTF-8");
					// boolean isInMemory = fi.isInMemory();
					// long sizeInBytes = fi.getSize();
					File file = new File( filePath, fileName) ;
					fi.write( file ) ;
//					up.up(fileName, fi.getInputStream());
				}else{
				}
			}
		}catch(Exception ex) {
			out.println("..bad error.");
		}
	}else{
	}
//	up.save(new File("C:\\Users\\NobIsh\\Desktop\\20180811"));
%>