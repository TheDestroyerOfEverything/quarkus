package my.groupId;

import io.agroal.api.AgroalDataSource;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jooq.tables.Users;
import org.jboss.resteasy.reactive.RestQuery;
import org.jooq.DSLContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static jooq.tables.Users.USERS;

@Path("/api/hello")
public class GreetingResource {

    @Inject
    AgroalDataSource dataSource;

    @Inject
    DSLContext dsl;

    @GET
    public GreetingResponse hello() {
        return new GreetingResponse("Hello there", 21);
    }


    @PUT
    @Path("/users")
    public void updateUser(UserRequest request) {
        try (
                var connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("UPDATE users SET name_ = ?, age_ = ? WHERE id_ = ?");
        ) {
            preparedStatement.setString(1, request.name()); // Set name
            preparedStatement.setInt(2, request.age());      // Set age
            preparedStatement.setLong(3, request.id());
            preparedStatement.executeUpdate();
            System.out.println("inserted record");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @POST
    @Path("/users")
    public void addUser(UserRequest request) {
        try (
                var connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("insert into users(id_, name_, age_) values (?, ?, ?)");
        ) {
            preparedStatement.setLong(1, request.id());
            preparedStatement.setString(2, request.name());
            preparedStatement.setInt(3, request.age());
            preparedStatement.executeUpdate();
            System.out.println("inserted record");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @DELETE
    @Path("/users/{id}")
    public void deleteUser(@PathParam("id") Long id) {
        try (
                var connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM users WHERE id_ = ?");
        ) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
            System.out.println("deleted record");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @GET
    @Path("/users")
    public List<UserResponse> getUsers() {
        return dsl.selectFrom(USERS)
                .orderBy(USERS.ID)
                .fetch(record -> new UserResponse(
                        record.getId(),
                        record.getName(),
                        record.getAge()
                ));

//        try (
//                var connection = dataSource.getConnection();
//                PreparedStatement preparedStatement = connection.prepareStatement("select * from users order by id_");
//        ) {
//            ResultSet resultSet = preparedStatement.executeQuery();
//            List<UserResponse> list = new ArrayList<>();
//            while (resultSet.next()) {
//                var id = resultSet.getLong("id_");
//                var name = resultSet.getString("name_");
//                var age = resultSet.getInt("age_");
//                list.add(new UserResponse(id, name, age));
//            }
//            return list;
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
    }


    @POST
    public UserResponse receiveData(@Valid UserRequest request) {
        System.out.println("Received data: " + JsonObject.mapFrom(request).encodePrettily());
        return new UserResponse(1L, "Bob", 1);
    }
}
