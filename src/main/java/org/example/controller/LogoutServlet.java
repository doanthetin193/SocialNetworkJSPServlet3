package org.example.controller;

// Nhập các thư viện cần thiết để xử lý Servlet và quản lý phiên làm việc
import jakarta.servlet.ServletException; // Xử lý ngoại lệ liên quan đến Servlet
import jakarta.servlet.annotation.WebServlet; // Định nghĩa URL mà Servlet sẽ xử lý
import jakarta.servlet.http.HttpServlet; // Lớp cơ sở để tạo Servlet xử lý HTTP
import jakarta.servlet.http.HttpServletRequest; // Nhận thông tin yêu cầu từ client (trình duyệt)
import jakarta.servlet.http.HttpServletResponse; // Gửi phản hồi từ server về client
import jakarta.servlet.http.HttpSession; // Quản lý phiên làm việc (session) của người dùng
import java.io.IOException; // Xử lý ngoại lệ liên quan đến đầu vào/đầu ra

// Định nghĩa Servlet và ánh xạ tới URL "/logout" để xử lý yêu cầu đăng xuất
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    // Phương thức doGet xử lý yêu cầu GET khi người dùng truy cập URL /logout
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Tác dụng: Lấy session hiện tại của người dùng để kiểm tra xem họ đã đăng nhập chưa
        // request.getSession(false): Không tạo session mới nếu chưa có, chỉ lấy session hiện tại
        HttpSession session = request.getSession(false);

        // Tác dụng: Kiểm tra nếu session tồn tại (người dùng đã đăng nhập), thì hủy bỏ nó
        // Điều này đảm bảo người dùng được đăng xuất bằng cách xóa toàn bộ dữ liệu trong session
        if (session != null) {
            // session.invalidate(): Hủy session, xóa mọi thông tin như "user", "role" được lưu trước đó
            session.invalidate();
        }

        // Tác dụng: Chuyển hướng người dùng về trang đăng nhập sau khi đăng xuất
        // request.getContextPath(): Lấy đường dẫn gốc của ứng dụng (ví dụ: /yourapp)
        // response.sendRedirect(): Gửi lệnh cho trình duyệt chuyển sang URL /login, bắt đầu lại quá trình đăng nhập
        response.sendRedirect(request.getContextPath() + "/login");
    }
}