package org.example.controller;

// Nhập các công cụ cần thiết để xử lý trang web và dữ liệu
import jakarta.servlet.ServletException; // Dùng để xử lý lỗi khi trang web gặp vấn đề
import jakarta.servlet.annotation.WebServlet; // Dùng để gắn địa chỉ URL cho trang web
import jakarta.servlet.http.HttpServlet; // Công cụ chính để xử lý các yêu cầu từ người dùng
import jakarta.servlet.http.HttpServletRequest; // Lấy thông tin mà người dùng gửi đến (như URL)
import jakarta.servlet.http.HttpServletResponse; // Gửi thông tin trả lại cho người dùng (như trang web hoặc lỗi)
import jakarta.servlet.http.HttpSession; // Giữ thông tin người dùng trong suốt phiên truy cập
import org.example.DAO.FollowDAO; // Công cụ để lấy danh sách người theo dõi hoặc đang theo dõi
import org.example.DAO.FollowDAOImpl; // Phần thực hiện cụ thể của FollowDAO
import org.example.model.User; // Đại diện cho thông tin của một người dùng (như tên, ID)
import java.io.IOException; // Xử lý lỗi khi gửi hoặc nhận dữ liệu
import java.util.List; // Dùng để lưu danh sách người dùng

// Gắn địa chỉ "/follows/*" cho trang web này, xử lý các yêu cầu như "/follows/following" hoặc "/follows/followers"
@WebServlet("/follows/*")
public class FollowListServlet extends HttpServlet {
    // Tác dụng: Tạo sẵn một công cụ (FollowDAO) để lấy danh sách người theo dõi hoặc đang theo dõi từ cơ sở dữ liệu
    // Giúp trang web dễ dàng lấy thông tin cần thiết khi người dùng yêu cầu
    private FollowDAO followDAO = new FollowDAOImpl();

    // Tác dụng: Xử lý khi người dùng truy cập trang (như gõ URL hoặc nhấp liên kết)
    // Lấy danh sách người theo dõi hoặc đang theo dõi, rồi hiển thị trên trang follows.jsp
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Tác dụng: Kiểm tra xem người dùng đã đăng nhập chưa bằng cách lấy thông tin phiên truy cập
        // Nếu chưa đăng nhập, không tạo phiên mới, chỉ lấy phiên hiện có (nếu có)
        HttpSession session = request.getSession(false);
        // Tác dụng: Lấy thông tin người dùng từ phiên truy cập để biết ai đang xem danh sách
        // Nếu không có phiên, trả về null (nghĩa là chưa đăng nhập)
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        // Tác dụng: Nếu người dùng chưa đăng nhập, báo lỗi "Không có quyền" (401) và dừng lại
        // Đảm bảo chỉ người đã đăng nhập mới xem được danh sách
        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED); // 401: Không được phép
            return;
        }

        // Tác dụng: Lấy phần sau của URL (ví dụ: "/following" từ "/follows/following")
        // Dùng để biết người dùng muốn xem danh sách "người đang theo dõi" hay "người theo dõi mình"
        String pathInfo = request.getPathInfo();
        // Tác dụng: Nếu URL không rõ ràng (như "/follows" hoặc "/follows/"), chuyển người dùng đến "/follows/following"
        // Đảm bảo luôn có nội dung mặc định để hiển thị thay vì để trống
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/follows/following");
            return;
        }

        // Tác dụng: Nếu URL là "/follows/following", lấy danh sách người mà người dùng đang theo dõi
        // Chuẩn bị dữ liệu để hiển thị trên trang web
        if ("/following".equals(pathInfo)) {
            List<User> following = followDAO.findFollowing(currentUser); // Lấy danh sách từ cơ sở dữ liệu
            request.setAttribute("following", following); // Gửi danh sách cho trang JSP để hiển thị
            request.setAttribute("listType", "following"); // Báo cho JSP biết đây là danh sách "đang theo dõi"
        }
        // Tác dụng: Nếu URL là "/follows/followers", lấy danh sách người theo dõi người dùng
        // Chuẩn bị dữ liệu để hiển thị trên trang web
        else if ("/followers".equals(pathInfo)) {
            List<User> followers = followDAO.findFollowers(currentUser); // Lấy danh sách từ cơ sở dữ liệu
            request.setAttribute("followers", followers); // Gửi danh sách cho trang JSP để hiển thị
            request.setAttribute("listType", "followers"); // Báo cho JSP biết đây là danh sách "người theo dõi"
        }

        // Tác dụng: Chuyển toàn bộ thông tin đã chuẩn bị sang trang follows.jsp
        // Trang JSP sẽ dùng thông tin này để vẽ giao diện cho người dùng xem
        request.getRequestDispatcher("/follows.jsp").forward(request, response);
    }
}