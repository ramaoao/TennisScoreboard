<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="header.jsp"/>

<main>
    <div class="container">
        <div>
            <h1>Start new match</h1>
            <div class="new-match-image"></div>
            <div class="form-container center">
                <form method="POST" action="${pageContext.request.contextPath}/new-match">

                    <c:if test="${not empty errorMessage}">
                        <p style="color: red;">
                                ${errorMessage}
                        </p>
                    </c:if>

                    <label class="label-player" for="player1">First player</label>
                    <input class="input-player"
                           placeholder="Name"
                           type="text"
                           id="player1"
                           name="player1"
                           title="Enter a name"
                           required
                           value="${requestScope.prevName1 != null ? requestScope.prevName1 : ''}">

                    <label class="label-player" for="player2">Second player</label>
                    <input class="input-player"
                           placeholder="Name"
                           type="text"
                           id="player2"
                           name="player2"
                           title="Enter a name"
                           required
                           value="${requestScope.prevName2 != null ? requestScope.prevName2 : ''}">

                    <input class="form-button" type="submit" value="Start">
                </form>
            </div>
        </div>
    </div>
</main>

<jsp:include page="footer.jsp"/>