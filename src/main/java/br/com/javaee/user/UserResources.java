package br.com.javaee.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.server.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.javaee.db.DBUtil;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResources {
	
	// Método para listar todos os usuários
	@GET
	public Response getAllUsers() {
		try (Connection connection = DBUtil.getConnection();
			 Statement statement = connection.createStatement()) {
			
			List<User> users = new ArrayList<>();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM pessoa");
			
			while (resultSet.next()) {
				User user = new User(
						resultSet.getInt("id"),
						resultSet.getString("name"),
						resultSet.getInt("idade")
						);
				users.add(user);
			}
			
			return Response.ok(users).build();
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.serverError().build();
		} 
		
	}
	
	// Método para adicionar um novo usuário
	@POST
	public Response addUser(User user) {
		try(Connection connection = DBUtil.getConnection();
			PreparedStatement preparedStatement = connection
					.prepareStatement("INSERT INTO pessoa (nome, idade) " // TODO deixar o id numeração automática
							+ "VALUES (?, ?", Statement.RETURN_GENERATED_KEYS)){
			
			preparedStatement.setString(1, user.getNome());
			preparedStatement.setInt(2, user.getIdade());
			
			int affectedRows = preparedStatement.executeUpdate();
			if (affectedRows == 0) {
				throw new SQLException("Creating user failed, no rows affected.");
				
			}
			
			try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					int newUserId = generatedKeys.getInt(1);
					user.setId(newUserId);
					return Response.status(Response.Status.CREATED).entity(user).build();
				} else {
					throw new SQLException("Creating user failed, no ID obtaine.");
				}
			}
			
			} catch (SQLException e) {
				e.printStackTrace();
				return Response.serverError().build();
				
			}
		}
	
	// Método para atualizar os dados de um usuário
	@PUT
	@Path("/{id}")
	public Response updateUser(@PathParam("id") int id, User updatedUser) {
		try (Connection connection = DBUtil.getConnection();
			 PreparedStatement preparedStatement = connection
					 .prepareStatement("UPDATE pessoa SET nome = ?, idade = ? WHERE id = ?")) {
			
			preparedStatement.setString(1, updatedUser.getNome());
			preparedStatement.setInt(2, updatedUser.getIdade());
			preparedStatement.setInt(3, id);
			
			int affectedRows = preparedStatement.executeUpdate();
			if (affectedRows == 0) {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
			
			updatedUser.setId(id);
			return Response.ok(updatedUser).build();
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.serverError().build();
		}
	}
	
	// Método para excluir um usuário
	@DELETE
	@Path("/{id}")
	public Response deleteUser(@PathParam("id") int id) {
		try (Connection connection = DBUtil.getConnection();
			 PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM users WHERE id = ?")) {
			
			preparedStatement.setInt(1, id);
			
			int affectedRows = preparedStatement.executeUpdate();
			if (affectedRows == 0) {
				return Response.status(Response.Status.NOT_FOUND).build();
				
			}
			
			return Response.noContent().build();
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.serverError().build();
		}
	}
	
}
