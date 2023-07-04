import java.io.*;
import java.util.*;
import java.sql.*;

public class Addressbook{
	
	Connection conn = null;
	Statement stmt = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	String url = null;
	
	public Addressbook() throws Exception {// UI에서 생성할 사람의 수 가져와서 객체 생성
		
		try {
			Class.forName("com.mysql.jdbc.Driver"); //드라이버 로드
			url = "jdbc:mariadb://localhost:3306/addressbookdb";
			conn = DriverManager.getConnection(url, "root", "1234"); 
			readFile(); //파라미터 집어넣기
		} 
		catch (ClassNotFoundException e) {
			      throw new ClassNotFoundException("ClassNotFoundException");
	    }
	}
	
	public int getCount() throws SQLException {// 등록된 사람 수 접근자 -> UI에서 쓰는 함수이다.

		int result = 0; 
		
		String sql = "SELECT COUNT (*) AS COUNT FROM person";
		stmt = conn.createStatement();
		rs = stmt.executeQuery(sql);
		
		while (rs.next()) {
			result = rs.getInt("COUNT"); 
		}
		return result;
	}

	// 동명이인 확인 메소드
	public boolean checkName(String name) throws SQLException {
		
		ResultSet result = stmt.executeQuery("select * from person where p_name = '" + name + "';" ); // 일치하는 이름 검색
		String find = null;
		
		while(result.next())
		{
			find = (String) result.getString("p_name");
		}
		
		if(find.equals(name)) // 등록된 이름이 존재한다면
			return true;
		
		return false;
	}

	// 등록된 전화번호가 있는지 확인 메소드
	public boolean checkPhoneNum(String phoneNum) throws SQLException //등록된 전화번호가 있는지 확인
	{ 
		ResultSet result = stmt.executeQuery("select * from person where p_number = '" + phoneNum + "';" ); // 일치하는 전화번호 검색
		String find = null;
		
		while(result.next())
		{
			find = (String) result.getString("p_number");
		}
		
		if(find.equals(phoneNum)) // 등록된 전화번호가 존재한다면
			return true;
		
		return false;
	}

	// 주소록 등록 메소드
	public void add(Person p) throws SQLException
	{
		stmt.executeUpdate("insert into person (p_name, p_number, p_address, p_email) values('" + p.getName() + "', '" + p.getPhoneNum() + "', '" + p.getAddress() + "', '" + p.getEmail() + "');");
	}

	// 이름으로 주소록 번호 검색 메소드, 등록된 이름 없을 경우 익셉션
	public ResultSet searchName(String name) throws SQLException // 이름을 이용한 검색
	{
		return stmt.executeQuery("select * from person where p_name like '%" + name + "%';"); //name을 포함하는 데이터 검색
	}

	// 전화번호로 주소록 번호 검색 메소드, 등록된 전화번호 없을 경우 익셉션
	public ResultSet searchPhoneNum(String phoneNum) throws SQLException // 전화번호를 이용한 검색
	{
		return stmt.executeQuery("select * from person where p_number like '%" + phoneNum + "%';");
	}

	// 주소록 수정 메소드
	public void modify(String phoneNum, Person p) throws SQLException // 일치하는 전화번호가 있는 데이터를 수정
	{
		stmt.executeUpdate("update person set p_name:='" + p.getName() + "', p_number:='" + p.getPhoneNum() + "', p_email:='" + p.getEmail() +	"' where p_number = '" + phoneNum + "';");
	}
	
	public void delete(String phoneNum) throws SQLException // 일치하는 전화번호(primary key)가 있는 데이터를 삭제
	{
		stmt.executeUpdate("delete from person where p_number = '" + phoneNum + "';");
	}

	// Person 객체 넘겨주는 메소드
	public Person getPerson(int index) throws SQLException {
		String sql = "SELECT * FROM person";
		stmt = conn.createStatement();
		rs = stmt.executeQuery(sql);
		
		rs.absolute(index);
		rs.next();
		
		String name = rs.getString("p_name");
		String number = rs.getString("p_number");
		String address = rs.getString("p_address");
		String email = rs.getString("p_email");
		
		Person p = new Person(name, number, address, email);
		
		return p; // index번쨰의 person객체를 넘겨준다.
	}

	// 프로그램 종료 할 때 close
	public void closeAll() throws SQLException
	{
		stmt.close(); // statement 닫기 
		conn.close(); // DB 연결 끊기 
	}
	

	// 데이터를  가져오는 메소드
	public void readFile() throws SQLException
	{
		stmt = conn.createStatement();
		rs = stmt.executeQuery("select * from person;"); // addressbook 테이블 내용 읽어오기
	}
}
