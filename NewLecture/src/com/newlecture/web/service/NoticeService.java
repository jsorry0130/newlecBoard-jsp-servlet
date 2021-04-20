package com.newlecture.web.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.newlecture.web.entity.Notice;
import com.newlecture.web.entity.NoticeView;

public class NoticeService {
	public int removeNoticeAll(int[] ids){
		
		return 0;
	}
	
	public int pubNoticeAll(int[] oids, int[] cids){
		
		
		List<String> oidsList = new ArrayList<>(); 
		for(int i=0; i<oids.length; i++)
			oidsList.add(String.valueOf(oids[i]));

		List<String> cidsList = new ArrayList<>(); 
		for(int i=0; i<cids.length; i++)
			cidsList.add(String.valueOf(cids[i]));
		
		
		return pubNoticeAll(oidsList, cidsList);
	}
	
	public int pubNoticeAll(List<String> oids, List<String> cids){
		
		String oidsCSV = String.join(",", oids);
		String cidsCSV = String.join(",", cids);
		
		return pubNoticeAll(oidsCSV, cidsCSV);
	}
	
	public int pubNoticeAll(String oidsCSV, String cidsCSV){
		int result = 0;
		
		String sqlOpen= String.format("update notice set pub=1 where id in (%s)", oidsCSV);
		String sqlClose= String.format("update notice set pub=0 where id in (%s)", cidsCSV);

		String url = "jdbc:oracle:thin:@localhost:1521/xepdb1";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "NEWLEC", "oracle");
			Statement st = con.createStatement();
			Statement stOpen = con.createStatement();
			result += stOpen.executeUpdate(sqlOpen);
			
			Statement stClose = con.createStatement();
			result += stClose.executeUpdate(sqlClose);
			
			stOpen.close();
			stClose.close();
			con.close();
		    
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	public int insertNotice(Notice notice){
		
		int result = 0;
		
		String sql = "insert into notice(title, content, writer_id, pub, files) values(?,?,?,?,?)";
		
		String url = "jdbc:oracle:thin:@localhost:1521/xepdb1";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "NEWLEC", "oracle");
			PreparedStatement st = con.prepareStatement(sql);
			st.setString(1, notice.getTitle());
			st.setString(2, notice.getContent());
			st.setString(3, notice.getWriterId());
			st.setBoolean(4, notice.getPub());
			st.setString(5, notice.getFiles());
			
			result = st.executeUpdate();
			
			st.close();
			con.close();
		    
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	public int deleteNotice(int id){
		
		return 0;
	}
	
	public int updateNotice(Notice notice){
		
		return 0;
	}
	
	public List<Notice> getNoticeNewestList(){
		
		return null;
	}
	
	
	public List<NoticeView> getNoticeList(){
		
		return getNoticeList("title", "", 1);
	}
	
	public List<NoticeView> getNoticeList(int page){
		
		return getNoticeList("title", "", page);
	}
	
	public List<NoticeView> getNoticeList(String field, String query, int page){
		
		List<NoticeView> list = new ArrayList<NoticeView>();
		
		String sql = "select * from ("
				+ "    select rownum num, n.* "
				+ "    from (select * from notice_view where "+field+" like ? order by regdate desc) n"
				+ "    ) "
				+ "where num between ? and ?"; 
		//startNum = 1, 11, 21, 31 -> an = 1+(page-1)*10
		//lastNum = 10, 20 -> page * 10
		
		String url = "jdbc:oracle:thin:@localhost:1521/xepdb1";
			
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "NEWLEC", "oracle");
			PreparedStatement st = con.prepareStatement(sql);
			st.setString(1, "%"+query+"%");
			st.setInt(2, 1+(page-1)*10);
			st.setInt(3, page*10);
			
			ResultSet rs = st.executeQuery();
			

			while(rs.next()){
				int id = rs.getInt("id");
				String title = rs.getString("TITLE"); 
				Date regdate =rs.getDate("regdate");
				String writerId =rs.getString("writer_id");
				int hit =rs.getInt("hit");
				String files =rs.getString("files");
				//String content =rs.getString("content");
				int cmtCount = rs.getInt("cmt_count");
				boolean pub = rs.getBoolean("pub");
				NoticeView notice = new NoticeView(id, title, regdate, writerId, hit, files, cmtCount, pub);
				list.add(notice);
			}
		  
