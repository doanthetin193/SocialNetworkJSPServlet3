<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>

    <title>Đăng nhập</title>

    <style>

        body {
            font-family: Arial, sans-serif;
            max-width: 500px;
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
        .form-group input {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .error {
            color: red;
            margin-bottom: 15px;
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
        .link {
            margin-top: 15px;
            text-align: center;
        }
    </style>
</head>
<body>


<h2>Đăng nhập</h2>
<c:if test="${not empty error}">
    <div class="error">${error}</div>
</c:if>
<!-- Tác dụng: Kiểm tra xem máy chủ có gửi thông báo lỗi không -->
<!-- Nếu có lỗi (ví dụ: "Sai mật khẩu"), nó sẽ hiện lên bằng chữ đỏ để bạn biết -->

<form action="login" method="post">
    <!-- Tác dụng: Tạo một biểu mẫu để bạn nhập thông tin đăng nhập (tên và mật khẩu) -->
    <!-- "action='login'": Khi bạn nhấn nút "Đăng nhập", dữ liệu sẽ được gửi đến địa chỉ "/login" trên máy chủ -->
    <!-- "method='post'": Cách gửi dữ liệu là POST, nghĩa là thông tin (tên, mật khẩu) được gửi kín đáo, không hiện trên URL -->
    <!-- Ví dụ: Nếu dùng GET thay POST, tên và mật khẩu sẽ lộ trên URL như "login?username=abc&password=123", nhưng POST thì không -->

    <div class="form-group">
        <label for="username">Tên đăng nhập:</label>
        <input type="text" id="username" name="username" required>
        <!-- Tác dụng: Tạo ô để bạn gõ tên đăng nhập, ví dụ: "Nam123" -->
        <!-- "name='username'": Đặt tên cho dữ liệu này là "username" để máy chủ nhận biết -->
        <!-- "required": Bắt buộc phải nhập, nếu để trống thì form không gửi được -->
    </div>

    <div class="form-group">
        <label for="password">Mật khẩu:</label>

        <input type="password" id="password" name="password" required>
        <!-- Tác dụng: Tạo ô để bạn gõ mật khẩu, ví dụ: "abc123" -->
        <!-- "type='password'": Làm cho chữ bạn gõ bị ẩn thành dấu chấm (•••••) để bảo mật -->
        <!-- "name='password'": Đặt tên cho dữ liệu này là "password" để máy chủ nhận biết -->
        <!-- "required": Bắt buộc phải nhập, không để trống -->
    </div>

    <button type="submit" class="btn">Đăng nhập</button>
    <!-- Tác dụng: Tạo nút "Đăng nhập" để gửi dữ liệu bạn vừa nhập -->
    <!-- Khi nhấp, tên đăng nhập và mật khẩu được gửi đến "/login" để máy chủ kiểm tra -->
</form>

<div class="link">
    <p>Chưa có tài khoản? <a href="register">Đăng ký ngay</a></p>
    <!-- Tác dụng: Hiển thị dòng chữ "Chưa có tài khoản?" và nút liên kết "Đăng ký ngay" -->
    <!-- Khi nhấp "Đăng ký ngay", bạn sẽ được chuyển đến trang đăng ký ("/register") -->
</div>
</body>
</html>