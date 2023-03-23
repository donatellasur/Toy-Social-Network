package repository.database;


import domain.Friendship;
import domain.validator.FriendshipValidator;
import repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class FriendshipDBRepo implements Repository<Friendship, Long> {
    private final JDBCUtils jdbcUtils = new JDBCUtils();
    private FriendshipValidator validator;

    public FriendshipDBRepo(FriendshipValidator validator) {
        this.validator = validator;
    }

    @Override
    public Friendship findOne(Long aLong) {
        String query = "SELECT * FROM friendships WHERE \"id\" = ?";
        Friendship friendship   = null;
        try (Connection connection = jdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);

        ) {
            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                Long idUser1 = resultSet.getLong("user1");
                Long idUser2 = resultSet.getLong("user2");
                Timestamp date = resultSet.getTimestamp("friendsFrom");
                LocalDateTime friendsFrom = new Timestamp(date.getTime()).toLocalDateTime();
                friendship = new Friendship(idUser1,idUser2,friendsFrom);
                friendship.setId(aLong);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return friendship;
    }

    @Override
    public Iterable<Friendship> findAll() {
        String query = "SELECT * FROM friendships";
        List<Friendship> friendships = new ArrayList<>();
        try (Connection connection = jdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery();
        ) {
            while(resultSet.next()){
                Long id = resultSet.getLong("id");
                Long idUser1 = resultSet.getLong("user1");
                Long idUser2 = resultSet.getLong("user2");
                Timestamp date = resultSet.getTimestamp("friendsFrom");
                LocalDateTime friendsFrom = LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.ofHours(0));
                Friendship friendship = new Friendship(idUser1,idUser2,friendsFrom);
                friendship.setId(id);
                friendships.add(friendship);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return friendships;
    }

    public Iterable<Friendship> findAllForOne(Long id){
        String query = "SELECT * FROM friendships WHERE \"user1\" = ? OR \"user2\" = ?";
        List<Friendship> friendships = new ArrayList<>();
        try (Connection connection = jdbcUtils.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();
        ){
            while(resultSet.next()){
                Long idUser1 = resultSet.getLong("user1");
                Long idUser2 = resultSet.getLong("user2");
                Timestamp date = resultSet.getTimestamp("friendsFrom");
                LocalDateTime friendsFrom = LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.ofHours(0));
                Friendship friendship = new Friendship(idUser1,idUser2,friendsFrom);
                friendship.setId(id);
                friendships.add(friendship);
            }

        }catch (SQLException ex){
            ex.printStackTrace();
        }
        return friendships;
    }

    @Override
    public Friendship save(Friendship entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        validator.validate(entity);

        String query = "INSERT INTO friendships(\"id\",\"user1\",\"user2\",\"friendsFrom\") VALUES (?,?,?,?)";
        try (Connection connection = jdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setLong(1, entity.getId());
            statement.setLong(2, entity.getIdUser1());
            statement.setLong(3, entity.getIdUser2());
            Timestamp friendsFrom = Timestamp.valueOf(entity.getFriendsFrom());
            statement.setTimestamp(4, friendsFrom);
            statement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return entity;
    }

    @Override
    public Friendship delete(Long aLong) {
        String query = "DELETE FROM friendships WHERE \"id\" = ?";
        Friendship friendship = findOne(aLong);
        try (Connection connection = jdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setLong(1, aLong);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return friendship;
    }

    @Override
    public Friendship update(Friendship entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        validator.validate(entity);
        if(findOne(entity.getId()) == null)
            throw new IllegalArgumentException("The entity does not exist!");

        String query = "UPDATE friendships SET \"user1\" = ?,\"user2\" = ?,\"friendsFrom\" = ? WHERE \"id\" = ?";
        try (Connection connection = jdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setLong(1, entity.getIdUser1());
            statement.setLong(2, entity.getIdUser2());
            Timestamp friendsFrom = Timestamp.valueOf(entity.getFriendsFrom());
            statement.setTimestamp(3, friendsFrom);
            statement.setLong(4, entity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }
}
