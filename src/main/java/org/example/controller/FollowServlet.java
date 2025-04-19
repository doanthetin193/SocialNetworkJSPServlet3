package org.example.controller;

// Nhập các thư viện cần thiết để xử lý Servlet, session, và truy vấn dữ liệu
import jakarta.servlet.ServletException; // Xử lý ngoại lệ liên quan đến Servlet
import jakarta.servlet.annotation.WebServlet; // Định nghĩa URL mà Servlet sẽ xử lý
import jakarta.servlet.http.HttpServlet; // Lớp cơ sở để tạo Servlet xử lý HTTP
import jakarta.servlet.http.HttpServletRequest; // Nhận thông tin yêu cầu từ client
import jakarta.servlet.http.HttpServletResponse; // Gửi phản hồi từ server về client
import jakarta.servlet.http.HttpSession; // Quản lý phiên làm việc của người dùng
import org.example.DAO.FollowDAO; // Giao diện để xử lý hành động follow/unfollow
import org.example.DAO.FollowDAOImpl; // Lớp triển khai cụ thể của FollowDAO
import org.example.DAO.UserDAOImpl; // Lớp để truy vấn thông tin người dùng
import org.example.model.User; // Lớp mô hình đại diện cho thông tin người dùng
import java.io.IOException; // Xử lý ngoại lệ liên quan đến đầu vào/đầu ra

// Định nghĩa Servlet và ánh xạ tới URL "/follow/*" để xử lý yêu cầu follow/unfollow
@WebServlet("/follow/*")
public class FollowServlet extends HttpServlet {
    // Tác dụng: Khởi tạo đối tượng FollowDAO để thực hiện các hành động follow/unfollow trong cơ sở dữ liệu
    private FollowDAO followDAO = new FollowDAOImpl();

    // Phương thức doPost xử lý yêu cầu POST để theo dõi (follow) một người dùng
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Tác dụng: Lấy thông tin đường dẫn sau "/follow/" (ví dụ: "/1" từ "/follow/1") để biết ID người dùng cần theo dõi
        String pathInfo = request.getPathInfo();
        // Tác dụng: Lấy session hiện tại (nếu có) để kiểm tra người dùng đã đăng nhập chưa
        HttpSession session = request.getSession(false);
        // Tác dụng: Lấy thông tin người dùng hiện tại từ session, null nếu chưa đăng nhập
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        // Tác dụng: Kiểm tra nếu người dùng chưa đăng nhập, trả về lỗi 401 (Unauthorized)
        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED); // Thông báo không có quyền truy cập
            return;
        }

        try {
            // Tác dụng: Chuyển đổi phần sau "/follow/" (ví dụ: "1") thành số Long để lấy ID người dùng cần theo dõi
            Long followingId = Long.parseLong(pathInfo.substring(1));

            // Tác dụng: Tìm thông tin người dùng cần theo dõi từ cơ sở dữ liệu dựa trên ID
            User followingUser = new UserDAOImpl().findById(followingId);

            // Tác dụng: Kiểm tra nếu người dùng cần theo dõi không tồn tại, trả về lỗi 404 (Not Found)
            if (followingUser == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "User to follow not found");
                return;
            }

            // Tác dụng: Gọi FollowDAO để thực hiện hành động theo dõi, lưu mối quan hệ vào cơ sở dữ liệu
            followDAO.follow(currentUser, followingUser);
            // Tác dụng: Trả về mã trạng thái 200 (OK) để báo hiệu hành động thành công
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (NumberFormatException e) {
            // Tác dụng: Xử lý lỗi nếu ID không hợp lệ (không phải số), trả về lỗi 400 (Bad Request)
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID");
        } catch (Exception e) {
            // Tác dụng: Ném ngoại lệ nếu có lỗi trong quá trình xử lý (ví dụ: lỗi cơ sở dữ liệu)
            throw new ServletException("Error following user", e);
        }
    }

    // Phương thức doDelete xử lý yêu cầu DELETE để bỏ theo dõi (unfollow) một người dùng
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Tác dụng: Lấy thông tin đường dẫn sau "/follow/" (ví dụ: "/1" từ "/follow/1") để biết ID người dùng cần bỏ theo dõi
        String pathInfo = request.getPathInfo();
        // Tác dụng: Lấy session hiện tại (nếu có) để kiểm tra người dùng đã đăng nhập chưa
        HttpSession session = request.getSession(false);
        // Tác dụng: Lấy thông tin người dùng hiện tại từ session, null nếu chưa đăng nhập
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        // Tác dụng: Kiểm tra nếu người dùng chưa đăng nhập, trả về lỗi 401 (Unauthorized)
        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            // Tác dụng: Kiểm tra nếu pathInfo không hợp lệ (rỗng hoặc thiếu ID), trả về lỗi 400 (Bad Request)
            if (pathInfo == null || pathInfo.length() <= 1) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing user ID");
                return;
            }

            // Tác dụng: Chuyển đổi phần sau "/follow/" (ví dụ: "1") thành số Long để lấy ID người dùng cần bỏ theo dõi
            Long followingId = Long.parseLong(pathInfo.substring(1));

            // Tác dụng: Tìm thông tin người dùng cần bỏ theo dõi từ cơ sở dữ liệu dựa trên ID
            User followingUser = new UserDAOImpl().findById(followingId);

            // Tác dụng: Kiểm tra nếu người dùng cần bỏ theo dõi không tồn tại, trả về lỗi 404 (Not Found)
            if (followingUser == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "User to unfollow not found");
                return;
            }

            // Tác dụng: Kiểm tra thông tin của currentUser và followingUser để đảm bảo hợp lệ trước khi bỏ theo dõi
            if (currentUser.getId() == null || followingUser.getId() == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user information");
                return;
            }

            // Tác dụng: Gọi FollowDAO để thực hiện hành động bỏ theo dõi, xóa mối quan hệ khỏi cơ sở dữ liệu
            followDAO.unfollow(currentUser, followingUser);
            // Tác dụng: Trả về mã trạng thái 200 (OK) để báo hiệu hành động thành công
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (NumberFormatException e) {
            // Tác dụng: Xử lý lỗi nếu ID không hợp lệ (không phải số), trả về lỗi 400 (Bad Request)
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID");
        } catch (Exception e) {
            // Tác dụng: Ném ngoại lệ nếu có lỗi trong quá trình xử lý (ví dụ: lỗi cơ sở dữ liệu)
            throw new ServletException("Error unfollowing user", e);
        }
    }
}