package com.example.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.context.ApplicationContext;

import com.example.context.ContextSingleton;
import com.example.model.Roles;
import com.example.service.PermissionService;
import com.example.service.RolePermissionService;
import com.example.service.RoleService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "rolesServlet", value = "/roles")
public class RolesServlet extends HttpServlet {

    private ApplicationContext context;

    public void init() {
        this.context = ContextSingleton.getInstance().getContext();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RoleService roleService             = context.getBean(RoleService.class);
        PermissionService permService       = context.getBean(PermissionService.class);
        RolePermissionService rpService     = context.getBean(RolePermissionService.class);

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String idParam = request.getParameter("id");
        String msgOk   = request.getParameter("ok");
        String msgErr  = request.getParameter("err");

        out.println(HtmlHelper.head("Roles"));
        out.println(HtmlHelper.nav("Roles"));
        out.println("<div class='container'>");
        out.println("<h1>🛡️ Gestión de Roles</h1>");

        if (msgOk  != null) out.println("<div class='alert alert-success'>✔ " + HtmlHelper.safe(msgOk)  + "</div>");
        if (msgErr != null) out.println("<div class='alert alert-danger'>✖ "  + HtmlHelper.safe(msgErr) + "</div>");

        // Estadísticas
        out.println("<div class='stat-grid'>");
        out.println("<div class='stat-card'><div class='stat-num'>" + roleService.findAll().size() + "</div><div class='stat-label'>Roles definidos</div></div>");
        out.println("<div class='stat-card'><div class='stat-num'>" + rpService.findAll().size()   + "</div><div class='stat-label'>Asignaciones activas</div></div>");
        out.println("</div>");

        // Formulario
        Roles editing = null;
        if (idParam != null) {
            try { editing = roleService.findById(Integer.parseInt(idParam)); } catch (Exception e) { /* ignore */ }
        }
        out.println("<div class='card'>");
        out.println("<h2>" + (editing != null ? "✏️ Editar Rol" : "➕ Nuevo Rol") + "</h2>");
        out.println("<form method='post' action='roles'>");
        if (editing != null) {
            out.println("<input type='hidden' name='action' value='update'>");
            out.println("<input type='hidden' name='id' value='" + editing.getId() + "'>");
        } else {
            out.println("<input type='hidden' name='action' value='create'>");
        }
        out.println("<div class='form-grid'>");
        out.println("<div class='form-group'><label>Nombre</label><input type='text' name='name' required value='" + (editing != null ? HtmlHelper.safe(editing.getName()) : "") + "'></div>");
        out.println("<div class='form-group'><label>Descripción</label><input type='text' name='description' value='" + (editing != null ? HtmlHelper.safe(editing.getDescription()) : "") + "'></div>");
        out.println("</div><br>");
        out.println("<button class='btn btn-primary' type='submit'>" + (editing != null ? "Actualizar" : "Crear Rol") + "</button>");
        if (editing != null) out.println(" <a href='roles' class='btn btn-warning'>Cancelar</a>");
        out.println("</form></div>");

        // Tabla de roles con sus permisos
        out.println("<div class='card'><h2>📋 Lista de Roles</h2>");
        out.println("<table><tr><th>ID</th><th>Nombre</th><th>Descripción</th><th>Permisos</th><th>Acciones</th></tr>");
        roleService.findAll().forEach(r -> {
            out.println("<tr>");
            out.println("<td><span class='badge badge-purple'>#" + r.getId() + "</span></td>");
            out.println("<td><strong>" + HtmlHelper.safe(r.getName()) + "</strong></td>");
            out.println("<td>" + HtmlHelper.safe(r.getDescription()) + "</td>");
            out.println("<td>");
            rpService.findByRoleId(r.getId()).forEach(rp -> {
                try {
                    String pName = permService.findById(rp.getPermissionId()).getName();
                    out.println("<span class='badge badge-green' style='margin:2px'>" + HtmlHelper.safe(pName) + "</span>");
                } catch (Exception e) { /* permiso no encontrado */ }
            });
            out.println("</td>");
            out.println("<td><div class='actions'>");
            out.println("<a href='roles?id=" + r.getId() + "' class='btn btn-warning btn-sm'>Editar</a>");
            out.println("<form method='post' action='roles'><input type='hidden' name='action' value='delete'><input type='hidden' name='id' value='" + r.getId() + "'>");
            out.println("<button class='btn btn-danger btn-sm' onclick=\"return confirm('¿Eliminar rol " + HtmlHelper.safe(r.getName()) + "?')\">Eliminar</button></form>");
            out.println("</div></td></tr>");
        });
        out.println("</table></div>");
        out.println(HtmlHelper.foot());
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RoleService roleService = context.getBean(RoleService.class);
        String action = request.getParameter("action");
        try {
            if ("create".equals(action)) {
                roleService.save(new Roles(request.getParameter("name"), request.getParameter("description")));
                response.sendRedirect("roles?ok=Rol+creado+exitosamente");
            } else if ("update".equals(action)) {
                Roles r = roleService.findById(Integer.parseInt(request.getParameter("id")));
                r.setName(request.getParameter("name"));
                r.setDescription(request.getParameter("description"));
                roleService.save(r);
                response.sendRedirect("roles?ok=Rol+actualizado+exitosamente");
            } else if ("delete".equals(action)) {
                roleService.delete(Integer.parseInt(request.getParameter("id")));
                response.sendRedirect("roles?ok=Rol+eliminado");
            }
        } catch (Exception e) {
            response.sendRedirect("roles?err=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }

    public void destroy() { System.out.println("RolesServlet destroyed"); }
}
