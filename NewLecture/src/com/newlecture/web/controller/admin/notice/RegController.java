package com.newlecture.web.controller.admin.notice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.newlecture.web.entity.Notice;
import com.newlecture.web.service.NoticeService;

@MultipartConfig(

		fileSizeThreshold = 1024*1024,
		maxFileSize = 1024*1024*5,
		maxRequestSize = 1024*1024*5*5 
		
)

@WebServlet("/admin/board/notice/reg")
public class RegController extends HttpServlet{
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		
		request.getRequestDispatcher("/WEB-INF/view/admin/board/notice/reg.jsp").forward(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		String isOpen = request.getParameter("open");
		
		Collection<Part> parts = request.getParts();
		StringBuilder builder = new StringBuilder();
		
		for(Part p: parts) { //파일이 담겨있는 컬렉션 for 루프
			System.out.println(p.getName()+"," +p.getSize());
			if(!p.getName().equals("file")) continue;
			if(p.getSize()==0) continue;
			
			Part filePart = p;
			String fileName = filePart.getSubmittedFileName();
			builder.append(fileName);
			builder.append(",");
			
			
			InputStream fis = filePart.getInputStream();
			
			String realPath = request.getServletContext().getRealPath("/upload");
			System.out.println(realPath);
			
			//실제 realPath가 없다면 path(물리경로)를 생성하는 작업
			File path = new File(realPath);
			if(!path.exists()) {
				path.mkdirs();
			}
			
			String filePath = realPath +File.separator +fileName; //윈도우에 국한되지 않도록 자바 기반의 주소 구분자 seperator 사용
			FileOutputStream fos = new FileOutputStream(filePath);
			
			byte[] buf = new byte[1024];
			int size = 0;
			while((size = fis.read(buf))!= -1 ) { // read 함수의 반환값은 더 이상 읽어올것이 없을때 -1을 반환
				fos.write(buf,0, size); //버퍼에서 size 개수만큼만 저장(마지막 버퍼를 퍼올때 1024를 다 푸는것이 아니라 사이즈만큼만 퍼오기위해)
			}
			
			
			fos.close();
			fis.close();
		}
		
		builder.delete(builder.length()-1, builder.length());
		
		
		boolean pub = false;
		if(isOpen != null) {
			pub = true;
		}
		
		Notice notice = new Notice();
		notice.setTitle(title);
		notice.setContent(content);
		notice.setPub(pub);
		notice.setWriterId("newlec"); //일단 권한인증을 구현하기 전에 newlec으로 처리
		notice.setFiles(builder.toString());
		
		NoticeService service = new NoticeService();
		int result = service.insertNotice(notice);
		
//		response.setCharacterEncoding("UTF-8");
//		response.setContentType("text/html; charset=UTF-8");
		
		response.sendRedirect("list");
	}
}
