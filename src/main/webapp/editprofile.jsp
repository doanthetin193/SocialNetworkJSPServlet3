<%@page import="org.example.model.CustomUser"%>
<%@page import="org.example.model.Province"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Edit Profile</title>
</head>
<body>

	<%
		List<Province> provinces = (List<Province>) request.getAttribute("provinces");
		CustomUser customUser = (CustomUser) request.getAttribute("customUser");
		CustomUser currentPageState = (CustomUser) request.getAttribute("currentPageState");
		
		if (currentPageState == null) {
			currentPageState = customUser;
		}
		
		Boolean isEmailValid = (Boolean) request.getAttribute("isEmailValid");
	    if (isEmailValid == null) {
	        isEmailValid = true;
	    }
		List<String> emailStateMessages = (List<String>) request.getAttribute("emailStateMessages");
		
		Boolean isBirthDateValid = (Boolean) request.getAttribute("isBirthDateValid");
		if (isBirthDateValid == null) {
			isBirthDateValid = true;
		}
		String birthDateStateMessage = (String) request.getAttribute("birthDateStateMessage");
		
		Boolean isAvatarFilevalid = (Boolean) request.getAttribute("isAvatarFileValid");
		if (isAvatarFilevalid == null) {
			isAvatarFilevalid = true;
		}
		List<String> avatarFileStateMessage = (List<String>) request.getAttribute("avatarFileStateMessage");
	%>

	<div>
      <form method="post" action="${pageContext.request.contextPath}/user-info" enctype="multipart/form-data">
        <div>
      	<label>Username:</label>
      	<span><%= currentPageState.getUsername() %></span>
      </div>
      <div>
        <label for="email">Email:</label>
      	<input id="email" name="email" value="<%= currentPageState.getEmail() %>" required />
      	
      	<div style="color: red;">
      		<%
      			if (!isEmailValid) {
      				for (String message : emailStateMessages) {
    					%>
    						<span><%= message %></span>
    					<%
      				}
      			}
      		%>
      	</div>
      </div>
      <div>
      	<label for="birthdate">BirthDate:</label>
      	<input id="birthdate" type="date" name="birthDate" value="<%= currentPageState.getBirthDate() %>" />
      	
      	<div style="color: red;">
      		<%
      			if (!isBirthDateValid) {
      				out.println("<span>" + birthDateStateMessage + "</span>");
      			}
      		%>
      	</div>
      </div>
      
      <select name="provinceId" required>
		    <option value="">Select province</option>
		    <% 
		            for (Province province : provinces) {
		                boolean isSelected = province.getId() == currentPageState.getProvinceId();
		    %>
		                <option value="<%=province.getId()%>" <%=isSelected ? "selected" : ""%>>
		                    <%=province.getName()%>
		                </option>
		    <%
		            }
		    %>
		</select>
      
      <div>
        <label>Chọn hình ảnh: </label>
        <input type="file" accept=".jpg,.jpeg,.png" name="avatar" />
        
        <div style="color: red;">
        	<%
      			if (!isAvatarFilevalid) {
      				for (String message : avatarFileStateMessage) {
    					%>
    						<span><%= message %></span>
    					<%
      				}
      			}
      		%>
        </div>
      </div>
      
      <button>Lưu</button>
      </form>
	</div>
</body>
</html>