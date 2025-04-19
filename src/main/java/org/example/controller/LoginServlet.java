package org.example.controller;

// Nhập các thư viện cần thiết để xử lý Servlet, session, và truy vấn dữ liệu người dùng
import jakarta.servlet.ServletException; // Xử lý ngoại lệ liên quan đến Servlet
import jakarta.servlet.annotation.WebServlet; // Định nghĩa URL mà Servlet sẽ xử lý
import jakarta.servlet.http.HttpServlet; // Lớp cơ sở để tạo Servlet xử lý HTTP
import jakarta.servlet.http.HttpServletRequest; // Nhận thông tin yêu cầu từ client (trình duyệt)
import jakarta.servlet.http.HttpServletResponse; // Gửi phản hồi từ server về client
import jakarta.servlet.http.HttpSession; // Quản lý phiên làm việc (session) của người dùng
import org.example.DAO.UserDAO; // Giao diện để truy vấn dữ liệu người dùng
import org.example.DAO.UserDAOImpl; // Lớp triển khai cụ thể của UserDAO
import org.example.model.User; // Lớp mô hình đại diện cho thông tin người dùng
import java.io.IOException; // Xử lý ngoại lệ liên quan đến đầu vào/đầu ra

// Định nghĩa Servlet và ánh xạ tới URL "/login" để xử lý yêu cầu đăng nhập
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    // Tác dụng: Khởi tạo đối tượng UserDAO để truy vấn thông tin người dùng từ cơ sở dữ liệu
    private UserDAO userDAO = new UserDAOImpl();

    // Phương thức doPost xử lý yêu cầu POST khi người dùng gửi form đăng nhập
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Tác dụng: Lấy thông tin username và password từ form đăng nhập mà người dùng gửi lên
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Tác dụng: Kiểm tra thông tin đăng nhập bằng cách tìm người dùng trong cơ sở dữ liệu
        // userDAO.findByUsernameAndPassword: Trả về đối tượng User nếu thông tin đúng, null nếu sai
        User user = userDAO.findByUsernameAndPassword(username, password);

        // Tác dụng: Kiểm tra nếu thông tin đăng nhập hợp lệ (user không null)
        if (user != null) {
            // Tác dụng: Tạo hoặc lấy session hiện tại để lưu thông tin người dùng trong phiên làm việc
            HttpSession session = request.getSession();
            // Tác dụng: Lưu đối tượng user vào session để xác nhận người dùng đã đăng nhập
            session.setAttribute("user", user);
            // Tác dụng: Lưu vai trò (role) của người dùng để phân quyền truy cập sau này
            session.setAttribute("role", user.getRole());
            // Tác dụng: Chuyển hướng người dùng đến trang phù hợp dựa trên vai trò
            // Nếu role là "ADMIN" thì đến /admin/posts, nếu không thì đến trang chủ (/)
            response.sendRedirect(request.getContextPath() + (user.getRole().equals("ADMIN") ? "/admin/posts" : "/"));
        } else {
            // Tác dụng: Nếu đăng nhập thất bại, lưu thông báo lỗi để hiển thị trên trang đăng nhập
            request.setAttribute("error", "Username không tồn tại!");
            // Tác dụng: Chuyển tiếp (forward) về trang login.jsp để hiển thị lại form với thông báo lỗi
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
//;0p p ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;đăng nhập
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Tác dụng: Chuyển tiếp (forward) yêu cầu đến file login.jsp để hiển thị giao diện đăng nhập
        // Người dùng sẽ thấy form đăng nhập khi truy cập URL /login
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
}