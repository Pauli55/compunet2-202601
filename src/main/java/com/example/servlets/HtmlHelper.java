package com.example.servlets;

/**
 * Utilidad para generar HTML común entre todos los servlets.
 */
public class HtmlHelper {

    public static String head(String title) {
        return "<!DOCTYPE html><html lang='es'><head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1'>" +
                "<title>" + title + " | GameApp</title>" +
                "<style>" +
                "  *{box-sizing:border-box;margin:0;padding:0;}" +
                "  body{font-family:'Segoe UI',Arial,sans-serif;background:#0f0f1a;color:#e0e0f0;min-height:100vh;}" +
                "  nav{background:#1a1a2e;padding:14px 32px;display:flex;align-items:center;gap:24px;border-bottom:2px solid #7c3aed;}" +
                "  nav a{color:#a78bfa;text-decoration:none;font-weight:600;padding:6px 12px;border-radius:6px;transition:background .2s;}" +
                "  nav a:hover,nav a.active{background:#7c3aed;color:#fff;}" +
                "  nav .brand{color:#c4b5fd;font-size:1.3em;font-weight:700;margin-right:auto;}" +
                "  .container{max-width:1100px;margin:32px auto;padding:0 24px;}" +
                "  h1{color:#c4b5fd;margin-bottom:24px;font-size:1.8em;}" +
                "  h2{color:#a78bfa;margin:24px 0 12px;font-size:1.2em;}" +
                "  table{width:100%;border-collapse:collapse;background:#1a1a2e;border-radius:10px;overflow:hidden;margin-bottom:24px;}" +
                "  th{background:#7c3aed;color:#fff;padding:12px 16px;text-align:left;font-size:.85em;text-transform:uppercase;letter-spacing:.5px;}" +
                "  td{padding:11px 16px;border-bottom:1px solid #2d2d4e;font-size:.9em;}" +
                "  tr:last-child td{border-bottom:none;}" +
                "  tr:hover td{background:#23234a;}" +
                "  .card{background:#1a1a2e;border:1px solid #2d2d4e;border-radius:12px;padding:24px;margin-bottom:24px;}" +
                "  .form-grid{display:grid;grid-template-columns:1fr 1fr;gap:16px;}" +
                "  .form-group{display:flex;flex-direction:column;gap:6px;}" +
                "  .form-group.full{grid-column:1/-1;}" +
                "  label{font-size:.8em;color:#a78bfa;font-weight:600;text-transform:uppercase;letter-spacing:.5px;}" +
                "  input[type=text],input[type=email],input[type=password],input[type=number],select{" +
                "    background:#0f0f1a;border:1px solid #3d3d6e;color:#e0e0f0;padding:9px 12px;border-radius:6px;font-size:.9em;width:100%;}" +
                "  input:focus,select:focus{outline:none;border-color:#7c3aed;}" +
                "  .btn{display:inline-block;padding:9px 20px;border-radius:6px;border:none;cursor:pointer;font-size:.9em;font-weight:600;transition:opacity .2s;}" +
                "  .btn-primary{background:#7c3aed;color:#fff;}" +
                "  .btn-danger{background:#dc2626;color:#fff;}" +
                "  .btn-warning{background:#d97706;color:#fff;}" +
                "  .btn-sm{padding:5px 12px;font-size:.8em;}" +
                "  .btn:hover{opacity:.85;}" +
                "  .badge{display:inline-block;padding:3px 10px;border-radius:20px;font-size:.75em;font-weight:600;}" +
                "  .badge-purple{background:#4c1d95;color:#c4b5fd;}" +
                "  .badge-green{background:#064e3b;color:#6ee7b7;}" +
                "  .badge-blue{background:#1e3a5f;color:#93c5fd;}" +
                "  .alert{padding:12px 16px;border-radius:8px;margin-bottom:16px;}" +
                "  .alert-danger{background:#450a0a;border:1px solid #dc2626;color:#fca5a5;}" +
                "  .alert-success{background:#052e16;border:1px solid #16a34a;color:#86efac;}" +
                "  .actions{display:flex;gap:6px;}" +
                "  .stat-grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(180px,1fr));gap:16px;margin-bottom:24px;}" +
                "  .stat-card{background:#1a1a2e;border:1px solid #2d2d4e;border-radius:10px;padding:20px;text-align:center;}" +
                "  .stat-num{font-size:2em;font-weight:700;color:#c4b5fd;}" +
                "  .stat-label{color:#8b8baa;font-size:.85em;margin-top:4px;}" +
                "  @media(max-width:640px){.form-grid{grid-template-columns:1fr;}.form-group.full{grid-column:1;}}" +
                "</style></head><body>";
    }

    public static String nav(String active) {
        return "<nav>" +
                "<span class='brand'>🎮 GameApp</span>" +
                navLink("/demo/", "Inicio", active) +
                navLink("/demo/users", "Usuarios", active) +
                navLink("/demo/roles", "Roles", active) +
                navLink("/demo/games", "Juegos", active) +
                navLink("/demo/permissions", "Permisos", active) +
                navLink("/demo/role-permissions", "Roles-Permisos", active) +
                "</nav>";
    }

    private static String navLink(String href, String label, String active) {
        String cls = label.equals(active) ? " class='active'" : "";
        return "<a href='" + href + "'" + cls + ">" + label + "</a>";
    }

    public static String foot() {
        return "</div></body></html>";
    }

    public static String safe(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
