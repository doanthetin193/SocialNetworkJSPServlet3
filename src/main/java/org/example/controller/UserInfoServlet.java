package org.example.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import org.example.DAO.ProvinceDAO;
import org.example.DAO.ProvinceDAOImpl;
import org.example.DAO.UserDAO;
import org.example.DAO.UserDAOImpl;
import org.example.model.CustomUser;
import org.example.model.Province;
import org.example.model.User;
import org.example.util.Constants;
import org.example.util.Validator;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@MultipartConfig
@WebServlet("/user-info")
public class UserInfoServlet extends HttpServlet {
    
    private ProvinceDAO provinceDAO = new ProvinceDAOImpl();
    private UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        List<Province> provinces = provinceDAO.getAll();

        req.setAttribute("customUser", (CustomUser) currentUser);
        req.setAttribute("provinces", provinces);

        req.getRequestDispatcher("/editprofile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();

        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String birthDateStr = req.getParameter("birthDate");
        String provinceId = req.getParameter("provinceId");
        Part avatarPart = req.getPart("avatar");

        boolean isEmailValid = true;
        List<String> emailStateMessages = new ArrayList<>();

        if (!Validator.matchesFormat(email, Constants.EMAIL_PATTERN)) {
            emailStateMessages.add("Email không đúng định dạng.");
            isEmailValid = false;
        }

        if (userDAO.isEmailExists(email)) {
            emailStateMessages.add("Email đã tồn tại.");
            isEmailValid = false;
        }

        session.setAttribute("isEmailValid", isEmailValid);
        if (!isEmailValid) {
            session.setAttribute("emailStateMessages", emailStateMessages);
        }

        boolean isBirthDateValid = true;
        String birthDateStateMessage = "";

        try {
            LocalDate birthDate = LocalDate.parse(birthDateStr);
            if (Period.between(birthDate, LocalDate.now()).getYears() <= 15) {
                birthDateStateMessage = "Tuổi phải trên 15.";
                isBirthDateValid = false;
            }
        } catch (Exception e) {
            birthDateStateMessage = "Ngày sinh không hợp lệ.";
            isBirthDateValid = false;
        }

        session.setAttribute("isBirthDateValid", isBirthDateValid);
        if (!isBirthDateValid) {
            session.setAttribute("birthDateStateMessage", birthDateStateMessage);
        }

        boolean isAvatarFileValid = true;
        List<String> avatarFileStateMessages = new ArrayList<>();

        if (avatarPart != null && avatarPart.getSize() > 0) {
            if (avatarPart.getSize() > 200 * 1024) {
                avatarFileStateMessages.add("Kích thước ảnh không được vượt quá 200KB.");
                isAvatarFileValid = false;
            }

            String contentType = avatarPart.getContentType();
            if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
                avatarFileStateMessages.add("Chỉ chấp nhận ảnh định dạng JPG hoặc PNG.");
                isAvatarFileValid = false;
            }
        }

        session.setAttribute("isAvatarFileValid", isAvatarFileValid);
        if (!isAvatarFileValid) {
            session.setAttribute("avatarFileStateMessage", avatarFileStateMessages);
        }

        CustomUser updatedUser = new CustomUser();

        if (isEmailValid && isBirthDateValid && isAvatarFileValid) {
            try {
                String fileName = Paths.get(avatarPart.getSubmittedFileName()).getFileName().toString();
                String uploadPath = getServletContext().getRealPath("/img");

                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdirs();

                File file = new File(uploadPath + File.separator + fileName);
                avatarPart.write(file.getAbsolutePath());

                User user = (User) session.getAttribute("user");

                updatedUser.setId(user.getId());
                updatedUser.setUsername(username);
                updatedUser.setEmail(email);
                updatedUser.setBirthDate(LocalDate.parse(birthDateStr));
                updatedUser.setProvinceId(Integer.parseInt(provinceId));
                updatedUser.setAvatarFileName(fileName);

                userDAO.update(updatedUser);

                session.setAttribute("currentPageState", updatedUser);
                resp.sendRedirect(req.getContextPath() + "/home.jsp");
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Forward về editprofile.jsp nếu có lỗi và gán lại tất cả attributes từ session vào request
        req.setAttribute("currentPageState", new CustomUser(username, email, birthDateStr, Integer.parseInt(provinceId)));
        req.setAttribute("isEmailValid", session.getAttribute("isEmailValid"));
        req.setAttribute("emailStateMessages", session.getAttribute("emailStateMessages"));
        req.setAttribute("isBirthDateValid", session.getAttribute("isBirthDateValid"));
        req.setAttribute("birthDateStateMessage", session.getAttribute("birthDateStateMessage"));
        req.setAttribute("isAvatarFileValid", session.getAttribute("isAvatarFileValid"));
        req.setAttribute("avatarFileStateMessage", session.getAttribute("avatarFileStateMessage"));
        req.setAttribute("provinces", provinceDAO.getAll());

        req.getRequestDispatcher("/editprofile.jsp").forward(req, resp);
    }
}
