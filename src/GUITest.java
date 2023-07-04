import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.table.*;

class GUI extends JFrame {

	// 저장된 주소록 파일을 객체단위로 불러온다.
	ObjectInputStream in = null; // 직렬화
	ObjectOutputStream out = null; // 직렬화
	Person p;
	Addressbook ad = null;

	public GUI() throws Exception { // 파일을 읽어와야 하기 때문에 throws Exception을 추가했다.

		// 프레임 만들기(위치, 크기 등)
		JFrame frame = new JFrame("주소록 프로그램");
		frame.setPreferredSize(new Dimension(600, 400));
		frame.setLocation(500, 400);
		Container contentPane = frame.getContentPane();

		// 테이블 만들기
		String colNames[] = { "이름", "전화번호", "주소", "이메일" }; // 테이블에 들어갈 데이터 종류이다.
		DefaultTableModel model = new DefaultTableModel(colNames, 0); // 처음엔 행이 0개다.
		JTable table = new JTable(model);
		contentPane.setLayout(new BorderLayout(30, 20));
		contentPane.add(new JScrollPane(table), BorderLayout.CENTER);

		// 텍스트상자를 담을 panel,버튼을 담은 panel2,검색을 위한 콤보박스와 텍스트필드를 담을 panel3, panel1와 3을 담을
		// panel4를 생성
		JPanel panel = new JPanel();
		JPanel panel2 = new JPanel();
		JPanel panel3 = new JPanel();
		JPanel panel4 = new JPanel();

		// 각 패널의 레이아웃을 설정해준다.
		panel.setLayout(new GridLayout(2, 4));
		panel2.setLayout(new GridLayout(2, 2));
		panel3.setLayout(new GridLayout(1, 3));
		panel4.setLayout(new GridLayout(2, 1));

		// person 객체의 필드(이름, 전화번호, 주소, 이메일)를 입력받을 텍스트 상자를 만든다.
		JTextField text1 = new JTextField(10);
		JTextField text2 = new JTextField(10);
		JTextField text3 = new JTextField(10);
		JTextField text4 = new JTextField(10);

		// 주소록에 필요한 기능을 수행할 버튼을 생성한다.
		JButton button1 = new JButton("주소록 추가");
		JButton button2 = new JButton("주소록 삭제");
		JButton button3 = new JButton("주소록 수정");
		// JButton button4 = new JButton("주소록 저장");
		JButton button5 = new JButton("주소록 검색");

		// 검색 버튼 콤보박스 만들기
		String[] search = { "이름", "전화번호" };
		JComboBox strCombo = new JComboBox(search);
		JTextField text5 = new JTextField(10);

		// 만들어둔 panel에 필드를 담을 텍스트 상자를 넣어준다.
		panel.add(new JLabel("이름"));
		panel.add(text1);
		panel.add(new JLabel("전화번호"));
		panel.add(text2);
		panel.add(new JLabel("주소"));
		panel.add(text3);
		panel.add(new JLabel("이메일"));
		panel.add(text4);

		// 만들어둔 panel2에 버튼을 넣어준다.
		panel2.add(button1);
		panel2.add(button2);
		panel2.add(button3);
		// panel2.add(button4);

		// 만들어둔 panel3에 검색에 필요한 콤보박스, 텍스트상자, 버튼을 넣는다.
		panel3.add(strCombo);
		panel3.add(text5);
		panel3.add(button5);

		// 위치 조절을 위해 panel1,3을 panel4에 담는다.
		panel4.add(panel3);
		panel4.add(panel);

		// contentPane에 panel을 담고 위치를 정해준다.
		contentPane.add(panel4, BorderLayout.SOUTH);
		contentPane.add(panel2, BorderLayout.EAST);

		// 이벤트 리스너를 등록한다.
		button1.addActionListener(new AddActionListener(table, text1, text2, text3, text4));
		button2.addActionListener(new RemoveActionListener(table));
		button3.addActionListener(new ModifyActionListener(table, text1, text2, text3, text4));
		// button4.addActionListener(new SaveActionListener(table));
		button5.addActionListener(new SearchActionListener(table, text5, strCombo));

		// 윈도우를 디스플레이 한다.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

		// 주소록 객체 생성
		ad = new Addressbook(); // 다 수정하면 try catch 하기

		// 읽어온 파일의 내용을 읽어 생성해둔 테이블에 담는다.
		int c = ad.getCount();
		if (c == 0) {
			JOptionPane.showMessageDialog(null, "등록된 주소록이 없습니다."); // 알림창을 띄운다.
		} else {
			for (int i = 0; i < c; i++) {
				String arr[] = new String[4];
				arr[0] = ad.getPerson(i).getName();
				arr[1] = ad.getPerson(i).getPhoneNum();
				arr[2] = ad.getPerson(i).getAddress();
				arr[3] = ad.getPerson(i).getEmail();
				table.getModel();
				model.addRow(arr); // 기존에 저장된 파일의 person 객체를 테이블에 담아준다.
			}
		}
	}

