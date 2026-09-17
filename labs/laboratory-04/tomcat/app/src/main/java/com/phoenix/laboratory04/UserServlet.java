package com.phoenix.laboratory04;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/users")
public class UsersServlet extends HttpServlet {

    private DataSource dataSource;

    @Override
    public void init() throws ServletException {
        try {
            InitialContext context = new InitialContext();

            dataSource = (DataSource) context.lookup(
                "java:comp/env/jdbc/LaboratoryDB"
            );

        } catch (NamingException e) {
            throw new ServletException(
                "Unable to initialize JDBC DataSource",
                e
            );
        }
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        String sql = """
            SELECT idusr, nombre
            FROM tbUsuarios
            ORDER BY idusr
            """;

        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            PrintWriter out = response.getWriter()
        ) {

            out.println("<html>");
            out.println("<head><title>Laboratory 04</title></head>");
            out.println("<body>");
            out.println("<h1>Laboratory 04 - Users</h1>");
            out.println("<table border='1'>");
            out.println("<tr><th>ID</th><th>Nombre</th></tr>");

            while (resultSet.next()) {
                out.printf(
                    "<tr><td>%d</td><td>%s</td></tr>%n",
                    resultSet.getInt("idusr"),
                    resultSet.getString("nombre")
                );
            }

            out.println("</table>");
            out.println("</body>");
            out.println("</html>");

        } catch (SQLException e) {
            throw new ServletException(
                "Database query failed",
                e
            );
        }
    }
}