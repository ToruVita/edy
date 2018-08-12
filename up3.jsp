<%@ page import="jplas.codeClone.*" %>
<%@ page import="java.io.*,java.util.*, javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>
<%@ page import="org.apache.commons.fileupload.*" %>
<%@ page import="org.apache.commons.fileupload.disk.*" %>
<%@ page import="org.apache.commons.fileupload.servlet.*" %>
<%@ page import="org.apache.commons.io.output.*" %>
<%
	TeacherUnit tu = new TeacherUnit();
	String msg = "ABC";
	String contentType = request.getContentType();
	if((contentType.indexOf("multipart/form-data") >= 0)) {
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(1024);
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax( -1 );
		try{ 
			List fileItems = upload.parseRequest(request);
			Iterator i = fileItems.iterator();
			while(i.hasNext()){
				FileItem fi = (FileItem)i.next();
				if(!fi.isFormField()){
					String fileName = new String(fi.getName().getBytes("Shift-JIS"), "UTF-8");
					tu.addIn(fileName, fi.getInputStream());
					msg = msg + fileName;
				}
			}
			File tf = tu.prepare();
			
			msg = tu.toString(tf);
		}catch(Exception ex){
			msg = "JSP ERROR";
		}
	}
%>
<%= msg %>