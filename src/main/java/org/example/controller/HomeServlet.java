package org.example.controller;

// Nhập các thư viện cần thiết để xử lý Servlet, session, và truy vấn dữ liệu bài viết
import jakarta.servlet.ServletException; // Xử lý ngoại lệ liên quan đến Servlet
import jakarta.servlet.annotation.WebServlet; // Định nghĩa URL mà Servlet sẽ xử lý
import jakarta.servlet.http.HttpServlet; // Lớp cơ sở để tạo Servlet xử lý HTTP
import jakarta.servlet.http.HttpServletRequest; // Nhận thông tin yêu cầu từ client (trình duyệt)
import jakarta.servlet.http.HttpServletResponse; // Gửi phản hồi từ server về client
import jakarta.servlet.http.HttpSession; // Quản lý phiên làm việc (session) của người dùng
import org.example.DAO.PostsDAO; // Giao diện để truy vấn dữ liệu bài viết
import org.example.DAO.PostsDAOImpl; // Lớp triển khai cụ thể của PostsDAO
import org.example.model.Posts; // Lớp mô hình đại diện cho thông tin bài viết
import org.example.model.User; // Lớp mô hình đại diện cho thông tin người dùng
import java.io.IOException; // Xử lý ngoại lệ liên quan đến đầu vào/đầu ra
import java.util.List; // Lớp để lưu danh sách bài viết

// Định nghĩa Servlet và ánh xạ tới URL "/home" để hiển thị trang chủ
@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    // Tác dụng: Định nghĩa số lượng bài viết hiển thị trên mỗi trang (hằng số)
    private static final int POSTS_PER_PAGE = 5;
    // Tác dụng: Khởi tạo đối tượng PostsDAO để truy vấn danh sách bài viết từ cơ sở dữ liệu
    private PostsDAO postsDAO = new PostsDAOImpl();

    // Phương thức doGet xử lý yêu cầu GET để hiển thị trang chủ với danh sách bài viết
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Tác dụng: Lấy session hiện tại (nếu có) để kiểm tra người dùng đã đăng nhập chưa
        // false: Không tạo session mới nếu chưa có
        HttpSession session = request.getSession(false);
        // Tác dụng: Lấy thông tin người dùng hiện tại từ session (nếu đã đăng nhập), null nếu chưa
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        // Tác dụng: Xác định trang hiện tại (mặc định là 1) để phân trang danh sách bài viết
        int page = 1;
        // Tác dụng: Lấy tham số "page" từ URL (ví dụ: /home?page=2) để biết người dùng đang ở trang nào
        String pageStr = request.getParameter("page");
        // Tác dụng: Nếu tham số page tồn tại và không rỗng, chuyển thành số nguyên để sử dụng
        if (pageStr != null && !pageStr.isEmpty()) {
            page = Integer.parseInt(pageStr);
        }

        // Tác dụng: Tính toán vị trí bắt đầu (offset) trong cơ sở dữ liệu dựa trên trang hiện tại
        // Ví dụ: Trang 1 -> offset = 0, Trang 2 -> offset = 5
        int offset = (page - 1) * POSTS_PER_PAGE;
        // Tác dụng: Lấy danh sách bài viết từ cơ sở dữ liệu dựa trên offset, số lượng mỗi trang, và người dùng hiện tại
        List<Posts> posts;
        String searchQuery = request.getParameter("search_query");
        if (searchQuery == null || searchQuery.isEmpty() || searchQuery.isBlank()) {
        	posts = postsDAO.findAll(offset, POSTS_PER_PAGE, currentUser);
        } else {
        	posts = postsDAO.search(offset, POSTS_PER_PAGE, currentUser, searchQuery);
        }

        // Tác dụng: Lưu danh sách bài viết vào request để truyền sang trang JSP hiển thị
        request.setAttribute("posts", posts);
        // Tác dụng: Lưu số trang hiện tại vào request để JSP biết đang ở trang nào
        request.setAttribute("currentPage", page);
        // Tác dụng: Tính tổng số trang dựa trên số bài viết và lưu vào request
        // Math.ceil: Làm tròn lên để đảm bảo đủ trang cho tất cả bài viết
        request.setAttribute("totalPages", (int) Math.ceil((double) posts.size() / POSTS_PER_PAGE));

        // Tác dụng: Chuyển tiếp (forward) yêu cầu đến file home.jsp để hiển thị giao diện trang chủ
        // JSP sẽ sử dụng dữ liệu từ request để render danh sách bài viết
        request.getRequestDispatcher("/home.jsp").forward(request, response);
    }
}