<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:include page="header.jsp"/>

<main>
    <div class="container">
        <h1>Welcome to Tennis Scoreboard</h1>
        <p>Manage your tennis matches, record results, and track rankings</p>
        <div class="welcome-image"></div>
        <div class="form-container center">
            <a class="homepage-action-button" href="${pageContext.request.contextPath}/new-match">
                <button class="btn start-match">
                    Start a new match
                </button>
            </a>
            <a class="homepage-action-button" href="${pageContext.request.contextPath}/matches">
                <button class="btn view-results">
                    View match results
                </button>
            </a>
        </div>
    </div>
</main>

<jsp:include page="footer.jsp"/>