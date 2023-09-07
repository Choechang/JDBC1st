package com.kh.model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.kh.model.vo.Member;

//DAO(Data Access Object) : DB에 직접적으로 접근해서 사용자의 요청에 맞는 sql문 실행 후 결과 반환(JDBC)
//							결과를 Controller로 다시 리턴
public class MemberDao {

	/**
	 * 
	 * 사용자가 입력한 정보들을 db에 추가시켜주는 매소드
	 * 
	 * @param m : 사용자가 입력한 값들이 담겨있는 member 객체
	 * @return  : insert가 추가되 ㄴ행수
	 */
	public int insertMember(Member m) {
		// insert문 => 처리된 행수(int) => 트랜잭션 처리
		
		//필요한 변수들 먼저 세팅
		int result = 0; // 처리된 결과(처리된 행 수)를 받아줄 변수
		Connection conn = null; // 연결된 db의 연결정보를 담는 객체
		PreparedStatement pstmt = null; // 완성된 sql문 전달해서 곧바로 실행한 후 결과를 받는 객체
		
		//실행할 sql문(완성된 형태)
		//	INSERT INTO MEMBER
		//	VALUES(SEQ_USERNO.NEXTVAL, 'user01', 'pass01', '홍길동', 
		//			null, 23, 'user01@iei.or.kr', '01022222222', 
		//			'부산', '등산, 영화보기', '2021-08-02');
		
//		String sql = "INSERT INTO MEMBER VALUES(SEQ_USERNO.NEXTVAL, "
//				+ "'" + m.getUserId()    + "'," 
//				+ "'" + m.getUserPsw()   + "',"
//				+ "'" + m.getUserName()  + "',"
//				+ "'" + m.getGender()    + "',"
//				      + m.getAge()       + ","
//				+ "'" + m.getEmail()     + "',"
//				+ "'" + m.getPhone()     + "',"
//				+ "'" + m.getAddress()   + "',"
//				+ "'" + m.getHobby()     + "', SYSDATE)";
		
		String sql = "INSERT INTO MEMBER VALUES(SEQ_USERNO.NEXTVAL,?,?,?,?,?,?,?,?,?,SYSDATE)";
		
		
		try {
			// 1) JDBC driver 등록
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			// 2) Connection 객체 생성 => db 연결
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","JDBC","JDBC");
			
			// 3) Statement 객체 생성
			pstmt = conn.prepareStatement(sql);		
			pstmt.setString(1, m.getUserId());
			pstmt.setString(2, m.getUserPsw());
			pstmt.setString(3, m.getUserName());
			pstmt.setString(4, m.getGender());
			pstmt.setInt(5,  m.getAge());
			pstmt.setString(6, m.getEmail());
			pstmt.setString(7, m.getPhone());
			pstmt.setString(8, m.getAddress() );
			pstmt.setString(9, m.getHobby());
			
			
			// 4,5) sql문 전달하면서 실행 후 결과받기(처리된 행 수)
			result = pstmt.executeUpdate(sql);
			
			// 6) 트랜잭션 처리
			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {	
			e.printStackTrace();
		} finally {
			// 7) 다 쓴 jdbc 객체들 반환
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		
			
		}
		
		return result;
		
	}

	public ArrayList<Member> selectList() {
		//select문(여러행 조회) => ResultSet 객체 => ArrayList<Member>에 담기
		
		//필요한 변수들 세팅
		ArrayList<Member> list = new ArrayList<>(); // 비어있는 상태
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null; // select문 실행시 조회된 결과값들이 최초로 담기는 객체
		
		// 싱행할 sql
		String sql = "SELECT * FROM MEMBER ";
		
		
		try {
			// 1) JDBC 드라이버 등록
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			// 2) Connection 생성
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","JDBC","JDBC");
			
			// 3) Statement 생성
			stmt = conn.createStatement();
			
			// 4, 5) sql문 실행 및 결과받기
			rset = stmt.executeQuery(sql);
			
			// 6) ResultSet 으로부터 데이터를 하나씩 꺼내서 
			while(rset.next()) {
				Member m = new Member();
				m.setUserNo(rset.getInt("userno"));
				m.setUserId(rset.getString("userid"));
				m.setUserPsw(rset.getString("userpwd"));
				m.setUserName(rset.getString("username"));
				m.setGender(rset.getString("gender"));
				m.setAge(rset.getInt("age"));
				m.setEmail(rset.getString("email"));
				m.setPhone(rset.getString("phone"));
				m.setAddress(rset.getString("address"));
				m.setHobby(rset.getString("hobby"));
				m.setEnrollDate(rset.getDate("enrolldate"));
				// 현재 참조하고 있는 행에 대한 모든 컬럼에 데이터들을 한 Member객체에 담기 끝!
				
				list.add(m); //리스트에 담기
				
//				list.add(new Member(m.setUserNo(rset.getInt("userno"));
//				m.setUserId(rset.getString("userid"));
//				m.setUserPsw(rset.getString("userpwd"));
//				m.setUserName(rset.getString("username"));
//				m.setGender(rset.getString("gender"));
//				m.setAge(rset.getInt("age"));
//				m.setEmail(rset.getString("email"));
//				m.setPhone(rset.getString("phone"));
//				m.setAddress(rset.getString("address"));
//				m.setHobby(rset.getString("hobby"));
//				m.setEnrollDate(rset.getDate("enrolldate"));)); 도 가능
						
			}
			
			// 반복문이 끝난 시점
			// 조회된 데이터가 없다면 리스트는 비어있을 것이다.
			// 조회된 데이터가 있다면 list에는 한 개이상 담겨있을 것이다.
		} catch (ClassNotFoundException e) {	
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {	
			try {
				rset.close();
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
		}

	public Member selectByUserId(String userId) {
		// select문(한 개) => ResultSet객체 => Member객체
		
		Member m = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		
		String sql = "SELECT * FROM MEMBER WHERE USERID = ?";
		
		
		try {
			// 1) 
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			// 2) 
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe","JDBC","JDBC");
			
			// 3) 
			pstmt = conn.prepareStatement(sql); // 미완성 쿼리 전달(완성 쿼리 전달해도 괜찮다.)
			pstmt.setString(1, userId);
			// 4,5 )
			rset = pstmt.executeQuery();
			
			// 6)
			
			//while로 해도 됨
			if(rset.next()) {
				m = new Member();
				m.setUserNo(rset.getInt("userno"));
				m.setUserId(rset.getString("userid"));
				m.setUserPsw(rset.getString("userpwd"));
				m.setUserName(rset.getString("username"));
				m.setGender(rset.getString("gender"));
				m.setAge(rset.getInt("age"));
				m.setEmail(rset.getString("email"));
				m.setPhone(rset.getString("phone"));
				m.setAddress(rset.getString("address"));
				m.setHobby(rset.getString("hobby"));
				m.setEnrollDate(rset.getDate("enrolldate"));
			}
			
//			if(rset.next()) {
//				m = new Member(rset.getInt("userno"),
//						rset.getString("userid"),
//						rset.getString("userpwd"),
//						rset.getString("username"),
//						rset.getString("gender"),
//						rset.getInt("age"),
//						rset.getString("email"),
//						rset.getString("phone"),
//						rset.getString("address"),
//						rset.getString("hobby"),
//						rset.getDate("enrolldate"));
//			}
			// 위의 조건문 다 끝난 시점
			// 만약 조회된 데이터가 없다면 => m => null
			// 만약 조회된 데이터가 있다면 => m는 생성된 객체
		

		} catch (ClassNotFoundException e) {	
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {	
			try {
				rset.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return m;
	}

	public ArrayList<Member> selectByUserName(String keyword) {
		//select문(여러행) => ResultSet 객체 => ArrayList<Member> 객체에 담기
		
		ArrayList<Member> list = new ArrayList<>(); // 텅 빈 상태
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		
		// SELECT * FROM MEMBER WHERE USERNAME LIKE '%"+keyword+"%'
		String sql = "SELECT * FROM MEMBER WHERE USERNAME LIKE ?";
		
		try {
			// 1) JDBC 드라이버 등록
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			// 2) Connection 생성
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","JDBC","JDBC");
			
			// 3) Statement 생성
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, "%" + keyword +"%");
			
			// 4, 5) sql문 실행 및 결과받기
			rset = pstmt.executeQuery(sql);
			
			// 6) ResultSet 으로부터 데이터를 하나씩 꺼내서 
			while(rset.next()) {
				Member m = new Member();
				m.setUserNo(rset.getInt("userno"));
				m.setUserId(rset.getString("userid"));
				m.setUserPsw(rset.getString("userpwd"));
				m.setUserName(rset.getString("username"));
				m.setGender(rset.getString("gender"));
				m.setAge(rset.getInt("age"));
				m.setEmail(rset.getString("email"));
				m.setPhone(rset.getString("phone"));
				m.setAddress(rset.getString("address"));
				m.setHobby(rset.getString("hobby"));
				m.setEnrollDate(rset.getDate("enrolldate"));
				// 현재 참조하고 있는 행에 대한 모든 컬럼에 데이터들을 한 Member객체에 담기 끝!
				
				list.add(m); //리스트에 담기
		
			
			}
			
			// 반복문이 끝난 시점
			// 조회된 데이터가 없다면 리스트는 비어있을 것이다.
			// 조회된 데이터가 있다면 list에는 한 개이상 담겨있을 것이다.
		} catch (ClassNotFoundException e) {	
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {	
			try {
				rset.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}	
	
	public int updateMember(Member m) {
		// Update문 => 처리된 행 수(int => 트랜잭션 처리
		int result = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		/*
		 * update member
		 * set userpwd = 'xx', email = 'xx', phone = 'xx', address = 'xx'
		 * where userid = 'xxx'
		 * */
		
//		String sql = "UPDATE MEMBER "
//					+"SET USERPWD = '" + m.getUserPsw() + "' "
//						+",	EMAIL = '" + m.getEmail()   + "' "
//						+", PHONE = '" + m.getPhone() + "'"
//						+", ADDRESS = '" + m.getAddress() + "'"
//						+ "WHERE USERID = '" + m.getUserId() + "'";
		String sql = "UPDATE MEMBER SET USERPWD =? , EMAIL =? , PHONE =?, ADDRESS =? WHERE USERID =?";
		
		
			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
				conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","JDBC","JDBC");
				pstmt = conn.prepareStatement(sql);
				
				
				pstmt.setString(1,  m.getUserPsw());
				pstmt.setString(2,  m.getEmail());
				pstmt.setString(3,  m.getPhone());
				pstmt.setString(4,  m.getAddress());
				pstmt.setString(5,  m.getUserId());
				
				
				
				result = pstmt.executeUpdate();
				
				if(result > 0) {
					conn.commit();
				}else {
					conn.rollback();
				}
			
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {	
				try {
					pstmt.close();
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			return result;
		}	
	
	public int deleteMember(String userId) {
		//delete문 => 처리된 행 수 => 트랜잭션 처리
		
		int result = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		String sql = "delete from member where userid =?";
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","JDBC","JDBC");
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userId);
			result = pstmt.executeUpdate();
			
			if(result > 0) {
				conn.commit();
			}else {
				conn.rollback();
			}
		
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {	
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}	
		
	
} 
		


		


