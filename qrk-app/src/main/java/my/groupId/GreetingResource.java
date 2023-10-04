package my.groupId;

import io.agroal.api.AgroalDataSource;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("/api/hello")
public class GreetingResource {

    @Inject
    AgroalDataSource dataSource;

    @GET
    public GreetingResponse hello() {
        return new GreetingResponse("Hello there", 21);
    }

    @GET
    @Path("/users-add")
    public void addUser(@RestQuery Long id, @RestQuery String name, @RestQuery Integer age) {
        try (
                var connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("insert into users(id_, name_, age_) values (?, ?, ?)");
        ) {
            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setInt(3, age);
            preparedStatement.executeUpdate();
            System.out.println("inserted record");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @GET
    @Path("/users")
    public List<UserResponse> getUsers() {
        try (
                var connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("select * from users");
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<UserResponse> list = new ArrayList<>();
            while (resultSet.next()) {
                var id = resultSet.getLong("id_");
                var name = resultSet.getString("name_");
                var age = resultSet.getInt("age_");
                list.add(new UserResponse(id, name, age));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @POST
    public UserResponse receiveData(@Valid UserRequest request) {
        System.out.println("Received data: " + JsonObject.mapFrom(request).encodePrettily());
        return new UserResponse(1L, "Bob", 1);
    }
}
