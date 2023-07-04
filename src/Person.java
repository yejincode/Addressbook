import java.io.*;
import java.util.*;

public class Person implements java.io.Serializable { // 직렬화 가능한 클래스로 만듦
	private String name; // 이름 필드
	private String phoneNum; // 전화번호 필드
	private String address; // 집주소 필드
	private String email; // 이메일 필드

	public Person(String name, String phoneNum, String address, String email) {
		this.name = name;
		this.phoneNum = phoneNum;
		this.address = address;
		this.email = email;
	}

	public Person() {
		this.name = null;
		this.phoneNum = null;
		this.address = null;
		this.email = null;
	}

	public void setName(String name) { // 이름 설정자
		this.name = name;
	}

	public void setPhoneNum(String phoneNum) { // 전화번호 설정자
		this.phoneNum = phoneNum;
	}

	public void setAddress(String address) { // 집주소 설정자
		this.address = address;
	}

	public void setEmail(String email) { // 이메일 설정자
		this.email = email;
	}

	public String getEmail() { // 이메일 접근자
		return email;
	}

	public String getName() { // 이름 접근자
		return name;
	}

	public String getPhoneNum() { // 전화번호 접근자
		return phoneNum;
	}

	public String getAddress() { // 집주소 접근자
		return address;
	}

	// 필드 데이터 파일에 쓰는(저장) 메소드 -> 객체 직렬화 사용하면서 안쓰는 함수가 되었음
	public void writeMyField(DataOutputStream fos) throws Exception {
		try {
			fos.writeUTF(name);
			fos.writeUTF(phoneNum);
			fos.writeUTF(address);
			fos.writeUTF(email);
		} catch (IOException ioe) {
			throw new IOException("PersonSaveIOE\"");
		} catch (Exception ex) {
			throw new Exception("PersonSaveEx");
		}
	}

	// 파일로부터 데이터 읽고 필드에 넣어주는 메소드 -> 객체 직렬화 사용하면서 안쓰는 함수가 되었음
	public void readMyField(DataInputStream fis) throws Exception {
		try {
			name = fis.readUTF();
			phoneNum = fis.readUTF();
			address = fis.readUTF();
			email = fis.readUTF();
		} catch (EOFException eofe) {
			throw new EOFException("PersonReadEOFE");
		} catch (IOException ioe) {
			throw new IOException("PersonReadIOE");
		} catch (Exception ex) {
			throw new Exception("PersonReadEx");
		}
	}

}
