package models;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import database.DBConnection;
import dto.Student;
import dto.University1;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class StudentModel {
	
	private final DBConnection dbConnection;
	private Connection connection;
	private ResultSet resultSet;
	private PreparedStatement pStmt;
	private Statement stmt;

	public StudentModel() {
		super();
		this.dbConnection = new DBConnection();
		this.connection = DBConnection.getConnection();
	}
	
	/* Get latest Id of Student table*/
	public Integer getLatestStuId() throws SQLException {
		ResultSet rs;
		int latestId = 0;
		stmt = connection.createStatement();
		rs = stmt.executeQuery("select max(student_id)+1 as max_id from students;");
		while(rs.next()) {
			latestId=rs.getInt("max_id");
		}
		
		return latestId;	
	}
	
	public boolean saveStudent(Student student) throws SQLException {
		var isSave = true;
		connection = DBConnection.getConnection();
		
		System.out.println("before");
		
		pStmt = connection.prepareStatement("INSERT INTO `students` "
				+ "(`name`, `gender`, `nrc`, `birthday`, `phone`, "
				+ "`address`, "+ "`hostel_address`, `religion_id`,`township_id`,`ethcinity_id`) "
				+ "VALUES (?, ?, ?, ?, ?, ?,?,?,?,?);"
				);
		pStmt.setString(1, student.getName());
		pStmt.setInt(2, student.getGender());
		pStmt.setString(3, student.getNrc());
		
		LocalDate date = LocalDate.parse(student.getBirthday());
		Date birthday = Date.valueOf(date);
		pStmt.setDate(4, birthday);
		
		pStmt.setString(5, student.getPhone());
		pStmt.setString(6, student.getAddress());
		pStmt.setString(7, student.getHostel_address());
		pStmt.setInt(8, student.getReligion_id());
		pStmt.setInt(9, student.getTownship_id());
		pStmt.setInt(10, student.getEthcinity_id());
		
		isSave = pStmt.execute();
		connection.close();

		return isSave;
		
		
	}

	
	public ObservableList<Student> getStudentList() throws SQLException{
		
		ObservableList<Student> students = FXCollections.observableArrayList();
		
		String sql = "SELECT "
				+ "s.*, "
				+ "u.name as university, "
				+ "a.name as attendance_year, "
				+ "m.name as major "
				+ "FROM students s "
				+ "JOIN enrollments e ON s.student_id = e.student_id "
				+ "JOIN attendance_years a ON e.attendance_year_id = a.attendance_year_id "
				+ "JOIN universities u ON a.university_id = u.university_id "
				+"JOIN majors m ON e.major_id = m.major_id "
				+ "WHERE s.student_id = e.student_id "
				+ "GROUP BY student_id;";

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
				students.add(new Student(
						resultSet.getInt("student_id"),
						resultSet.getString("name"),
						resultSet.getString("phone").toString(),
						resultSet.getString("attendance_year").toString(),
						resultSet.getString("major").toString(),
						resultSet.getString("university").toString()
						)
						);
					}
				} 
				catch (SQLException e) {
					System.out.println(e.getMessage());
				}
				
				return students;
			}
	
	public ObservableList<Student> getStudentList(int university_id,int attendance_year_id) throws SQLException{
		
		ObservableList<Student> students = FXCollections.observableArrayList();
		
		String sql = "SELECT "
				+ "s.*, "
				+ "u.name as university, "
				+ "a.name as attendance_year, "
				+ "m.name as major "
				+ "FROM students s "
				+ "JOIN enrollments e ON s.student_id = e.student_id "
				+ "JOIN attendance_years a ON e.attendance_year_id = a.attendance_year_id "
				+ "JOIN universities u ON a.university_id = u.university_id "
				+"JOIN majors m ON e.major_id = m.major_id "
				+ "WHERE s.student_id = e.student_id "
				+ "GROUP BY student_id;";

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
				students.add(new Student(
						resultSet.getInt("student_id"),
						resultSet.getString("name"),
						resultSet.getString("phone").toString(),
						resultSet.getString("attendance_year").toString(),
						resultSet.getString("major").toString(),
						resultSet.getString("university").toString()
						)
						);
					}
				} 
				catch (SQLException e) {
					System.out.println(e.getMessage());
				}
				
				return students;
			}
		}