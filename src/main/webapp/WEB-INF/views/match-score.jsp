<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:include page="header.jsp"/>

<main>
    <div class="container">
        <h1>Current match</h1>
        <div class="current-match-image"></div>
        <section class="playerScore">
            <table class="table">
                <thead class="result">
                <tr>
                    <th class="table-text">Player</th>
                    <th class="table-text">Sets</th>
                    <th class="table-text">Games</th>
                    <th class="table-text">Points</th>
                </tr>
                </thead>
                <tbody>
                <tr class="player1">
                    <td class="table-text">${match.player1Name}</td>
                    <td class="table-text">${match.player1Sets}</td>
                    <td class="table-text">${match.player1Games}</td>
                    <td class="table-text">${match.player1Points}</td>
                    <td class="table-text">
                        <form method="POST" action="${pageContext.request.contextPath}/match-score">
                            <input type="hidden" name="uuid" value=${match.uuid}>
                            <input type="hidden" name="scorerId" value="${match.player1Id}">
                            <button type="submit" class="score-btn">Score</button>
                        </form>
                    </td>
                </tr>

                <tr class="player2">
                    <td class="table-text">${match.player2Name}</td>
                    <td class="table-text">${match.player2Sets}</td>
                    <td class="table-text">${match.player2Games}</td>
                    <td class="table-text">${match.player2Points}</td>
                    <td class="table-text">
                        <form method="POST" action="${pageContext.request.contextPath}/match-score">
                            <input type="hidden" name="uuid" value=${match.uuid}>
                            <input type="hidden" name="scorerId" value="${match.player2Id}">
                            <button type="submit" class="score-btn">Score</button>
                        </form>
                    </td>
                </tr>
                </tbody>
            </table>
        </section>
    </div>
</main>

<jsp:include page="footer.jsp"/>