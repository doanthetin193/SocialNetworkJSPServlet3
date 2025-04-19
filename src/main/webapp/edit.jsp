<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!-- Tác dụng: Thêm công cụ JSTL để dễ dàng xử lý điều kiện và vòng lặp -->
<!-- Cho phép viết code đơn giản hơn khi làm việc với dữ liệu từ server -->
<!DOCTYPE html>
<html>
<head>
    <title>Sửa bài viết</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
        }
        .form-group {
            margin-bottom: 15px;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
        }
        .form-group input[type="text"],
        .form-group textarea {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .form-group textarea {
            height: 200px;
            resize: vertical;
        }
        .btn {
            background-color: #4CAF50;
            color: white;
            padding: 10px 15px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .btn:hover {
            background-color: #45a049;
        }
        .error {
            color: red;
            margin-bottom: 15px;
        }
    </style>
</head>
<body>
<h2>Sửa bài viết</h2>
<!-- Tác dụng: Hiển thị tiêu đề lớn "Sửa bài viết" trên trang -->
<!-- Giúp người dùng biết ngay mục đích của trang này -->

<c:if test="${not empty error}">
    <div class="error">${error}</div>
</c:if>
<!-- Tác dụng: Kiểm tra nếu có thông báo lỗi từ server thì hiển thị nó -->
<!-- Thông báo sẽ hiện màu đỏ để người dùng biết có vấn đề khi sửa bài viết -->

<form action="${pageContext.request.contextPath}/post/edit" method="post">
    <!-- Tác dụng: Tạo một form để người dùng nhập dữ liệu và gửi về server -->
    <!-- Gửi dữ liệu đến "/post/edit" bằng phương thức POST khi nhấn nút "Lưu thay đổi" -->

    <input type="hidden" name="postId" value="${post.id}">
    <!-- Tác dụng: Gửi ID của bài viết cần sửa về server mà không hiển thị trên giao diện -->
    <!-- Giúp server biết bài viết nào đang được chỉnh sửa -->

    <div class="form-group">
        <label for="title">Tiêu đề:</label>
        <!-- Tác dụng: Hiển thị chữ "Tiêu đề:" để người dùng biết ô này nhập gì -->
        <input type="text" id="title" name="title" value="${post.title}" required>
        <!-- Tác dụng: Tạo ô nhập liệu để sửa tiêu đề bài viết -->
        <!-- Hiển thị tiêu đề hiện tại từ server và yêu cầu phải nhập (required) -->
    </div>

    <div class="form-group">
        <label for="body">Nội dung:</label>
        <!-- Tác dụng: Hiển thị chữ "Nội dung:" để người dùng biết ô này nhập gì -->
        <textarea id="body" name="body" required>${post.body}</textarea>
        <!-- Tác dụng: Tạo ô nhập liệu lớn để sửa nội dung bài viết -->
        <!-- Hiển thị nội dung hiện tại từ server và yêu cầu phải nhập -->
    </div>

    <button type="submit" class="btn">Lưu thay đổi</button>
    <!-- Tác dụng: Tạo nút "Lưu thay đổi" để gửi dữ liệu form về server -->
    <!-- Khi nhấn, dữ liệu sẽ được gửi đến "/post/edit" để cập nhật bài viết -->

    <a href="${pageContext.request.contextPath}/" class="btn" style="background-color: #666;">Hủy</a>
    <!-- Tác dụng: Tạo nút "Hủy" để quay về trang chính mà không lưu thay đổi -->
    <!-- Đây là một liên kết chứ không gửi dữ liệu form -->
</form>
</body>
</html>