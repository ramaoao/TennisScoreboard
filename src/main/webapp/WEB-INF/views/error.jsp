<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="header.jsp"/>

<main>
    <div class="error-page">
        <div class="error-image">
            <img src="${pageContext.request.contextPath}/images/error-img.png" alt="Error image" class="error-img">
        </div>

        <div class="error-message-container center">
            <h1>Error!</h1>
            <p>${errorMessage != null ? errorMessage : "Iternal Server Error."}</p>
        </div>
    </div>
</main>

<jsp:include page="footer.jsp"/>