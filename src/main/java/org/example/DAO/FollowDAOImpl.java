package org.example.DAO;

import org.example.model.User;
import org.example.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FollowDAOImpl implements FollowDAO {
    private final DatabaseConnection databaseConnection = new DatabaseConnection();

    @Override
    public void follow(User follower, User following) {
        String sql = "INSERT INTO follows (follower_id, following_id) VALUES (?, ?)";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, follower.getId());
            statement.setLong(2, following.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unfollow(User follower, User following) {
        if (follower == null || following == null || follower.getId() == null || following.getId() == null) {
            throw new IllegalArgumentException("Follower or following user is null or has null ID");
        }
        
        String sql = "DELETE FROM follows WHERE follower_id = ? AND following_id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, follower.getId());
            statement.setLong(2, following.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<User> findFollowing(User user) {
        List<User> followingUsers = new ArrayList<>();
        String sql = "SELECT following_id FROM follows WHERE follower_id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, user.getId());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long followingId = resultSet.getLong("following_id");
                User followingUser = new User(); // Fetch user details based on followingId
                followingUser.setId(followingId);
                followingUsers.add(followingUser);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return followingUsers;
    }

    @Override
    public List<User> findFollowers(User user) {
        List<User> followers = new ArrayList<>();
        String sql = "SELECT follower_id FROM follows WHERE following_id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, user.getId());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long followerId = resultSet.getLong("follower_id");
                User followerUser = new User(); // Fetch user details based on followerId
                followerUser.setId(followerId);
                followers.add(followerUser);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return followers;
    }
}