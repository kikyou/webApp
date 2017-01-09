<%@ page
    contentType="text/html; charset=UTF-8"
    import="javax.servlet.*"
    import="javax.servlet.http.*"
    import="java.io.*"
    import="java.util.*"
    import="java.net.URL"
    import="java.text.*"
    import="org.baidu.ecom.webApp.*"
%>

<%
    HttpServer server = (HttpServer) application.getAttribute("HttpServer");
%>

<html>
    <head>
        <title>BigSQL Monitor</title>
        <link rel="stylesheet" type="text/css" href="/static/hadoop.css">
    </head>
    <body>
        <h1>BigSQL Monitor</h1>
        <table border="2" cellpadding="5" cellspacing="2">
            <thead style="font-weight: bold">
                <tr>
                    <th colspan="2">Summary</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td><b>MasterId:</b></td>
                    <td><%= server.getId() %></td>
                </tr>
            </tbody>
        </table>
    </body>
</html>