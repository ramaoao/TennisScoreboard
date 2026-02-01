<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="header.jsp"/>

<main>
    <div class="container">
        <h1>Matches</h1>
        <div class="input-container">
            <form action="${pageContext.request.contextPath}/matches" method="GET" style="display: flex">
                <input class="input-filter"
                       name="filter_by_player_name"
                       value="${filterByPlayerName}"
                       placeholder="Filter by name"
                       type="text"
                       required />

                <button type="submit" class="btn-filter">Search</button>
            </form>

            <div>
                <a href="${pageContext.request.contextPath}/matches">
                    <button class="btn-filter">Reset Filter</button>
                </a>
            </div>
        </div>

        <table class="table-matches">
            <thead>
            <tr>
                <th>Player One</th>
                <th>Player Two</th>
                <th>Winner</th>
            </tr>
            </thead>
            <tbody>
            <c:if test="${empty matches}">
                <tr>
                    <td colspan="3" style="text-align: center;">No matches found.</td>
                </tr>
            </c:if>
            <c:forEach var="match" items="${matches}">
                <tr>
                    <td>${match.player1Name}</td>
                    <td>${match.player2Name}</td>
                    <td>
                    <span class="winner-name-td">${match.winnerName}</span>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <c:if test="${totalPages > 1}">
            <div class="pagination">
                <c:if test="${currentPage > 1}">
                    <a class="prev" href="${pageContext.request.contextPath}/matches?page=${currentPage - 1}&filter_by_player_name=${filterByPlayerName}"> < </a>
                </c:if>

                <c:forEach begin="1" end="${totalPages}" var="i">
                    <a class="num-page ${currentPage == i ? 'current' : ''}" href="${pageContext.request.contextPath}/matches?page=${i}&filter_by_player_name=${filterByPlayerName}">${i}</a>
                </c:forEach>

                <c:if test="${currentPage < totalPages}">
                    <a class="next" href="${pageContext.request.contextPath}/matches?page=${currentPage + 1}&filter_by_player_name=${filterByPlayerName}"> > </a>
                </c:if>
            </div>
        </c:if>
    </div>
</main>

<jsp:include page="footer.jsp"/>