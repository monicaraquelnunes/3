package br.com.javaee.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
	
	private static final String URL = "jdbc:mysql://localhost:3306/seu_banco_de_dados";
	private static final String USER = "seu_usuario";
	private static final String PASSWORD = "sua_senha";
	
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASSWORD);
	}
}
