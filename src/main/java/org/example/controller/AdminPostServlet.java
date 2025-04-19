package org.example.controller;

// Nhập các công cụ cần thiết để xử lý trang web và dữ liệu
import jakarta.servlet.ServletException; // Dùng để xử lý lỗi khi trang web gặp vấn đề
import jakarta.servlet.annotation.WebServlet; // Dùng để gắn địa chỉ URL cho trang web
import jakarta.servlet.http.HttpServlet; // Công cụ chính để xử lý các yêu cầu từ người dùng
import jakarta.servlet.http.HttpServletRequest; // Lấy thông tin mà người dùng gửi đến (như URL, dữ liệu form)
import jakarta.servlet.http.HttpServletResponse; // Gửi thông tin trả lại cho người dùng (như trang web hoặc lỗi)
import org.example.DAO.PostsDAO; // Công cụ để lấy, sửa, xóa bài viết từ cơ sở dữ liệu
import org.example.DAO.PostsDAOImpl; // Phần thực hiện cụ thể của PostsDAO
import org.example.model.Posts; // Đại diện cho thông tin của một bài viết (tiêu đề, nội dung, v.v.)
import java.io.IOException; // Xử lý lỗi khi gửi hoặc nhận dữ liệu
import java.time.LocalDateTime; // Dùng để ghi lại thời gian cập nhật bài viết
import java.util.List; // Dùng để lưu danh sách bài viết

// Gắn địa chỉ "/admin/posts/*" cho trang web này, xử lý các yêu cầu như "/admin/posts" hoặc "/admin/posts/edit/1"
@WebServlet("/admin/posts/*")
public class AdminPostServlet extends HttpServlet {
    // Tác dụng: Tạo sẵn một công cụ (PostsDAO) để làm việc với bài viết trong cơ sở dữ liệu
    // Giúp trang web dễ dàng lấy, sửa, hoặc xóa bài viết khi cần
    private PostsDAO postsDAO = new PostsDAOImpl();

    // Tác dụng: Xử lý khi người dùng truy cập trang (như gõ URL hoặc nhấp liên kết)
    // Quyết định hiển thị danh sách bài viết hoặc form sửa bài viết dựa trên URL
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Tác dụng: Lấy phần sau của URL (ví dụ: "/edit/1" từ "/admin/posts/edit/1")
        // Dùng để biết người dùng muốn xem danh sách hay sửa bài viết
        String pathInfo = request.getPathInfo();
        // Tác dụng: Nếu URL là "/admin/posts" hoặc "/admin/posts/", hiển thị danh sách bài viết
        if (pathInfo == null || pathInfo.equals("/")) {
            listPosts(request, response); // Gọi hàm hiển thị danh sách
        }
        // Tác dụng: Nếu URL bắt đầu bằng "/edit/", hiển thị form sửa bài viết
        else if (pathInfo.startsWith("/edit/")) {
            showEditForm(request, response); // Gọi hàm hiển thị form sửa
        }
    }

    // Tác dụng: Lấy tất cả bài viết từ cơ sở dữ liệu và hiển thị trên trang danh sách
    // Chuẩn bị dữ liệu để người dùng xem toàn bộ bài viết trong khu vực admin
    private void listPosts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Posts> posts = postsDAO.findAll(); // Lấy toàn bộ bài viết từ cơ sở dữ liệu
        request.setAttribute("posts", posts); // Gửi danh sách bài viết cho trang JSP
        // Tác dụng: Chuyển sang trang posts.jsp để hiển thị danh sách bài viết
        request.getRequestDispatcher("/admin/posts.jsp").forward(request, response);
    }

    // Tác dụng: Lấy thông tin một bài viết cụ thể và hiển thị form để sửa nó
    // Chuẩn bị dữ liệu để người dùng chỉnh sửa bài viết trên giao diện
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo(); // Lấy phần sau của URL (ví dụ: "/edit/1")
        // Tác dụng: Tách ID bài viết từ URL (ví dụ: lấy "1" từ "/edit/1")
        Long postId = Long.parseLong(pathInfo.split("/")[2]);
        Posts post = postsDAO.findById(postId); // Lấy thông tin bài viết từ cơ sở dữ liệu dựa trên ID
        // Tác dụng: Nếu không tìm thấy bài viết, báo lỗi "Không tìm thấy" (404) và dừng lại
        if (post == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        request.setAttribute("post", post); // Gửi thông tin bài viết cho trang JSP
        // Tác dụng: Chuyển sang trang edit-post.jsp để hiển thị form sửa bài viết
        request.getRequestDispatcher("/admin/edit-post.jsp").forward(request, response);
    }

    // Tác dụng: Xử lý khi người dùng gửi dữ liệu (như nhấn nút "Lưu" trên form)
    // Quyết định cập nhật bài viết dựa trên dữ liệu gửi lên
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo(); // Lấy phần sau của URL
        // Tác dụng: Nếu URL là "/admin/posts/edit", cập nhật bài viết dựa trên dữ liệu form
        if (pathInfo != null && pathInfo.equals("/edit")) {
            updatePost(request, response); // Gọi hàm cập nhật bài viết
        }
    }

    // Tác dụng: Cập nhật thông tin bài viết dựa trên dữ liệu từ form
    // Lưu thay đổi vào cơ sở dữ liệu và đưa người dùng về danh sách bài viết
    private void updatePost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Tác dụng: Lấy thông tin từ form (ID, tiêu đề, nội dung, trạng thái)
        Long postId = Long.parseLong(request.getParameter("postId"));
        String title = request.getParameter("title");
        String body = request.getParameter("body");
        String status = request.getParameter("status");

        Posts post = postsDAO.findById(postId); // Lấy bài viết từ cơ sở dữ liệu dựa trên ID
        // Tác dụng: Nếu không tìm thấy bài viết, báo lỗi "Không tìm thấy" (404) và dừng lại
        if (post == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Tác dụng: Cập nhật thông tin bài viết với dữ liệu mới từ form
        post.setTitle(title); // Đặt lại tiêu đề
        post.setBody(body); // Đặt lại nội dung
        post.setStatus(status); // Đặt lại trạng thái
        post.setUpdatedAt(LocalDateTime.now()); // Ghi lại thời gian cập nhật là hiện tại

        postsDAO.update(post); // Lưu thay đổi vào cơ sở dữ liệu
        // Tác dụng: Chuyển người dùng về trang danh sách bài viết sau khi cập nhật xong
        response.sendRedirect(request.getContextPath() + "/admin/posts");
    }

    // Tác dụng: Xử lý khi người dùng yêu cầu xóa bài viết
    // Xóa bài viết khỏi cơ sở dữ liệu và báo thành công
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Tác dụng: Lấy ID bài viết từ yêu cầu để biết bài nào cần xóa
        Long postId = Long.parseLong(request.getParameter("postId"));
        Posts post = postsDAO.findById(postId); // Lấy bài viết từ cơ sở dữ liệu
        // Tác dụng: Nếu không tìm thấy bài viết, báo lỗi "Không tìm thấy" (404) và dừng lại
        if (post == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        postsDAO.delete(postId); // Xóa bài viết khỏi cơ sở dữ liệu
        // Tác dụng: Báo cho người dùng rằng xóa thành công bằng mã 200 (OK)
        response.setStatus(HttpServletResponse.SC_OK);
    }
}