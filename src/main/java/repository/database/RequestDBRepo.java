package repository.database;

import domain.Request;
import domain.validator.RequestValidator;
import repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RequestDBRepo implements Repository<Request, Long> {
    private final JDBCUtils jdbcUtils = new JDBCUtils();
    private RequestValidator validator;
    public RequestDBRepo(RequestValidator validator) {
        this.validator = validator;
    }

    @Override
    public Request findOne(Long aLong) {
        String query = "SELECT * FROM requests WHERE \"id\" = ?";
        Request request = null;
        try (Connection connection = jdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long idUser1 = resultSet.getLong("idUser1");
                Long idUser2 = resultSet.getLong("idUser2");
                String status = resultSet.getString("status");
//                LocalDateTime requestFrom = new Timestamp(date.getTime()).toLocalDateTime();
                request = new Request(idUser1, idUser2, status);
                request.setId(aLong);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return request;
    }

    @Override
    public Iterable<Request> findAll() {
        List<Request> requests = new ArrayList<>();
        try (Connection connection = jdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM requests");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long idUser1 = resultSet.getLong("idUser1");
                Long idUser2 = resultSet.getLong("idUser2");
                String status = resultSet.getString("status");
                Request request = new Request(idUser1, idUser2, status);
                request.setId(id);
                requests.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    @Override
    public Request save(Request entity) {
        if(entity == null)
            throw new IllegalArgumentException("entity must not be null");
        validator.validate(entity);
        String query = "INSERT INTO requests (\"id\", \"idUser1\", \"idUser2\", \"status\") VALUES (?, ?, ?, ?)";
        try (Connection connection = jdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setLong(1, entity.getId());
            statement.setLong(2, entity.getIdUser1());
            statement.setLong(3, entity.getIdUser2());
            statement.setString(4, entity.getStatus());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }

    @Override
    public Request delete(Long aLong) {
        String query = "DELETE FROM requests WHERE \"id\" = ?";
        Request request = findOne(aLong);
        try (Connection connection = jdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setLong(1, aLong);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return request;
    }

    @Override
    public Request update(Request entity) {
        if(entity == null)
            throw new IllegalArgumentException("entity must not be null");
        validator.validate(entity);
        if(findOne(entity.getId()) == null)
            throw new IllegalArgumentException("entity must exist");
        String query = "UPDATE requests SET \"idUser1\" = ?, \"idUser2\" = ?, \"status\" = ? WHERE \"id\" = ?";
        try (Connection connection = jdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setLong(1, entity.getIdUser1());
            statement.setLong(2, entity.getIdUser2());
            statement.setString(3, entity.getStatus());
            statement.setLong(4, entity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }
}

