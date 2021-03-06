package com.sist.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;


import com.sist.member.MemberVO;

public class MemberDAO {
	private Connection conn;
	   private PreparedStatement ps;
	   //DB 연결 ==> 주소값 얻기  
	   /*
	    *   A a=new A();
	    *   bind("aaa",a)
	    *   bind("jdbc/oracle",new Connection())
	    *   100 101 
	    *   
	    *   int a=100
	    */
	   public void getConnection()
	   {
		   try // RMI
		   {
			   // 이름 저장 => 객체 이름  Connection 주소값 
			   /*
			    *  ===================== java://env/comp  JNDI
			    *   =========jdbc/oracle
			    *   이름      주소
			    *   =========  ==> 디렉토리
			    *  ===================== 
			    */
			   Context init=new InitialContext();//탐색기 열기 
	           // c 드라이브
	           Context root=(Context)init.lookup("java://comp/env");
			   // 원하는 폴더 
	           DataSource ds=(DataSource)root.lookup("jdbc/oracle");
			   conn=ds.getConnection();
			   // lookup ==> 이름으로 객체주소를 찾을 때 사용하는 메소드 
		   }catch(Exception ex)
		   {
			   System.out.println(ex.getMessage());
		   }
	   }
	   // 연결 종료 ==> 반환 
	   public void disConnection()
	   {
		   try
		   {
			   if(ps!=null) ps.close();
			   if(conn!=null) conn.close();
		   }catch(Exception ex) {}
		   // POJO
	   }
	   public int memberIdcheck(String mem_id)
	   {
		   int count=0;
		   try
		   {
			   getConnection();
			   String sql="SELECT COUNT(*) "
					     +"FROM member_table "
					     +"WHERE mem_id=?";
			   ps=conn.prepareStatement(sql);
			   ps.setString(1, mem_id);
			   ResultSet rs=ps.executeQuery();
			   rs.next();
			   count=rs.getInt(1);
			   rs.close();
		   }catch(Exception ex)
		   {
			   System.out.println(ex.getMessage());
		   }
		   finally
		   {
			   disConnection();
		   }
		   return count;
	   }
	   public List<ZipcodeVO> postFindData(String dong)
	   {
		   System.out.println("dong="+dong);
		   List<ZipcodeVO> list=new ArrayList<ZipcodeVO>();
		   try
		   {
			   getConnection();
			   String sql="SELECT zipcode,sido,gugun,dong,NVL(bunji,' ') "
					     +"FROM zipcode "
					     +"WHERE dong LIKE '%'||?||'%'";
			   ps=conn.prepareStatement(sql);
			   ps.setString(1, dong);
			   ResultSet rs=ps.executeQuery();
			   while(rs.next())
			   {
				   ZipcodeVO vo=new ZipcodeVO();
				   vo.setZipcode(rs.getString(1));
				   vo.setSido(rs.getString(2));
				   vo.setGugun(rs.getString(3));
				   vo.setDong(rs.getString(4));
				   vo.setBunji(rs.getString(5));
				   list.add(vo);
			   }
			   rs.close();
		   }catch(Exception ex)
		   {
			   System.out.println(ex.getMessage());
		   }
		   finally
		   {
			   disConnection();
		   }
		   return list;
	   }
	   public int postFindCount(String dong)
	   {
		   int list=0;
		   try
		   {
			   getConnection();
			   String sql="SELECT COUNT(*) "
					     +"FROM zipcode "
					     +"WHERE dong LIKE '%'||?||'%'";
			   ps=conn.prepareStatement(sql);
			   ps.setString(1, dong);
			   ResultSet rs=ps.executeQuery();
			   rs.next();
			   list=rs.getInt(1);
			   rs.close();
		   }catch(Exception ex)
		   {
			   System.out.println(ex.getMessage());
		   }
		   finally
		   {
			   disConnection();
		   }
		   return list;
	   }
	   public void memberInsert(MemberVO vo)
	   {
		   try
		   {
			   // Connection주소 얻기 SELECT NVL(MAX(mem_no)+1,1) FROM member_table)
			   getConnection();
			   String sql="INSERT INTO member_table VALUES("
					     +"( SELECT NVL(MAX(mem_no)+1,1) FROM member_table) ,?,?,?,?,?,?,0,0,?,?)";
			   ps=conn.prepareStatement(sql);
			   ps.setString(1, vo.getMem_id());
			   ps.setString(2, vo.getMem_pw());
			   ps.setString(3, vo.getMem_name());
			   ps.setString(6, vo.getMem_sex());
			   ps.setString(7, vo.getMem_birth());
			   ps.setString(4, vo.getMem_email());
/*			   ps.setString(7, vo.getPost());
			   ps.setString(8, vo.getAddr1());
			   ps.setString(9, vo.getAddr2());*/
			   ps.setString(5, vo.getMem_phone());
//			   ps.setString(11, vo.getContent());
			   ps.setString(8, vo.getLikeList());
			   ps.executeUpdate();
		   }catch(Exception ex)
		   {
			   ex.printStackTrace();
			   System.out.println(ex.getMessage());
		   }
		   finally
		   {
			   disConnection();//반환
		   }
	   }
	   public MemberVO isLogin(String mem_id,String mem_pw)
	   {
		   MemberVO vo=new MemberVO();
		   try
		   {
			   getConnection();
			   //ID체크 
			   String sql="SELECT COUNT(*) "
					     +"FROM member_table "
					     +"WHERE mem_id=?";
			   ps=conn.prepareStatement(sql);
			   ps.setString(1, mem_id);
			   ResultSet rs=ps.executeQuery();
			   rs.next();
			   int count=rs.getInt(1);
			   rs.close();
			   if(count==0)
			   {
				   vo.setMsg("NOID"); 
			   }
			   else
			   {
				  sql="SELECT mem_id,mem_name,mem_type,mem_pw "
				     +"FROM member_table "
					 +"WHERE mem_id=?";
				  ps=conn.prepareStatement(sql);
				  ps.setString(1, mem_id);
				  rs=ps.executeQuery();
				  rs.next();
				  vo.setMem_id(rs.getString(1));
				  vo.setMem_name(rs.getString(2));
				  vo.setMem_type(rs.getString(3));
				  String db_pwd=rs.getString(4);
				  if(db_pwd.equals(mem_pw))
				  {
					  vo.setMsg("OK");
				  }
				  else
				  {
					  vo.setMsg("NOPWD");
				  }
			   }
			   //PWD체크 
		   }catch(Exception ex)
		   {
			   System.out.println(ex.getMessage());
		   }
		   finally
		   {
			  disConnection(); 
		   }
		   return vo;
	   }
	   public MemberVO MemberUpdateData(int mem_no) {
			MemberVO vo=new MemberVO();
			try {
				getConnection();
				String sql="SELECT mem_no,mem_id,mem_pw,mem_name,mem_sex,mem_birth,"
						+"mem_email,mem_phone1,mem_phone2,mem_phone3 "
						+"FROM member_table "
						+"WHERE mem_no=?";
				ps=conn.prepareStatement(sql);
				ps.setInt(1, mem_no);
				ResultSet rs=ps.executeQuery();
				rs.next();
				vo.setMem_no(rs.getInt(1));
				vo.setMem_id(rs.getString(2));
				vo.setMem_pw(rs.getString(3));
				vo.setMem_name(rs.getString(4));
				vo.setMem_sex(rs.getString(5));
				vo.setMem_birth(rs.getString(6));
				vo.setMem_email(rs.getString(7));
				vo.setMem_phone1(rs.getString(8));
				vo.setMem_phone2(rs.getString(9));
				vo.setMem_phone3(rs.getString(10));
				rs.close();
			}catch(Exception ex) {
				System.out.println(ex.getMessage());
			}finally {
				disConnection();
			}
			return vo;
		}
	   public MemberVO MemberUpdate(MemberVO vo) {
			try {
				getConnection();
				String sql="UPDATE member_table SET mem_pw=?,mem_email=?,mem_phone1=?,mem_phone2=?,mem_phone3=? "
						+"WHERE mem_no=?";
				ps=conn.prepareStatement(sql);
				ps.setString(1, vo.getMem_pw());
				ps.setString(2, vo.getMem_email());
				ps.setString(3, vo.getMem_phone1());
				ps.setString(4, vo.getMem_phone2());
				ps.setString(5, vo.getMem_phone3());
				ps.setInt(6, vo.getMem_no());
				ps.executeUpdate();
			}catch(Exception ex) {
				System.out.println(ex.getMessage());
			}finally {
				disConnection();
			}		
			return vo;
		}
	      
}
