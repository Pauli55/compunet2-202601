package com.example.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

import org.springframework.context.ApplicationContext;

import com.example.context.ContextSingleton;
import com.example.model.User;
import com.example.service.RoleService;
import com.example.service.UserService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "usersServlet", value = "/users")
public class UsersServlet extends HttpServlet {

    private ApplicationContext context;

    public void init() {
        this.context = ContextSingleton.getInstance().getContext();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserService userService = context.getBean(UserService.class);
        RoleService roleService = context.getBean(RoleService.class);
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String idParam   = request.getParameter("id");
        String msgOk     = request.getParameter("ok");
        String msgErr    = request.getParameter("err");

        out.println(HtmlHelper.head("Usuarios"));
        out.println(HtmlHelper.nav("Usuarios"));
        out.println("<div class='container'>");
        out.println("<h1>👤 Gestión de Usuarios</h1>");

        if (msgOk  != null) out.println("<div class='alert alert-success'>✔ " + HtmlHelper.safe(msgOk)  + "</div>");
        if (msgErr != null) out.println("<div class='alert alert-danger'>✖ "  + HtmlHelper.safe(msgErr) + "</div>");

        // ── Estadísticas ──
        long total = userService.findAll().size();
        out.println("<div class='stat-grid'>");
        out.println("<div class='stat-card'><div class='stat-num'>" + total + "</div><div class='stat-label'>Usuarios registrados</div></div>");
        out.println("</div>");

        // ── Formulario crear / editar ──
        User editing = null;
        if (idParam != null) {
            try { editing = userService.findById(Integer.parseInt(idParam)); } catch (Exception e) { /* ignorar */ }
        }

        out.println("<div class='card'>");
        out.println("<h2>" + (editing != null ? "✏️ Editar Usuario" : "➕ Nuevo Usuario") + "</h2>");
        out.println("<form method='post' action='users'>");
        if (editing != null) {
            out.println("<input type='hidden' name='action' value='update'>");
            out.println("<input type='hidden' name='id' value='" + editing.getId() + "'>");
        } else {
            out.println("<input type='hidden' name='action' value='create'>");
        }
        out.println("<div class='form-grid'>");
        out.println("<div class='form-group'><label>Username</label><input type='text' name='username' required value='" + (editing != null ? HtmlHelper.safe(editing.getUsername()) : "") + "'></div>");
        out.println("<div class='form-group'><label>Email</label><input type='email' name='email' required value='" + (editing != null ? HtmlHelper.safe(editing.getEmail()) : "") + "'></div>");
        out.println("<div class='form-group'><label>Password " + (editing != null ? "(dejar vacío = no cambiar)" : "") + "</label><input type='password' name='password' " + (editing == null ? "required" : "") + "></div>");
        out.println("<div class='form-group'><label>Rol</label><select name='roleId'>");
        out.println("<option value=''>Sin rol</option>");
        User finalEditing = editing;
        roleService.findAll().forEach(r -> {
            String sel = (finalEditing != null && r.getId().equals(finalEditing.getRoleId())) ? " selected" : "";
            out.println("<option value='" + r.getId() + "'" + sel + ">" + HtmlHelper.safe(r.getName()) + "</option>");
        });
        out.println("</select></div>");
        out.println("<div class='form-group full'><label>Bio</label><input type='text' name='bio' value='" + (editing != null ? HtmlHelper.safe(editing.getBio()) : "") + "'></div>");
        out.println("</div>");
        out.println("<br><button class='btn btn-primary' type='submit'>" + (editing != null ? "Actualizar" : "Crear Usuario") + "</button>");
        if (editing != null) out.println(" <a href='users' class='btn btn-warning'>Cancelar</a>");
        out.println("</form></div>");

        // ── Tabla ──
        out.println("<div class='card'><h2>📋 Lista de Usuarios</h2>");
        out.println("<table><tr><th>ID</th><th>Username</th><th>Email</th><th>Bio</th><th>Rol</th><th>Creado</th><th>Acciones</th></tr>");
        userService.findAll().forEach(u -> {
            String roleName = "";
            if (u.getRoleId() != null) {
                try { roleName = roleService.findById(u.getRoleId()).getName(); } catch (Exception e) { roleName = "?"; }
            }
            out.println("<tr>");
            out.println("<td><span class='badge badge-purple'>#" + u.getId() + "</span></td>");
            out.println("<td><strong>" + HtmlHelper.safe(u.getUsername()) + "</strong></td>");
            out.println("<td>" + HtmlHelper.safe(u.getEmail()) + "</td>");
            out.println("<td>" + HtmlHelper.safe(u.getBio()) + "</td>");
            out.println("<td>" + (roleName.isEmpty() ? "-" : "<span class='badge badge-blue'>" + HtmlHelper.safe(roleName) + "</span>") + "</td>");
            out.println("<td style='font-size:.8em;color:#6b6b8a'>" + (u.getCreatedAt() != null ? u.getCreatedAt().toString().substring(0,10) : "-") + "</td>");
            out.println("<td><div class='actions'>");
            out.println("<a href='users?id=" + u.getId() + "' class='btn btn-warning btn-sm'>Editar</a>");
            out.println("<form method='post' action='users'><input type='hidden' name='action' value='delete'><input type='hidden' name='id' value='" + u.getId() + "'>");
            out.println("<button class='btn btn-danger btn-sm' onclick=\"return confirm('¿Eliminar usuario " + HtmlHelper.safe(u.getUsername()) + "?')\">Eliminar</button></form>");
            out.println("</div></td></tr>");
        });
        out.println("</table></div>");
        out.println(HtmlHelper.foot());
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserService userService = context.getBean(UserService.class);
        String action = request.getParameter("action");
        try {
            if ("create".equals(action)) {
                User u = new User(
                        request.getParameter("username"),
                        request.getParameter("email"),
                        request.getParameter("password"),
                        request.getParameter("bio"),
                        new Timestamp(System.currentTimeMillis())
                );
                String roleIdStr = request.getParameter("roleId");
                if (roleIdStr != null && !roleIdStr.isEmpty()) u.setRoleId(Integer.parseInt(roleIdStr));
                userService.save(u);
                response.sendRedirect("users?ok=Usuario+creado+exitosamente");
            } else if ("update".equals(action)) {
                Integer id = Integer.parseInt(request.getParameter("id"));
                User u = userService.findById(id);
                u.setUsername(request.getParameter("username"));
                u.setEmail(request.getParameter("email"));
                u.setBio(request.getParameter("bio"));
                String pass = request.getParameter("password");
                if (pass != null && !pass.isEmpty()) u.setPasswordHash(pass);
                String roleIdStr = request.getParameter("roleId");
                u.setRoleId((roleIdStr != null && !roleIdStr.isEmpty()) ? Integer.parseInt(roleIdStr) : null);
                userService.save(u);
                response.sendRedirect("users?ok=Usuario+actualizado+exitosamente");
            } else if ("delete".equals(action)) {
                userService.delete(Integer.parseInt(request.getParameter("id")));
                response.sendRedirect("users?ok=Usuario+eliminado");
            }
        } catch (Exception e) {
            response.sendRedirect("users?err=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }

    public void destroy() { System.out.println("UsersServlet destroyed"); }
}
