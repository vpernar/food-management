package rs.raf.domaci4;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static rs.raf.domaci4.IzborServlet.IDs;

@WebServlet(name = "PregledServlet", value = "/odabrana-jela")
public class PregledServlet extends HttpServlet {

    public static Map<String, List<Obrok>> izabraniObroci = new ConcurrentHashMap<>(mapInit());
    public static final String password = "raf123";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        if (req.getParameter("password") != null && req.getParameter("password").equals(password)) {

            out.println("<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <meta name=\"viewport\" content=\"initial-scale=1, maximum-scale=1\">\n" +
                    "    <link rel='stylesheet' href='webjars/bootstrap/3.2.0/css/bootstrap.min.css'>\n" +
                    "</head>\n" +
                    "<body>");
            out.println("<h1> Odabrana jela: </h1>");
            out.println("<form method=\"POST\" action=\"/odabrana-jela\" id=\"form\">\n" +
                    "  <input type=\"submit\" value=\"Ocisti\">\n" +
                    "</form>\n");

            AtomicInteger cnt = new AtomicInteger();

            for (Map.Entry<String, List<Obrok>> entry : izabraniObroci.entrySet()) {
                String dan = convertToDan(entry.getKey());

                out.println("<table class=\"table table-striped\"\n" +
                        "<thead>" +
                        "  <th>" + dan + "</th>" +
                        "  <tr>\n" +
                        "    <th scope=\"col\">#</th>\n" +
                        "    <th scope=\"col\">Jelo</th>\n" +
                        "    <th scope=\"col\">Kolicina</th>\n" +
                        "  </tr>\n" +
                        "</thead>\n" +
                        "<tbody>");

                entry.getValue().forEach(obrok -> {
                    cnt.getAndIncrement();
                    out.println("<tr>\n" +
                            "    <th scope=\"row\">" + cnt + "</th>\n" +
                            "    <td>" + obrok.getJelo() + "</td>\n" +
                            "    <td>" + obrok.getBroj() + "</td>\n" +
                            "  </tr>");
                });
                out.println("</tbody>\n" +
                        "</table>");
            }
            out.println("</body></html>");

        } else {
            resp.setStatus(401);
            out.println("<html><body>");
            out.println("<h1>Pristup nije dozvoljen</h1>");
            out.println("</body></html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        izabraniObroci = mapInit();
        IDs.clear();

        resp.sendRedirect("/odabrana-jela?password=" + password);
    }

    private String convertToDan(String dan) {
        char first = (char) (dan.charAt(0) - 32);
        StringBuilder sb = new StringBuilder(dan);
        sb.setCharAt(0, first);
        return sb.toString();
    }

    private static Map<String, List<Obrok>> mapInit() {
        return Map.of("ponedeljak", new ArrayList<>(),
                "utorak", new ArrayList<>(),
                "sreda", new ArrayList<>(),
                "cetvrtak", new ArrayList<>(),
                "petak", new ArrayList<>());
    }

}