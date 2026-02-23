package com.example.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.context.ApplicationContext;

import com.example.context.ContextSingleton;
import com.example.model.Games;
import com.example.service.GameService;
import com.example.service.UserService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "gamesServlet", value = "/games")
public class GamesServlet extends HttpServlet {

    private ApplicationContext context;

    public void init() {
        this.context = ContextSingleton.getInstance().getContext();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GameService gameService  = context.getBean(GameService.class);
        UserService userService  = context.getBean(UserService.class);

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String idParam = request.getParameter("id");
        String msgOk   = request.getParameter("ok");
        String msgErr  = request.getParameter("err");

        out.println(HtmlHelper.head("Juegos"));
        out.println(HtmlHelper.nav("Juegos"));
        out.println("<div class='container'>");
        out.println("<h1>🎲 Gestión de Juegos</h1>");

        if (msgOk  != null) out.println("<div class='alert alert-success'>✔ " + HtmlHelper.safe(msgOk)  + "</div>");
        if (msgErr != null) out.println("<div class='alert alert-danger'>✖ "  + HtmlHelper.safe(msgErr) + "</div>");

        // Estadísticas
        java.util.List<Games> allGames = gameService.findAll();
        java.util.Map<String, Long> byCat = new java.util.LinkedHashMap<>();
        allGames.forEach(g -> byCat.merge(g.getCategory() != null ? g.getCategory() : "Sin categoría", 1L, Long::sum));
        out.println("<div class='stat-grid'>");
        out.println("<div class='stat-card'><div class='stat-num'>" + allGames.size() + "</div><div class='stat-label'>Juegos registrados</div></div>");
        byCat.forEach((cat, cnt) -> out.println("<div class='stat-card'><div class='stat-num'>" + cnt + "</div><div class='stat-label'>" + HtmlHelper.safe(cat) + "</div></div>"));
        out.println("</div>");

        // Formulario
        Games editing = null;
        if (idParam != null) {
            try { editing = gameService.findById(Integer.parseInt(idParam)); } catch (Exception e) { /* ignore */ }
        }
        out.println("<div class='card'>");
        out.println("<h2>" + (editing != null ? "✏️ Editar Juego" : "➕ Nuevo Juego") + "</h2>");
        out.println("<form method='post' action='games'>");
        if (editing != null) {
            out.println("<input type='hidden' name='action' value='update'>");
            out.println("<input type='hidden' name='id' value='" + editing.getId() + "'>");
        } else {
            out.println("<input type='hidden' name='action' value='create'>");
        }
        out.println("<div class='form-grid'>");
        out.println("<div class='form-group'><label>Nombre</label><input type='text' name='name' required value='" + (editing != null ? HtmlHelper.safe(editing.getName()) : "") + "'></div>");
        out.println("<div class='form-group'><label>Categoría</label><input type='text' name='category' value='" + (editing != null ? HtmlHelper.safe(editing.getCategory()) : "") + "'></div>");
        out.println("<div class='form-group'><label>Mín. Jugadores</label><input type='number' name='minPlayers' min='1' value='" + (editing != null && editing.getMinPlayers() != null ? editing.getMinPlayers() : "2") + "'></div>");
        out.println("<div class='form-group'><label>Máx. Jugadores</label><input type='number' name='maxPlayers' min='1' value='" + (editing != null && editing.getMaxPlayers() != null ? editing.getMaxPlayers() : "4") + "'></div>");
        out.println("<div class='form-group'><label>Creado por (Usuario)</label><select name='createdBy'>");
        out.println("<option value=''>Sin asignar</option>");
        Games finalEditing = editing;
        userService.findAll().forEach(u -> {
            String sel = (finalEditing != null && u.getId().equals(finalEditing.getCreatedBy())) ? " selected" : "";
            out.println("<option value='" + u.getId() + "'" + sel + ">" + HtmlHelper.safe(u.getUsername()) + "</option>");
        });
        out.println("</select></div>");
        out.println("<div class='form-group full'><label>Descripción</label><input type='text' name='description' value='" + (editing != null ? HtmlHelper.safe(editing.getDescription()) : "") + "'></div>");
        out.println("</div><br>");
        out.println("<button class='btn btn-primary' type='submit'>" + (editing != null ? "Actualizar" : "Crear Juego") + "</button>");
        if (editing != null) out.println(" <a href='games' class='btn btn-warning'>Cancelar</a>");
        out.println("</form></div>");

        // Tabla
        out.println("<div class='card'><h2>📋 Lista de Juegos</h2>");
        out.println("<table><tr><th>ID</th><th>Nombre</th><th>Descripción</th><th>Jugadores</th><th>Categoría</th><th>Creado por</th><th>Acciones</th></tr>");
        allGames.forEach(g -> {
            String creatorName = "";
            if (g.getCreatedBy() != null) {
                try { creatorName = userService.findById(g.getCreatedBy()).getUsername(); } catch (Exception e) { creatorName = "?"; }
            }
            out.println("<tr>");
            out.println("<td><span class='badge badge-purple'>#" + g.getId() + "</span></td>");
            out.println("<td><strong>" + HtmlHelper.safe(g.getName()) + "</strong></td>");
            out.println("<td>" + HtmlHelper.safe(g.getDescription()) + "</td>");
            out.println("<td>" + (g.getMinPlayers() != null ? g.getMinPlayers() : "?") + " – " + (g.getMaxPlayers() != null ? g.getMaxPlayers() : "?") + "</td>");
            out.println("<td>" + (g.getCategory() != null ? "<span class='badge badge-blue'>" + HtmlHelper.safe(g.getCategory()) + "</span>" : "-") + "</td>");
            out.println("<td>" + (creatorName.isEmpty() ? "-" : "<span class='badge badge-green'>" + HtmlHelper.safe(creatorName) + "</span>") + "</td>");
            out.println("<td><div class='actions'>");
            out.println("<a href='games?id=" + g.getId() + "' class='btn btn-warning btn-sm'>Editar</a>");
            out.println("<form method='post' action='games'><input type='hidden' name='action' value='delete'><input type='hidden' name='id' value='" + g.getId() + "'>");
            out.println("<button class='btn btn-danger btn-sm' onclick=\"return confirm('¿Eliminar " + HtmlHelper.safe(g.getName()) + "?')\">Eliminar</button></form>");
            out.println("</div></td></tr>");
        });
        out.println("</table></div>");
        out.println(HtmlHelper.foot());
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        GameService gameService = context.getBean(GameService.class);
        String action = request.getParameter("action");
        try {
            if ("create".equals(action)) {
                Games g = new Games(
                        request.getParameter("name"),
                        request.getParameter("description"),
                        parseIntOrNull(request.getParameter("minPlayers")),
                        parseIntOrNull(request.getParameter("maxPlayers")),
                        request.getParameter("category")
                );
                g.setCreatedBy(parseIntOrNull(request.getParameter("createdBy")));
                gameService.save(g);
                response.sendRedirect("games?ok=Juego+creado+exitosamente");
            } else if ("update".equals(action)) {
                Games g = gameService.findById(Integer.parseInt(request.getParameter("id")));
                g.setName(request.getParameter("name"));
                g.setDescription(request.getParameter("description"));
                g.setMinPlayers(parseIntOrNull(request.getParameter("minPlayers")));
                g.setMaxPlayers(parseIntOrNull(request.getParameter("maxPlayers")));
                g.setCategory(request.getParameter("category"));
                g.setCreatedBy(parseIntOrNull(request.getParameter("createdBy")));
                gameService.save(g);
                response.sendRedirect("games?ok=Juego+actualizado+exitosamente");
            } else if ("delete".equals(action)) {
                gameService.delete(Integer.parseInt(request.getParameter("id")));
                response.sendRedirect("games?ok=Juego+eliminado");
            }
        } catch (Exception e) {
            response.sendRedirect("games?err=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }

    private Integer parseIntOrNull(String v) {
        if (v == null || v.trim().isEmpty()) return null;
        try { return Integer.parseInt(v.trim()); } catch (NumberFormatException e) { return null; }
    }

    public void destroy() { System.out.println("GamesServlet destroyed"); }
}
