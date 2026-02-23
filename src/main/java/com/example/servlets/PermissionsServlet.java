package com.example.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.context.ApplicationContext;

import com.example.context.ContextSingleton;
import com.example.model.Permissions;
import com.example.service.PermissionService;
import com.example.service.RolePermissionService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "permissionsServlet", value = "/permissions")
public class PermissionsServlet extends HttpServlet {

    private ApplicationContext context;

    public void init() {
        this.context = ContextSingleton.getInstance().getContext();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PermissionService permService  = context.getBean(PermissionService.class);
        RolePermissionService rpService = context.getBean(RolePermissionService.class);

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String idParam = request.getParameter("id");
        String msgOk   = request.getParameter("ok");
        String msgErr  = request.getParameter("err");

        out.println(HtmlHelper.head("Permisos"));
        out.println(HtmlHelper.nav("Permisos"));
        out.println("<div class='container'>");
        out.println("<h1>🔑 Gestión de Permisos</h1>");

        if (msgOk  != null) out.println("<div class='alert alert-success'>✔ " + HtmlHelper.safe(msgOk)  + "</div>");
        if (msgErr != null) out.println("<div class='alert alert-danger'>✖ "  + HtmlHelper.safe(msgErr) + "</div>");

        // Estadísticas
        out.println("<div class='stat-grid'>");
        out.println("<div class='stat-card'><div class='stat-num'>" + permService.findAll().size() + "</div><div class='stat-label'>Permisos registrados</div></div>");
        out.println("</div>");

        // Formulario
        Permissions editing = null;
        if (idParam != null) {
            try { editing = permService.findById(Integer.parseInt(idParam)); } catch (Exception e) { /* ignore */ }
        }
        out.println("<div class='card'>");
        out.println("<h2>" + (editing != null ? "✏️ Editar Permiso" : "➕ Nuevo Permiso") + "</h2>");
        out.println("<form method='post' action='permissions'>");
        if (editing != null) {
            out.println("<input type='hidden' name='action' value='update'>");
            out.println("<input type='hidden' name='id' value='" + editing.getId() + "'>");
        } else {
            out.println("<input type='hidden' name='action' value='create'>");
        }
        out.println("<div class='form-grid'>");
        out.println("<div class='form-group'><label>Nombre (ej: READ_USER)</label><input type='text' name='name' required value='" + (editing != null ? HtmlHelper.safe(editing.getName()) : "") + "'></div>");
        out.println("<div class='form-group'><label>Descripción</label><input type='text' name='description' value='" + (editing != null ? HtmlHelper.safe(editing.getDescription()) : "") + "'></div>");
        out.println("</div><br>");
        out.println("<button class='btn btn-primary' type='submit'>" + (editing != null ? "Actualizar" : "Crear Permiso") + "</button>");
        if (editing != null) out.println(" <a href='permissions' class='btn btn-warning'>Cancelar</a>");
        out.println("</form></div>");

        // Tabla
        out.println("<div class='card'><h2>📋 Lista de Permisos</h2>");
        out.println("<table><tr><th>ID</th><th>Nombre</th><th>Descripción</th><th>Roles que lo usan</th><th>Acciones</th></tr>");
        permService.findAll().forEach(p -> {
            long usos = rpService.findByPermissionId(p.getId()).size();
            out.println("<tr>");
            out.println("<td><span class='badge badge-purple'>#" + p.getId() + "</span></td>");
            out.println("<td><span class='badge badge-green'>" + HtmlHelper.safe(p.getName()) + "</span></td>");
            out.println("<td>" + HtmlHelper.safe(p.getDescription()) + "</td>");
            out.println("<td>" + usos + " rol(es)</td>");
            out.println("<td><div class='actions'>");
            out.println("<a href='permissions?id=" + p.getId() + "' class='btn btn-warning btn-sm'>Editar</a>");
            out.println("<form method='post' action='permissions'><input type='hidden' name='action' value='delete'><input type='hidden' name='id' value='" + p.getId() + "'>");
            out.println("<button class='btn btn-danger btn-sm' onclick=\"return confirm('¿Eliminar permiso " + HtmlHelper.safe(p.getName()) + "?')\">Eliminar</button></form>");
            out.println("</div></td></tr>");
        });
        out.println("</table></div>");
        out.println(HtmlHelper.foot());
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PermissionService permService = context.getBean(PermissionService.class);
        String action = request.getParameter("action");
        try {
            if ("create".equals(action)) {
                permService.save(new Permissions(request.getParameter("name"), request.getParameter("description")));
                response.sendRedirect("permissions?ok=Permiso+creado+exitosamente");
            } else if ("update".equals(action)) {
                Permissions p = permService.findById(Integer.parseInt(request.getParameter("id")));
                p.setName(request.getParameter("name"));
                p.setDescription(request.getParameter("description"));
                permService.save(p);
                response.sendRedirect("permissions?ok=Permiso+actualizado+exitosamente");
            } else if ("delete".equals(action)) {
                permService.delete(Integer.parseInt(request.getParameter("id")));
                response.sendRedirect("permissions?ok=Permiso+eliminado");
            }
        } catch (Exception e) {
            response.sendRedirect("permissions?err=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }

    public void destroy() { System.out.println("PermissionsServlet destroyed"); }
}