			rs.close();
			st.close();
			con.close();
		    
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return list;
	}
	
	
	public List<NoticeView> getNoticePubList(String field, String query, int page) {
		
		List<NoticeView> list = new ArrayList<NoticeView>();
		
		String sql = "select * from ("
				+ "    select rownum num, n.* "
				+ "    from (select * from notice_view where "+field+" like ? order by regdate desc) n"
				+ "    ) "
				+ "where pub=1 and num between ? and ?"; 
		//startNum = 1, 11, 21, 31 -> an = 1+(page-1)*10
		//lastNum = 10, 20 -> page * 10
		
		String url = "jdbc:oracle:thin:@localhost:1521/xepdb1";
			
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "NEWLEC", "oracle");
			PreparedStatement st = con.prepareStatement(sql);
			st.setString(1, "%"+query+"%");
			st.setInt(2, 1+(page-1)*10);
			st.setInt(3, page*10);
			
			ResultSet rs = st.executeQuery();
			

			while(rs.next()){
				int id = rs.getInt("id");
				String title = rs.getString("TITLE"); 
				Date regdate =rs.getDate("regdate");
				String writerId =rs.getString("writer_id");
				int hit =rs.getInt("hit");
				String files =rs.getString("files");
				//String content =rs.getString("content");
				int cmtCount = rs.getInt("cmt_count");
				boolean pub = rs.getBoolean("pub");
				NoticeView notice = new NoticeView(id, title, regdate, writerId, hit, files, cmtCount, pub);
				list.add(notice);
			}
		  
			rs.close();
			st.close();
			con.close();
		    
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return list;
	}
	
	public int getNoticeCount(){	//목록이 있으면 목록에 대한 개수가 필요할 수 있다.
		
		return getNoticeCount("title", "");
	}
	
	public int getNoticeCount(String field, String query ){
		
		int count = 0;
		
		List<Notice> list = new ArrayList<Notice>();
		
		String sql = "select count(id) count from ("
				+ "    select rownum num, n.* "
				+ "    from (select * from notice where "+field+" like ? order by regdate desc) n"
				+ "    )";
		
		//startNum = 1, 11, 21, 31 -> an = 1+(page-1)*10
		//lastNum = 10, 20 -> page * 10
		
		String url = "jdbc:oracle:thin:@localhost:1521/xepdb1";
			
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "NEWLEC", "oracle");
			PreparedStatement st = con.prepareStatement(sql);
			st.setString(1, "%"+query+"%");

			ResultSet rs = st.executeQuery();
			
			if(rs.next()) {
				count = rs.getInt("count");
			}
		  
			rs.close();
			st.close();
			con.close();
		    
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return count;
	}
	
	public Notice getNotice(int id){ //detail 페이지 용
		
		Notice notice = null;
		
		String sql = "select * from notice where id = ?";
		
		String url = "jdbc:oracle:thin:@localhost:1521/xepdb1";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "NEWLEC", "oracle");
			PreparedStatement st = con.prepareStatement(sql);
			st.setInt(1, id);

			ResultSet rs = st.executeQuery();
			

			if(rs.next()){
				int nid = rs.getInt("id");
				String title = rs.getString("TITLE"); 
				Date regdate =rs.getDate("regdate");
				String writerId =rs.getString("writer_id");
				int hit =rs.getInt("hit");
				String files =rs.getString("files");
				String content =rs.getString("content");
				boolean pub = rs.getBoolean("pub");
				
				notice = new Notice(nid, title, regdate, writerId, hit, files, content, pub);
			
			}
		  
			rs.close();
			st.close();
			con.close();
		    
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return notice;
	}
	
	public Notice getNextNotice(int id){
		Notice notice = null;

		String sql = "select * from notice "
				+ "where id = ("
				+ "    select id from (select * from notice order by regdate) "
				+ "    where regdate > (select regdate from notice where id = ?) "
				+ "    and rownum = 1"
				+ ")";
		
		String url = "jdbc:oracle:thin:@localhost:1521/xepdb1";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "NEWLEC", "oracle");
			PreparedStatement st = con.prepareStatement(sql);
			st.setInt(1, id);

			ResultSet rs = st.executeQuery();
			

			if(rs.next()){
				int nid = rs.getInt("id");
				String title = rs.getString("TITLE"); 
				Date regdate =rs.getDate("regdate");
				String writerId =rs.getString("writer_id");
				int hit =rs.getInt("hit");
				String files =rs.getString("files");
				String content =rs.getString("content");
				boolean pub = rs.getBoolean("pub");
				
				notice = new Notice(nid, title, regdate, writerId, hit, files, content, pub);
			
			}
		  
			rs.close();
			st.close();
			con.close();
		    
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return notice;

	}
	
	public Notice getPrevNotice(int id){
		Notice notice = null;

		String sql = "select * from notice "
				+ "where id = ("
				+ "    select id from (select * from notice order by regdate desc) "
				+ "    where regdate < (select regdate from notice where id = ?) "
				+ "    and rownum = 1"
				+ ")";
		
		String url = "jdbc:oracle:thin:@localhost:1521/xepdb1";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "NEWLEC", "oracle");
			PreparedStatement st = con.prepareStatement(sql);
			st.setInt(1, id);

			ResultSet rs = st.executeQuery();
			

			if(rs.next()){
				int nid = rs.getInt("id");
				String title = rs.getString("TITLE"); 
				Date regdate =rs.getDate("regdate");
				String writerId =rs.getString("writer_id");
				int hit =rs.getInt("hit");
				String files =rs.getString("files");
				String content =rs.getString("content");
				boolean pub = rs.getBoolean("pub");
				
				notice = new Notice(nid, title, regdate, writerId, hit, files, content, pub);
			
			}
		  
			rs.close();
			st.close();
			con.close();
		    
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return notice;

	}

	public int deleteNoticeAll(int[] ids) {
		int result = 0;
		String params = "";
		
		for(int i =0; i<ids.length; i++) {
			params += ids[i];
			if(i<ids.length-1){
				params += ",";
			}
		}
		System.out.println("삭제할 로우들: "+params);
		String sql = "Delete notice where id in ("+params+")";
		
		String url = "jdbc:oracle:thin:@localhost:1521/xepdb1";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(url, "NEWLEC", "oracle");
			Statement st = con.createStatement();
			result = st.executeUpdate(sql);
			
			st.close();
			con.close();
		    
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}


}
