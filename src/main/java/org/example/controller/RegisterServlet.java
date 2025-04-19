package org.example.controller;

// Nhập các thư viện cần thiết để xử lý Servlet và truy vấn dữ liệu người dùng
import jakarta.servlet.ServletException; // Xử lý ngoại lệ liên quan đến Servlet
import jakarta.servlet.annotation.WebServlet; // Định nghĩa URL mà Servlet sẽ xử lý
import jakarta.servlet.http.HttpServlet; // Lớp cơ sở để tạo Servlet xử lý HTTP
import jakarta.servlet.http.HttpServletRequest; // Nhận thông tin yêu cầu từ client (trình duyệt)
import jakarta.servlet.http.HttpServletResponse; // Gửi phản hồi từ server về client
import org.example.model.User; // Lớp mô hình đại diện cho thông tin người dùng
import org.example.DAO.UserDAO; // Giao diện để truy vấn dữ liệu người dùng
import org.example.DAO.UserDAOImpl; // Lớp triển khai cụ thể của UserDAO
import java.io.IOException; // Xử lý ngoại lệ liên quan đến đầu vào/đầu ra
import java.time.LocalDateTime; // Lớp để lấy thời gian hiện tại cho trường createdAt

// Định nghĩa Servlet và ánh xạ tới URL "/register" để xử lý yêu cầu đăng ký
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    // Phương thức doPost xử lý yêu cầu POST khi người dùng gửi form đăng ký
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Tác dụng: Lấy thông tin username và password từ form đăng ký mà người dùng gửi lên
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Tác dụng: Khởi tạo đối tượng UserDAO để tương tác với cơ sở dữ liệu
        UserDAO userDAO = new UserDAOImpl();

        try {
            // Tác dụng: Kiểm tra xem username đã tồn tại trong cơ sở dữ liệu chưa
            try {
                // userDAO.findByUsername: Tìm người dùng theo username, trả về User nếu tồn tại
                User existingUser = userDAO.findByUsername(username);

                // Tác dụng: Nếu username đã tồn tại, thông báo lỗi và hiển thị lại trang đăng ký
                if (existingUser != null) {
                    request.setAttribute("error", "Username đã tồn tại!"); // Lưu thông báo lỗi
                    request.getRequestDispatcher("/register.jsp").forward(request, response); // Chuyển tiếp về trang đăng ký
                    return; // Thoát khỏi phương thức để không tiếp tục xử lý
                }
            } catch (ServletException e) {
                throw new RuntimeException(e); // Ném ngoại lệ nếu có lỗi trong quá trình kiểm tra
            } catch (IOException e) {
                throw new RuntimeException(e); // Ném ngoại lệ nếu có lỗi đầu vào/đầu ra
            }

            // Tác dụng: Tạo một đối tượng User mới để lưu thông tin người dùng vừa đăng ký
            User newUser = new User();
            newUser.setUsername(username); // Gán username từ form
            newUser.setPassword(password); // Gán password từ form
            newUser.setRole("USER"); // Tác dụng: Gán vai trò mặc định là "USER" cho người dùng mới
            newUser.setCreatedAt(LocalDateTime.now()); // Tác dụng: Gán thời gian tạo tài khoản là thời điểm hiện tại

            // Tác dụng: Lưu thông tin người dùng mới vào cơ sở dữ liệu
            userDAO.save(newUser);

            // Tác dụng: Chuyển hướng người dùng đến trang đăng nhập sau khi đăng ký thành công
            response.sendRedirect(request.getContextPath() + "/login");

        } catch (Exception e) {
            // Tác dụng: Xử lý lỗi nếu có vấn đề trong quá trình đăng ký (ví dụ: lỗi cơ sở dữ liệu)
            request.setAttribute("error", "Đã xảy ra lỗi khi đăng ký!"); // Lưu thông báo lỗi
            request.getRequestDispatcher("/register.jsp").forward(request, response); // Chuyển tiếp về trang đăng ký
            e.printStackTrace(); // In chi tiết lỗi ra console để debug
        }
    }

    // Phương thức doGet xử lý yêu cầu GET để hiển thị trang đăng ký
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Tác dụng: Chuyển tiếp (forward) yêu cầu đến file register.jsp để hiển thị giao diện đăng ký
        // Người dùng sẽ thấy form đăng ký khi truy cập URL /register
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }
}