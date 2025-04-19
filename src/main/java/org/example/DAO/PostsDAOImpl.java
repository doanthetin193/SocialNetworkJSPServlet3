package org.example.DAO;

import org.example.model.Posts;
import org.example.util.DatabaseConnection;
import org.example.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostsDAOImpl implements PostsDAO {
    private final DatabaseConnection databaseConnection = new DatabaseConnection();
    private final FollowDAO followDAO = new FollowDAOImpl();

    @Override
    public List<Posts> findAll(int offset, int limit, User currentUser) {
        List<Posts> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts WHERE status = 'ACTIVE' ORDER BY created_at DESC LIMIT ? OFFSET ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            statement.setInt(2, offset);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Posts post = mapRowToPost(resultSet);
                if (currentUser != null && post.getUser() != null) {
                    post.getUser().setFollowedByCurrentUser(isUserFollowing(currentUser, post.getUser()));
                }
                posts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public void save(Posts post) {
        String sql = "INSERT INTO posts (title, body, user_id, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getBody());
            statement.setLong(3, post.getUser().getId());
            statement.setString(4, post.getStatus());
            statement.setTimestamp(5, Timestamp.valueOf(post.getCreatedAt()));
            statement.setTimestamp(6, Timestamp.valueOf(post.getUpdatedAt()));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Posts findById(Long id) {
        String sql = "SELECT * FROM posts WHERE id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapRowToPost(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM posts WHERE id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Posts> findAll() {
        List<Posts> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts ORDER BY created_at DESC";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                posts.add(mapRowToPost(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public void update(Posts post) {
        String sql = "UPDATE posts SET title = ?, body = ?, updated_at = ?, status = ? WHERE id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getBody());
            statement.setTimestamp(3, Timestamp.valueOf(post.getUpdatedAt()));
            statement.setString(4, post.getStatus());
            statement.setLong(5, post.getId());
            
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Posts mapRowToPost(ResultSet resultSet) throws SQLException {
        Posts post = new Posts();
        post.setId(resultSet.getLong("id"));
        post.setTitle(resultSet.getString("title"));
        post.setBody(resultSet.getString("body"));
        post.setStatus(resultSet.getString("status"));
        post.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        
        // Lấy thông tin người dùng
        Long userId = resultSet.getLong("user_id");
        User user = getUserDetails(userId); // Phương thức để lấy thông tin chi tiết của người dùng
        post.setUser(user);
        
        return post;
    }

    private User getUserDetails(Long userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setUsername(resultSet.getString("username"));
                user.setRole(resultSet.getString("role"));
                // Thiết lập các thuộc tính khác nếu cần
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new User(userId); // Trả về user với chỉ có id nếu không tìm thấy thông tin chi tiết
    }

    // Phương thức kiểm tra xem currentUser có đang follow postUser hay không
    private boolean isUserFollowing(User currentUser, User postUser) {
        if (currentUser == null || postUser == null) {
            return false;
        }
        
        String sql = "SELECT COUNT(*) FROM follows WHERE follower_id = ? AND following_id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, currentUser.getId());
            statement.setLong(2, postUser.getId());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

	@Override
	public List<Posts> search(int offset, int limit, User currentUser, String searchQuery) {
		List<Posts> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts WHERE status = 'ACTIVE' and title like ? ORDER BY created_at DESC LIMIT ? OFFSET ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
        	statement.setString(1, "%" + searchQuery + "%");
            statement.setInt(2, limit);
            statement.setInt(3, offset);
            System.out.println(statement);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Posts post = mapRowToPost(resultSet);
                if (currentUser != null && post.getUser() != null) {
                    post.getUser().setFollowedByCurrentUser(isUserFollowing(currentUser, post.getUser()));
                }
                posts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
	}
}