package org.example.controller;

// Nhập các công cụ cần thiết để xử lý trang web và dữ liệu
import jakarta.servlet.ServletException; // Dùng để xử lý lỗi khi trang web gặp vấn đề
import jakarta.servlet.annotation.WebServlet; // Dùng để gắn địa chỉ URL cho trang web
import jakarta.servlet.http.HttpServlet; // Công cụ chính để xử lý các yêu cầu từ người dùng
import jakarta.servlet.http.HttpServletRequest; // Lấy thông tin mà người dùng gửi đến (như URL, dữ liệu form)
import jakarta.servlet.http.HttpServletResponse; // Gửi thông tin trả lại cho người dùng (như trang web hoặc lỗi)
import jakarta.servlet.http.HttpSession; // Giữ thông tin người dùng trong suốt phiên truy cập
import org.example.DAO.PostsDAO; // Công cụ để làm việc với bài viết trong cơ sở dữ liệu
import org.example.DAO.PostsDAOImpl; // Phần thực hiện cụ thể của PostsDAO
import org.example.model.Posts; // Đại diện cho thông tin của một bài viết (tiêu đề, nội dung, v.v.)
import org.example.model.User; // Đại diện cho thông tin của một người dùng (ID, tên, v.v.)
import java.io.IOException; // Xử lý lỗi khi gửi hoặc nhận dữ liệu
import java.time.LocalDateTime; // Dùng để ghi lại thời gian tạo hoặc cập nhật bài viết

// Gắn địa chỉ "/post/*" cho trang web này, xử lý các yêu cầu như "/post/edit" hoặc "/post/1"
@WebServlet("/post/*")
public class PostServlet extends HttpServlet {
    // Tác dụng: Tạo sẵn một công cụ để làm việc với bài viết trong cơ sở dữ liệu
    // Giúp trang web dễ dàng lấy, thêm, sửa, hoặc xóa bài viết
    private PostsDAO postsDAO = new PostsDAOImpl();

    // Tác dụng: Kiểm tra xem người dùng có quyền sửa hoặc xóa bài viết không
    // Chỉ cho phép nếu người dùng là tác giả bài viết hoặc là admin
    private boolean isAuthorizedToModifyPost(HttpServletRequest request, Posts post) {
        HttpSession session = request.getSession(false); // Lấy phiên truy cập hiện tại, không tạo mới
        if (session == null) // Nếu không có phiên, trả về false (không có quyền)
            return false;

        User currentUser = (User) session.getAttribute("user"); // Lấy thông tin người dùng hiện tại
        String userRole = (String) session.getAttribute("role"); // Lấy vai trò của người dùng (như "ADMIN")

        // Tác dụng: Kiểm tra quyền: người dùng phải đăng nhập và là tác giả hoặc admin
        return currentUser != null &&
                (currentUser.getId().equals(post.getUser().getId()) || "ADMIN".equals(userRole));
    }

    // Tác dụng: Xử lý khi người dùng truy cập trang (như gõ URL)
    // Hiển thị form sửa bài viết nếu URL là "/post/edit"
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo(); // Lấy phần sau của URL (ví dụ: "/edit")
        if ("/edit".equals(pathInfo)) { // Nếu URL là "/post/edit"
            Long postId = Long.parseLong(request.getParameter("postId")); // Lấy ID bài viết từ tham số
            Posts post = postsDAO.findById(postId); // Lấy bài viết từ cơ sở dữ liệu
            if (post == null) { // Nếu không tìm thấy bài viết, báo lỗi "Không tìm thấy" (404)
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            request.setAttribute("post", post); // Gửi thông tin bài viết cho trang JSP
            // Tác dụng: Chuyển sang trang edit.jsp để hiển thị form sửa bài viết
            request.getRequestDispatcher("/edit.jsp").forward(request, response);
        }
    }