	// 주소록 추가 메소드
	class AddActionListener implements ActionListener {
		JTable table;
		JTextField text1, text2, text3, text4;

		AddActionListener(JTable table, JTextField text1, JTextField text2, JTextField text3, JTextField text4) {
			this.table = table;
			this.text1 = text1;
			this.text2 = text2;
			this.text3 = text3;
			this.text4 = text4;
		}

		public void actionPerformed(ActionEvent e) {

			String arr[] = new String[4]; // person필드 담을 배열을 만든다. (테이블에 추가하기 위함)

			// 필드를 String형으로 바꾸어 배열에 담는다.
			String field_name = text1.getText(); // 이름 필드 자료형이 JTextField이므로 String형으로 바꾼다.
			arr[0] = field_name; // String형으로 바꾼 이름 필드를 배열에 담는다.
			String field_number = text2.getText(); // 전화번호 필드 자료형이 JTextField이므로 String형으로 바꾼다.
			arr[1] = field_number; // String형으로 바꾼 전화 번호 필드를 배열에 담는다.
			String field_address = text3.getText(); // 주소 필드 자료형이 JTextField이므로 String형으로 바꾼다.
			arr[2] = field_address; // String형으로 바꾼 주소 필드를 배열에 담는다.
			String field_email = text4.getText(); // 이메일 필드 자료형이 JTextField이므로 String형으로 바꾼다.
			arr[3] = field_email; // String형으로 바꾼 이메일 필드를 배열에 담는다.

			// 모든 필드에 데이터를 입력했는지 확인
			if ((field_name.equals("")) || (field_number.equals("")) || (field_address.equals(""))
					|| (field_email.equals(""))) {
				JOptionPane.showMessageDialog(null, "모든 데이터를 입력하세요");
				return;
			}

			// 동명이인 확인
			try {
				if (ad.checkName(field_name) == true) { // String형으로 바꾼 이름필드를 기존에 저장된 값과 비교해준다.
					JOptionPane.showMessageDialog(null, "이미 등록된 이름입니다. 이름 뒤에 숫자를 붙여 저장하세요. (ex:박강현1, 박강현2 ,박강현3)"); // 알림창을
																													// 띄운다.
					return; // 동명이인이 있으면 return하여 함수에서 나온다. 필드를 다시 입력해주고 추가 버튼을 눌러줘야 한다.
				}
			} catch (HeadlessException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (SQLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			// 겹치는 전화번호 확인
			try {
				if (ad.checkPhoneNum(field_number) == true) {// String형으로 바꾼 전화번호 필드를 기존에 저장된 값과 비교해준다.
					JOptionPane.showMessageDialog(null, "이미 등록된 번호입니다. 다른 번호를 입력해주세요."); // 알림창을 띄운다.
					return; // 이미 등록된 번호가 있으면 return하여 함수에서 나온다. 필드를 다시 입력해주고 추가 버튼을 눌러줘야 한다.
				}
			} catch (HeadlessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			DefaultTableModel model = (DefaultTableModel) table.getModel();
			model.addRow(arr); // 모델에 데이터를 입력해준다.

			// 입력한 정보를 추가한다.
			try {
				ad.add(new Person(field_name, field_number, field_address, field_email)); // 주소록 등록 메소드

			} catch (Exception error) {
				JOptionPane.showMessageDialog(null, "등록 가능 인원 수가 초과되었습니다.");
			}

			JOptionPane.showMessageDialog(null, "주소록을 추가했습니다.");

			// 텍스트박스 값을 비우기
			text1.setText("");
			text2.setText("");
			text3.setText("");
			text4.setText("");

		}
	}

	// 주소록 삭제 메소드
	class RemoveActionListener implements ActionListener {
		JTable table;

		RemoveActionListener(JTable table) {
			this.table = table;
		}

		public void actionPerformed(ActionEvent e) {

			int confirm = JOptionPane.showConfirmDialog(null, "정말 삭제하시겠습니까?", "삭제", JOptionPane.YES_NO_OPTION);

			if (confirm == JOptionPane.YES_OPTION) {
				int row = table.getSelectedRow();

				if (row == -1) // 테이블에 아무 데이터도 없을 떄
					return;

				DefaultTableModel model = (DefaultTableModel) table.getModel();

				ad.delete(row); // row인덱스번째의 객체를 삭제한다.

				model.removeRow(row); // row행의 테이블 값을 삭제한다.
				JOptionPane.showMessageDialog(null, "선택하신 주소록을 삭제했습니다.");
			}

		}
	}

	// 주소록 수정 메소드
	class ModifyActionListener implements ActionListener {
		JTable table;
		JTextField text1, text2, text3, text4;

		ModifyActionListener(JTable table, JTextField text1, JTextField text2, JTextField text3, JTextField text4) {
			this.table = table;
			this.text1 = text1;
			this.text2 = text2;
			this.text3 = text3;
			this.text4 = text4;
		}

		public void actionPerformed(ActionEvent e) {

			int confirm = JOptionPane.showConfirmDialog(null, "정말 수정하시겠습니까?", "수정", JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {

				int row = table.getSelectedRow();

				if (row == -1) // 테이블에 아무 데이터도 없을 떄
					return;

				DefaultTableModel model = (DefaultTableModel) table.getModel();

				String arr[] = new String[4]; // person필드 담을 배열을 만든다. (테이블에 추가하기 위함)

				// 필드를 String형으로 바꾸어 배열에 담는다.
				String field_name = text1.getText(); // 이름 필드 자료형이 JTextField이므로 String형으로 바꾼다.
				arr[0] = field_name; // String형으로 바꾼 이름 필드를 배열에 담는다.
				String field_number = text2.getText(); // 전화번호 필드 자료형이 JTextField이므로 String형으로 바꾼다.
				arr[1] = field_number; // String형으로 바꾼 전화 번호 필드를 배열에 담는다.
				String field_address = text3.getText(); // 주소 필드 자료형이 JTextField이므로 String형으로 바꾼다.
				arr[2] = field_address; // String형으로 바꾼 주소 필드를 배열에 담는다.
				String field_email = text4.getText(); // 이메일 필드 자료형이 JTextField이므로 String형으로 바꾼다.
				arr[3] = field_email; // String형으로 바꾼 이메일 필드를 배열에 담는다.

				// 모든 필드에 데이터를 입력했는지 확인
				if ((field_name.equals("")) || (field_number.equals("")) || (field_address.equals(""))
						|| (field_email.equals(""))) {
					JOptionPane.showMessageDialog(null, "모든 데이터를 입력하세요");
					return;
				}

				// 동명이인 확인
				if (ad.checkName(field_name) == true) { // String형으로 바꾼 이름필드를 기존에 저장된 값과 비교해준다.
					JOptionPane.showMessageDialog(null, "이미 등록된 이름입니다. 이름 뒤에 숫자를 붙여 저장하세요. (ex:박강현1, 박강현2 ,박강현3)"); // 알림창을
																													// 띄운다.
					return; // 동명이인이 있으면 return하여 함수에서 나온다. 필드를 다시 입력해주고 추가 버튼을 눌러줘야 한다.
				}

				// 겹치는 전화번호 확인
				if (ad.checkPhoneNum(field_number) == true) {// String형으로 바꾼 전화번호 필드를 기존에 저장된 값과 비교해준다.
					JOptionPane.showMessageDialog(null, "이미 등록된 번호입니다. 다른 번호를 입력해주세요."); // 알림창을 띄운다.
					return; // 이미 등록된 번호가 있으면 return하여 함수에서 나온다. 필드를 다시 입력해주고 추가 버튼을 눌러줘야 한다.
				}

				// 입력한 값으로 테이블 값 변경하기
				model.setValueAt(field_name, row, 0);
				model.setValueAt(field_number, row, 1);
				model.setValueAt(field_address, row, 2);
				model.setValueAt(field_email, row, 3);

				// 값 수정
				p = new Person(field_name, field_number, field_address, field_email); // 수정할 값으로 person 객체를 만든다.
				ad.modify(row, p); // row번째 리스트의 객체를 새로 만든 객체 p로 수정한다.
				JOptionPane.showMessageDialog(null, "주소록이 수정되었습니다.");

				// 텍스트박스 값을 비우기
				text1.setText("");
				text2.setText("");
				text3.setText("");
				text4.setText("");

			}

		}
	}

	// 주소록 검색 메소드
	class SearchActionListener implements ActionListener {
		JTable table;
		JComboBox strCombo;
		JTextField text5;

		SearchActionListener(JTable table, JTextField text5, JComboBox strCombo) {
			this.table = table;
			this.text5 = text5;
			this.strCombo = strCombo;

		}

		public void actionPerformed(ActionEvent e) {
			// 콤보박스 옵션 선택(이름 또는 전화번호)
			String option = strCombo.getSelectedItem().toString();

			if (option.equals("이름")) {
				String field_name = text5.getText(); // 검색할 이름을 String형으로 변환한다.
				try {
					ResultSet correct_name = ad.searchName(field_name); // 검색한 이름이 존재한다면 리스트의 인덱스를 return

					int index = correct_name.getRow();
					// 해당 인덱스의 필드 값을 string에 넣어준다.
					String searchperson = "이름: " + ad.getPerson(index).getName() + "   전화번호: "
							+ ad.getPerson(index).getPhoneNum() + "   주소: " + ad.getPerson(index).getAddress()
							+ "   이메일: " + ad.getPerson(index).getEmail();
					// 알림창을 통해 검색한 결과를 출력한다.
					JOptionPane.showMessageDialog(null, searchperson);

				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "찾으시는 내용이 없습니다.");
				}
			}

			if (option.equals("전화번호")) {
				String field_number = text5.getText(); // 검색할 번호를 String형으로 변환한다.
				try {
					ResultSet correct_number = ad.searchPhoneNum(field_number); // 검색한 번호가 존재한다면 리스트의 인덱스를 return

					int index = correct_number.getRow();
					// 해당 인덱스의 필드 값을 string에 넣어준다.
					String searchperson = "이름: " + ad.getPerson(index).getName() + "   전화번호: "
							+ ad.getPerson(index).getPhoneNum() + "   주소: " + ad.getPerson(index).getAddress()
							+ "   이메일: " + ad.getPerson(index).getEmail();
					// 알림창을 통해 검색한 결과를 출력한다.
					JOptionPane.showMessageDialog(null, searchperson);

				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "찾으시는 내용이 없습니다.");
				}
			}

			// 텍스트필드를 지워준다.
			text5.setText("");

		}
	}

}

public class GUITest {
	public static void main(String[] args) throws Exception {

		GUI g = new GUI(); // 위에서 만든 GUI객체를 생성한다.
	}
}
