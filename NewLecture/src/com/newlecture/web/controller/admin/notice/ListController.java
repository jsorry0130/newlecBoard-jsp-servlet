package com.newlecture.web.controller.admin.notice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest; 
import javax.servlet.http.HttpServletResponse;

import com.newlecture.web.entity.Notice;
import com.newlecture.web.entity.NoticeView;
import com.newlecture.web.service.NoticeService;

@WebServlet("/admin/board/notice/list")
public class ListController extends HttpServlet {
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//request.setCharacterEncoding("utf-8"); 필터가 대체 할 기능
		
		String[] openIds = request.getParameterValues("open-id");
		String[] delIds = request.getParameterValues("del-id");
		String cmd = request.getParameter("cmd");
		String ids_ = request.getParameter("ids"); //전체목록
		String[] ids = ids_.trim().split(" "); // 전체목록, 공백을 구분자로 사용하여 토큰 분리
					
		NoticeService service = new NoticeService();

		switch(cmd) {
		case "일괄공개":
			for(String openId: openIds)
				System.out.printf("opend Id: %s \n", openId);
			
			List<String> oids = Arrays.asList(openIds);
			List<String> cids = new ArrayList(Arrays.asList(ids));
			
			cids.removeAll(oids);
			System.out.println("ids: "+Arrays.asList(ids));
			System.out.println("oids: "+oids);
			System.out.println("cids: "+cids);
			
			//Transaction
			service.pubNoticeAll(oids, cids);
			//sevice.closeNoticeList(clsIds);
			
			break;
		
		case "일괄삭제":
			int[] ids1 = new int[delIds.length];
			for(int i =0; i<delIds.length; i++) {
				ids1[i] = Integer.parseInt(delIds[i]);
			}
			int result = service.deleteNoticeAll(ids1);
			break;
		}
		
		response.sendRedirect("list");

	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String field_ = request.getParameter("f");
		String query_ = request.getParameter("q");
		String page_ = request.getParameter("p");   // String 으로 page를 받는 이유는 null 인지 아닌지 검증이 필요하기 때문
													// 즉 값이 있는 것을 검증한 후에 int로 변환
		
		String field = "title";
		if(field_ != null && !field_.equals("")) {
			field = field_;
		}
		
		String query = "";
		if(query_ != null && query_ != "") {
			query = query_;
		}
		
		int page = 1;
		if(page_ != null && !page_.equals("")) {
			page = Integer.parseInt(page_);
		}
		
		NoticeService service = new NoticeService();
		List<NoticeView> list = service.getNoticeList(field, query, page);
		int count = service.getNoticeCount(field, query);
		System.out.println(count);
		request.setAttribute("list", list);
		request.setAttribute("count", count);

		request.getRequestDispatcher("/WEB-INF/view/admin/board/notice/list.jsp").forward(request, response);
	}
}