    // Tác dụng: Xử lý khi người dùng gửi dữ liệu (như nhấn "Lưu" trên form)
    // Tạo bài viết mới hoặc sửa bài viết tùy theo URL
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo(); // Lấy phần sau của URL
        if (pathInfo == null || pathInfo.equals("/")) { // Nếu URL là "/post" hoặc "/post/"
            createPost(request, response); // Gọi hàm tạo bài viết mới
        } else if ("/edit".equals(pathInfo)) { // Nếu URL là "/post/edit"
            editPost(request, response); // Gọi hàm sửa bài viết
        }
    }

    // Tác dụng: Tạo một bài viết mới dựa trên dữ liệu từ form
    // Lưu bài viết vào cơ sở dữ liệu và đưa người dùng về trang chính
    private void createPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false); // Lấy phiên truy cập hiện tại
        if (session == null) { // Nếu không có phiên, báo lỗi "Chưa đăng nhập" (401)
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        User currentUser = (User) session.getAttribute("user"); // Lấy thông tin người dùng hiện tại
        String title = request.getParameter("title"); // Lấy tiêu đề từ form
        String body = request.getParameter("body"); // Lấy nội dung từ form

        try {
            Posts newPost = new Posts(); // Tạo một bài viết mới
            newPost.setTitle(title); // Đặt tiêu đề cho bài viết
            newPost.setBody(body); // Đặt nội dung cho bài viết
            newPost.setUser(currentUser); // Gắn người dùng hiện tại làm tác giả
            newPost.setStatus("ACTIVE"); // Đặt trạng thái là "đã hoạt động"
            newPost.setCreatedAt(LocalDateTime.now()); // Ghi thời gian tạo là hiện tại
            newPost.setUpdatedAt(LocalDateTime.now()); // Ghi thời gian cập nhật là hiện tại

            postsDAO.save(newPost); // Lưu bài viết vào cơ sở dữ liệu
            // Tác dụng: Chuyển người dùng về trang chính sau khi tạo xong
            response.sendRedirect(request.getContextPath() + "/");
        } catch (Exception e) { // Nếu có lỗi, báo lỗi chi tiết
            throw new ServletException("Error creating post", e);
        }
    }

    // Tác dụng: Sửa một bài viết dựa trên dữ liệu từ form
    // Cập nhật bài viết trong cơ sở dữ liệu và đưa người dùng về trang chính
    private void editPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false); // Lấy session truy cập hiện tại
        if (session == null) { // Nếu không có session, báo lỗi "Chưa đăng nhập" (401)
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Tác dụng: Lấy thông tin từ form để cập nhật bài viết
        Long postId = Long.parseLong(request.getParameter("postId")); // Lấy ID bài viết
        String title = request.getParameter("title"); // Lấy tiêu đề mới
        String body = request.getParameter("body"); // Lấy nội dung mới

        Posts existingPost = postsDAO.findById(postId); // Lấy bài viết hiện tại từ cơ sở dữ liệu
        if (existingPost == null) { // Nếu không tìm thấy, báo lỗi "Không tìm thấy" (404)
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy bài viết");
            return;
        }

        // Tác dụng: Kiểm tra xem người dùng có quyền sửa bài viết này không
        if (!isAuthorizedToModifyPost(request, existingPost)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền sửa bài viết này");
            return;
        }

        try {
            // Tác dụng: Cập nhật bài viết với thông tin mới
            existingPost.setTitle(title); // Đặt tiêu đề mới
            existingPost.setBody(body); // Đặt nội dung mới
            existingPost.setUpdatedAt(LocalDateTime.now()); // Ghi thời gian cập nhật là hiện tại

            postsDAO.update(existingPost); // Lưu thay đổi vào cơ sở dữ liệu
            // Tác dụng: Chuyển người dùng về trang chính sau khi sửa xong
            response.sendRedirect(request.getContextPath() + "/");
        } catch (Exception e) { // Nếu có lỗi, hiển thị lại form với thông báo lỗi
            request.setAttribute("error", "Có lỗi xảy ra khi cập nhật bài viết: " + e.getMessage());
            request.setAttribute("post", existingPost);
            request.getRequestDispatcher("/edit.jsp").forward(request, response);
        }
    }

    // Tác dụng: Xử lý khi người dùng yêu cầu xóa bài viết
    // Xóa bài viết dựa trên ID từ URL
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo(); // Lấy phần sau của URL
        if (pathInfo == null || pathInfo.equals("/")) { // Nếu URL không đúng, báo lỗi
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            Long postId = Long.parseLong(pathInfo.substring(1)); // Lấy ID bài viết từ URL (ví dụ: "/1" -> 1)
            deletePost(postId, request, response); // Gọi hàm xóa bài viết
        } catch (NumberFormatException e) { // Nếu ID không phải số, báo lỗi
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    // Tác dụng: Xóa bài viết khỏi cơ sở dữ liệu nếu người dùng có quyền
    // Báo thành công sau khi xóa
    private void deletePost(Long postId, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Posts post = postsDAO.findById(postId); // Lấy bài viết từ cơ sở dữ liệu
        if (post == null) { // Nếu không tìm thấy, báo lỗi "Không tìm thấy" (404)
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Tác dụng: Kiểm tra xem người dùng có quyền xóa bài viết này không
        if (!isAuthorizedToModifyPost(request, post)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        postsDAO.delete(postId); // Xóa bài viết khỏi cơ sở dữ liệu
        // Tác dụng: Báo cho người dùng rằng xóa thành công bằng mã 200 (OK)
        response.setStatus(HttpServletResponse.SC_OK);
    }
}