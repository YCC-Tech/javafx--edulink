package models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import javafx.scene.image.Image;

public class StudentModel {
	
	private final DBConnection dbConnection;
	private Connection connection;
	private ResultSet resultSet;
	private PreparedStatement pStmt;
	private Statement stmt;
	
	private FileInputStream fis;

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
	
	public boolean saveStudent(Student student) throws SQLException, FileNotFoundException {
		var isSave = true;
		connection = DBConnection.getConnection();
		
		pStmt = connection.prepareStatement("INSERT INTO `students` "
				+ "(`name`, `gender`, `nrc`, `birthday`, `phone`, "
				+ "`address`, "+ "`hostel_address`, `religion_id`,`township_id`,`ethcinity_id`,`photo_url`) "
				+ "VALUES (?, ?, ?, ?, ?, ?,?,?,?,?,?);"
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
		
		fis = new FileInputStream(student.getImageFile());
		pStmt.setBinaryStream(11, (InputStream)fis, (int)student.getImageFile().length());
		
		isSave = pStmt.execute();
//		connection.close();

		return isSave;
		
		
	}
	
	public Student getStudent(int student_id) throws SQLException, FileNotFoundException{
		
		Image image=new Image("file:photo.jpg");
			
		Student student = null;
			
			String sql = "select * from students where student_id = '" + student_id + "';";

			try (PreparedStatement statement = connection.prepareStatement(sql)) {
				ResultSet resultSet = statement.executeQuery();
				
				while(resultSet.next()) {
					
					//get default of image not from db
					
					if(resultSet.getBinaryStream("photo_url")==null) {
						//E:\NTT_AT\Scholarship\IT_training\Edu Foundataion MS\Images
						File file = new File("../image/pathStatic.jpg");
						image = new Image(file.toURI().toString());
						
					}
					else {
						InputStream is = resultSet.getBinaryStream("photo_url");
						OutputStream os = new FileOutputStream(new File("photo.jpg"));
						byte[] content = new byte[1024];
						int size = 0;
						try {
							while((size= is.read(content))!=-1) {
								os.write(content, 0, size);
								
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						try {
							os.close();
							is.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
						
						
					image = new Image("file:photo.jpg");
					
					student = new Student(resultSet.getInt("student_id"),
							resultSet.getString("name"),
							resultSet.getInt("gender"),
							resultSet.getString("nrc"),
							resultSet.getString("birthday"),
							resultSet.getString("phone").toString(),
							resultSet.getString("address"),
							resultSet.getString("hostel_address"),
							resultSet.getInt("religion_id"),
							resultSet.getInt("township_id"),
							resultSet.getInt("ethcinity_id"),
							image);
							
				}

			} 
			catch (SQLException e) {
				System.out.println(e.getMessage());
			}
			
			return student;
		}
	

	public ObservableList<Student> getStudentList() throws SQLException{
		
		ObservableList<Student> students = FXCollections.observableArrayList();
		
		String sql = "SELECT "
				+ "s.*, "
				+ "u.short_name as university, "
				+ "a.name as attendance_year, "
				+ "m.short_name as major "
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

	public ObservableList<Student> getStudentInfoForUpdate(int studentId) {
		
		ObservableList<Student> student = FXCollections.observableArrayList();
		
		String sql = "SELECT "
				+ "s.*, "
				+ "e.name as ethcinity, "
				+ "r.name as religion, "
				+ "t.name as township, "
				+ "re.name as region "
				+ "FROM students s "
				+ "JOIN ethcinities e ON e.ethcinity_id = s.ethcinity_id "
				+ "JOIN religions r ON r.religion_id = s.religion_id "
				+ "JOIN townships t ON t.township_id = s.township_id "
				+ "JOIN regions re ON re.region_id = t.region_id "
				+ "WHERE s.student_id = " + studentId + ";";

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			resultSet = statement.executeQuery();
			
			while (resultSet.next()) {
				student.add(new Student(
						resultSet.getString("name"),
						resultSet.getInt("gender"),
						resultSet.getString("nrc"),
						resultSet.getString("birthday").toString(),
						resultSet.getString("phone").toString(),
						resultSet.getString("address"),
						resultSet.getString("hostel_address"),
						resultSet.getString("religion"),
						resultSet.getString("region"),
						resultSet.getString("township"),
						resultSet.getString("ethcinity")
						)
						);
					}
				} 
				catch (SQLException e) {
					System.out.println(e.getMessage());
				}
		
		return student;
	}

	public int updateStudent(Student student, int studentId) throws FileNotFoundException, SQLException {
		
		connection = DBConnection.getConnection();

		int updated = 0;

		pStmt = connection.prepareStatement("UPDATE `students` SET "
				+ "`name` = ?,"
				+ " `gender` = ?, "
				+ "`nrc` = ?,"
				+ " `birthday` = ?, "
				+ "`phone` = ?, "
				+ "`address` = ?, "
				+ "`hostel_address` = ?, "
				+ "`religion_id` = ?, "
				+ "`township_id` = ?, "
				+ "`ethcinity_id` = ?, "
				+ "`photo_url` = ? "
				+ "WHERE (`student_id` = ?);"
				);
		
		pStmt.setString(1, student.getName());
		pStmt.setInt(2, student.getGender());
		pStmt.setString(3, student.getNrc());
		pStmt.setString(4, student.getBirthday());
		pStmt.setString(5, student.getPhone());
		pStmt.setString(6, student.getAddress());
		pStmt.setString(7, student.getHostel_address());
		pStmt.setInt(8, student.getReligion_id());
		pStmt.setInt(9, student.getTownship_id());
		pStmt.setInt(10, student.getEthcinity_id());
		
		fis = new FileInputStream(student.getImageFile());
		pStmt.setBinaryStream(11, (InputStream)fis, (int)student.getImageFile().length());
		pStmt.setInt(12, studentId);
		

		updated = pStmt.executeUpdate();
//		connection.close();

		return updated;
	}
}