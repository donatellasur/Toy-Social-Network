package repository.database;

import domain.Account;
import domain.User;
import domain.validator.AccountValidator;
import repository.Repository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class AccountDBRepo implements Repository<Account, Long> {
    private final JDBCUtils jdbcUtils = new JDBCUtils();
    private AccountValidator validator;
    public AccountDBRepo(AccountValidator validator) {
        this.validator = validator;
    }


    @Override
    public Account findOne(Long aLong) {
        String query = "SELECT * FROM accounts WHERE \"id\" = ?" ;
        Account account = null;
        try (Connection connection = jdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setLong(1, aLong);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                account = new Account(username, email, password);
                account.setId(aLong);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return account;
    }

    @Override
    public Iterable<Account> findAll() {
        Set<Account> accountsSet = new HashSet<>();
        try(Connection connection= DriverManager.getConnection("jdbc:postgresql://localhost:5432/academic", "postgres", "postgres");
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM accounts");
            ResultSet resultSet = preparedStatement.executeQuery()){
            while(resultSet.next()){
                Long ID = resultSet.getLong("id");
                String username = resultSet.getString("username");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                Account account = new Account(username, email, password);
                account.setId(ID);
                accountsSet.add(account);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return accountsSet;
    }

    @Override
    public Account save(Account entity) {
        if(entity == null){
            throw new IllegalArgumentException("Entity must not be null");
        }
        validator.validate(entity);
        String query = "INSERT INTO accounts (\"id\", \"username\", \"email\", \"password\") VALUES (?, ?, ?, ?)";
        try (Connection connection = jdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setLong(1, entity.getId());
            statement.setString(2, entity.getUsername());
            statement.setString(3, entity.getEmail());
            statement.setString(4, entity.getPassword());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }

    @Override
    public Account delete(Long aLong) {
        String query = "DELETE FROM accounts WHERE \"id\" = ?";
        Account account = findOne(aLong);
        try (Connection connection = jdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setLong(1, aLong);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return account;
    }

    @Override
    public Account update(Account entity) {
        if(entity == null){
            throw new IllegalArgumentException("Entity must not be null");
        }
        validator.validate(entity);
        if(findOne(entity.getId()) == null){
            throw new IllegalArgumentException("Entity must exist");
        }
        String query = "UPDATE accounts SET \"username\" = ?, \"email\" = ?, \"password\" = ? WHERE \"id\" = ?";
        try (Connection connection = jdbcUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setString(1, entity.getUsername());
            statement.setString(2, entity.getEmail());
            statement.setString(3, entity.getPassword());
            statement.setLong(4, entity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entity;
    }
}

