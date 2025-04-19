package org.example.controller;

// Nhập các công cụ cần thiết để xử lý trang web và dữ liệu
import jakarta.servlet.ServletException; // Dùng để xử lý lỗi khi trang web gặp vấn đề
import jakarta.servlet.annotation.WebServlet; // Dùng để gắn địa chỉ URL cho trang web
import jakarta.servlet.http.HttpServlet; // Công cụ chính để xử lý các yêu cầu từ người dùng
import jakarta.servlet.http.HttpServletRequest; // Lấy thông tin mà người dùng gửi đến (như URL)
import jakarta.servlet.http.HttpServletResponse; // Gửi thông tin trả lại cho người dùng (như trang web hoặc lỗi)
import org.example.DAO.PostsDAO; // Công cụ để lấy danh sách bài viết từ cơ sở dữ liệu
import org.example.DAO.PostsDAOImpl; // Phần thực hiện cụ thể của PostsDAO
import org.example.model.Posts; // Đại diện cho thông tin của một bài viết (tiêu đề, nội dung, v.v.)
import java.io.IOException; // Xử lý lỗi khi gửi hoặc nhận dữ liệu
import java.util.List; // Dùng để lưu danh sách bài viết

// Gắn địa chỉ "/posts" cho trang web này, xử lý yêu cầu khi người dùng truy cập "/posts"
@WebServlet("/posts")
public class PostListServlet extends HttpServlet {
    // Tác dụng: Tạo sẵn một công cụ (PostsDAO) để lấy bài viết từ cơ sở dữ liệu
    // Giúp trang web dễ dàng lấy danh sách bài viết khi người dùng yêu cầu
    private PostsDAO postsDAO = new PostsDAOImpl();

    // Tác dụng: Xử lý khi người dùng truy cập trang (như gõ "/posts" hoặc nhấp liên kết)
    // Lấy tất cả bài viết và hiển thị trên trang posts.jsp
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Tác dụng: Lấy toàn bộ bài viết từ cơ sở dữ liệu để chuẩn bị hiển thị
        List<Posts> posts = postsDAO.findAll();
        // Tác dụng: Gửi danh sách bài viết cho trang JSP để nó có thể hiển thị
        request.setAttribute("posts", posts);
        // Tác dụng: Chuyển sang trang posts.jsp để vẽ giao diện và cho người dùng xem danh sách bài viết
        request.getRequestDispatcher("/posts.jsp").forward(request, response);
    }
}