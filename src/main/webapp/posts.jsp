<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Quản lý bài viết</title>
    <style>
        .post-form {
            margin-bottom: 20px;
            padding: 20px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .post-list {
            display: grid;
            gap: 20px;
        }
        .post-item {
            padding: 15px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .post-actions {
            margin-top: 10px;
            display: flex;
            gap: 10px;
        }
        .btn {
            padding: 5px 10px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .btn-edit {
            background-color: #4CAF50;
            color: white;
        }
        .btn-delete {
            background-color: #f44336;
            color: white;
        }
    </style>
</head>
<body>
<h1>Quản lý bài viết</h1>
<!-- Tác dụng: Hiển thị chữ lớn "Quản lý bài viết" trên trang -->
<!-- Đây là tiêu đề chính để bạn biết trang này dùng để làm gì -->

<div class="post-form">
    <h2>Đăng bài mới</h2>
    <!-- Tác dụng: Hiển thị chữ "Đăng bài mới" trong phần form -->
    <!-- Cho bạn biết đây là nơi để tạo bài viết mới -->

    <form action="${pageContext.request.contextPath}/post" method="post">
        <!-- Tác dụng: Tạo biểu mẫu để bạn nhập thông tin bài viết mới -->
        <!-- "action='${pageContext.request.contextPath}/post'": Dữ liệu sẽ được gửi đến "/post" trên máy chủ -->
        <!-- "method='post'": Dữ liệu (tiêu đề, nội dung) được gửi kín đáo qua POST, không hiện trên URL -->

        <div>
            <label for="title">Tiêu đề:</label>
            <!-- Tác dụng: Hiển thị chữ "Tiêu đề:" để bạn biết ô này nhập gì -->
            <input type="text" id="title" name="title" required>
            <!-- Tác dụng: Tạo ô để bạn gõ tiêu đề bài viết, ví dụ: "Xin chào mọi người" -->
            <!-- "name='title'": Đặt tên "title" để máy chủ nhận biết đây là tiêu đề -->
            <!-- "required": Bắt buộc phải nhập, không để trống -->
        </div>
        <div>
            <label for="body">Nội dung:</label>
            <!-- Tác dụng: Hiển thị chữ "Nội dung:" để bạn biết ô này nhập gì -->
            <textarea id="body" name="body" required></textarea>
            <!-- Tác dụng: Tạo ô lớn để bạn gõ nội dung bài viết, ví dụ: "Hôm nay là ngày đẹp trời" -->
            <!-- "name='body'": Đặt tên "body" để máy chủ nhận biết đây là nội dung -->
            <!-- "required": Bắt buộc phải nhập -->
        </div>
        <button type="submit" class="btn btn-submit">Đăng bài</button>
        <!-- Tác dụng: Tạo nút "Đăng bài" để gửi dữ liệu bạn vừa nhập -->
        <!-- Khi nhấp, tiêu đề và nội dung được gửi đến "/post" để lưu -->
    </form>
</div>

<div class="post-list">
    <c:forEach items="${posts}" var="post">
        <!-- Tác dụng: Lặp qua danh sách bài viết từ máy chủ để hiển thị từng bài -->
        <!-- Mỗi bài được gán vào biến "post" để dùng trong vòng lặp -->

        <div class="post-item">
            <h3>${post.title}</h3>
            <!-- Tác dụng: Hiển thị tiêu đề của bài viết, ví dụ: "Xin chào mọi người" -->
            <!-- Lấy từ dữ liệu máy chủ để bạn thấy nội dung chính của bài -->

            <p>${post.body}</p>
            <!-- Tác dụng: Hiển thị nội dung đầy đủ của bài viết -->
            <!-- Lấy từ dữ liệu máy chủ để bạn đọc toàn bộ bài -->

            <small>Đăng bởi: ${post.user.username} - ${post.createdAt}</small>
            <!-- Tác dụng: Hiển thị thông tin nhỏ về tác giả và thời gian đăng -->
            <!-- Ví dụ: "Đăng bởi: Nam - 2025-04-02T10:00" để bạn biết ai đăng và khi nào -->

            <c:if test="${sessionScope.role == 'ADMIN' || sessionScope.user.id == post.user.id}">
                <!-- Tác dụng: Kiểm tra xem bạn có quyền sửa/xóa bài viết không -->
                <!-- Chỉ hiện nút "Sửa" và "Xóa" nếu bạn là ADMIN hoặc là người đăng bài -->

                <div class="post-actions">
                    <button onclick="editPost(`${post.id}`)" class="btn btn-edit">Sửa</button>
                    <!-- Tác dụng: Tạo nút "Sửa" để bạn chỉnh sửa bài viết -->
                    <!-- Khi nhấp, gọi hàm JavaScript editPost() với ID bài viết -->

                    <button onclick="deletePost(`${post.id}`)" class="btn btn-delete">Xóa</button>
                    <!-- Tác dụng: Tạo nút "Xóa" để bạn xóa bài viết -->
                    <!-- Khi nhấp, gọi hàm JavaScript deletePost() với ID bài viết -->
                </div>
            </c:if>
        </div>
    </c:forEach>
</div>

<script>
    function deletePost(postId) {
        if (confirm('Bạn có chắc muốn xóa bài viết này?')) {
            fetch('${pageContext.request.contextPath}/post/' + postId, {
                method: 'DELETE'
            }).then(response => {
                if (response.ok) {
                    location.reload();
                }
            });
        }
    }
    <!-- Tác dụng: Hàm này hỏi bạn có chắc muốn xóa bài viết không -->
    <!-- Nếu đồng ý, gửi yêu cầu DELETE đến "/post/[ID]" để xóa bài -->
    <!-- Khi xóa xong, trang tải lại để cập nhật danh sách -->

    function editPost(postId) {
        const newTitle = prompt('Nhập tiêu đề mới:');
        const newBody = prompt('Nhập nội dung mới:');
        <!-- Tác dụng: Hỏi bạn tiêu đề và nội dung mới qua hai hộp thoại nhỏ -->
        <!-- Ví dụ: Bạn nhập "Tiêu đề mới" và "Nội dung mới" -->

        if (newTitle && newBody) {
            const formData = new FormData();
            formData.append('title', newTitle);
            formData.append('body', newBody);
            <!-- Tác dụng: Tạo một gói dữ liệu chứa tiêu đề và nội dung mới -->
            <!-- Gói này giống như một phong bì để gửi thông tin đi -->

            fetch('${pageContext.request.contextPath}/post/' + postId, {
                method: 'PUT',
                body: formData
            }).then(response => {
                if (response.ok) {
                    location.reload();
                }
            });
            <!-- Tác dụng: Gửi gói dữ liệu đến "/post/[ID]" bằng cách PUT để cập nhật bài viết -->
            <!-- Khi xong, trang tải lại để hiển thị bài đã sửa -->
        }
    }
</script>
</body>
</html>