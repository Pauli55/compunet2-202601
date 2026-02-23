package com.example.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.context.ApplicationContext;

import com.example.context.ContextSingleton;
import com.example.service.PermissionService;
import com.example.service.RolePermissionService;
import com.example.service.RoleService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "rolePermissionsServlet", value = "/role-permissions")
public class RolePermissionsServlet extends HttpServlet {

    private ApplicationContext context;

    public void init() {
        this.context = ContextSingleton.getInstance().getContext();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RoleService roleService         = context.getBean(RoleService.class);
        PermissionService permService   = context.getBean(PermissionService.class);
        RolePermissionService rpService = context.getBean(RolePermissionService.class);

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String msgOk  = request.getParameter("ok");
        String msgErr = request.getParameter("err");

        out.println(HtmlHelper.head("Roles-Permisos"));
        out.println(HtmlHelper.nav("Roles-Permisos"));
        out.println("<div class='container'>");
        out.println("<h1>🔗 Asignación de Roles y Permisos</h1>");

        if (msgOk  != null) out.println("<div class='alert alert-success'>✔ " + HtmlHelper.safe(msgOk)  + "</div>");
        if (msgErr != null) out.println("<div class='alert alert-danger'>✖ "  + HtmlHelper.safe(msgErr) + "</div>");

        // Estadísticas
        out.println("<div class='stat-grid'>");
        out.println("<div class='stat-card'><div class='stat-num'>" + rpService.findAll().size() + "</div><div class='stat-label'>Asignaciones activas</div></div>");
        out.println("<div class='stat-card'><div class='stat-num'>" + roleService.findAll().size() + "</div><div class='stat-label'>Roles</div></div>");
        out.println("<div class='stat-card'><div class='stat-num'>" + permService.findAll().size() + "</div><div class='stat-label'>Permisos</div></div>");
        out.println("</div>");

        // Formulario de asignación
        out.println("<div class='card'>");
        out.println("<h2>➕ Asignar Permiso a Rol</h2>");
        out.println("<form method='post' action='role-permissions'>");
        out.println("<input type='hidden' name='action' value='assign'>");
        out.println("<div class='form-grid'>");
        out.println("<div class='form-group'><label>Rol</label><select name='roleId' required>");
        out.println("<option value=''>-- Seleccionar rol --</option>");
        roleService.findAll().forEach(r ->
                out.println("<option value='" + r.getId() + "'>" + HtmlHelper.safe(r.getName()) + " - " + HtmlHelper.safe(r.getDescription()) + "</option>")
        );
        out.println("</select></div>");
        out.println("<div class='form-group'><label>Permiso</label><select name='permissionId' required>");
        out.println("<option value=''>-- Seleccionar permiso --</option>");
        permService.findAll().forEach(p ->
                out.println("<option value='" + p.getId() + "'>" + HtmlHelper.safe(p.getName()) + " - " + HtmlHelper.safe(p.getDescription()) + "</option>")
        );
        out.println("</select></div>");
        out.println("</div><br>");
        out.println("<button class='btn btn-primary' type='submit'>Asignar Permiso</button>");
        out.println("</form></div>");

        // Vista por roles (matriz)
        out.println("<div class='card'><h2>🗂️ Permisos por Rol</h2>");
        roleService.findAll().forEach(r -> {
            out.println("<div style='margin-bottom:16px;'>");
            out.println("<h3 style='color:#c4b5fd;margin-bottom:8px;'><span class='badge badge-purple'>" + HtmlHelper.safe(r.getName()) + "</span> <span style='color:#6b6b8a;font-size:.85em;font-weight:400;'>" + HtmlHelper.safe(r.getDescription()) + "</span></h3>");
            java.util.List<com.example.model.RolePermissions> rps = rpService.findByRoleId(r.getId());
            if (rps.isEmpty()) {
                out.println("<span style='color:#6b6b8a;font-size:.85em;'>Sin permisos asignados</span>");
            } else {
                rps.forEach(rp -> {
                    try {
                        String pName = permService.findById(rp.getPermissionId()).getName();
                        out.println("<span class='badge badge-green' style='margin:3px'>" + HtmlHelper.safe(pName) + "</span>");
                        out.println("<form method='post' action='role-permissions' style='display:inline'>");
                        out.println("<input type='hidden' name='action' value='revoke'>");
                        out.println("<input type='hidden' name='roleId' value='" + r.getId() + "'>");
                        out.println("<input type='hidden' name='permissionId' value='" + rp.getPermissionId() + "'>");
                        out.println("<button class='btn btn-danger btn-sm' style='padding:2px 8px;font-size:.72em;margin-right:6px' title='Revocar " + HtmlHelper.safe(pName) + "'>✕</button></form>");
                    } catch (Exception e) { /* ignore */ }
                });
            }
            out.println("</div><hr style='border-color:#2d2d4e;margin:12px 0'>");
        });
        out.println("</div>");

        // Tabla completa de asignaciones
        out.println("<div class='card'><h2>📋 Todas las Asignaciones</h2>");
        out.println("<table><tr><th>ID</th><th>Rol</th><th>Permiso</th><th>Acciones</th></tr>");
        rpService.findAll().forEach(rp -> {
            String roleName  = "";
            String permName  = "";
            try { roleName = roleService.findById(rp.getRoleId()).getName(); }   catch (Exception e) { roleName  = "Desconocido"; }
            try { permName = permService.findById(rp.getPermissionId()).getName(); } catch (Exception e) { permName = "Desconocido"; }
            out.println("<tr>");
            out.println("<td><span class='badge badge-purple'>#" + rp.getId() + "</span></td>");
            out.println("<td><span class='badge badge-blue'>" + HtmlHelper.safe(roleName) + "</span></td>");
            out.println("<td><span class='badge badge-green'>" + HtmlHelper.safe(permName) + "</span></td>");
            out.println("<td><form method='post' action='role-permissions'>");
            out.println("<input type='hidden' name='action' value='delete'>");
            out.println("<input type='hidden' name='id' value='" + rp.getId() + "'>");
            out.println("<button class='btn btn-danger btn-sm' onclick=\"return confirm('¿Eliminar esta asignación?')\">Eliminar</button></form></td>");
            out.println("</tr>");
        });
        out.println("</table></div>");
        out.println(HtmlHelper.foot());
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RolePermissionService rpService = context.getBean(RolePermissionService.class);
        String action = request.getParameter("action");
        try {
            if ("assign".equals(action)) {
                rpService.assign(
                        Integer.parseInt(request.getParameter("roleId")),
                        Integer.parseInt(request.getParameter("permissionId"))
                );
                response.sendRedirect("role-permissions?ok=Permiso+asignado+exitosamente");
            } else if ("revoke".equals(action)) {
                rpService.revoke(
                        Integer.parseInt(request.getParameter("roleId")),
                        Integer.parseInt(request.getParameter("permissionId"))
                );
                response.sendRedirect("role-permissions?ok=Permiso+revocado");
            } else if ("delete".equals(action)) {
                rpService.delete(Integer.parseInt(request.getParameter("id")));
                response.sendRedirect("role-permissions?ok=Asignación+eliminada");
            }
        } catch (Exception e) {
            response.sendRedirect("role-permissions?err=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }

    public void destroy() { System.out.println("RolePermissionsServlet destroyed"); }
}
