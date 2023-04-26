package rs.raf.domaci4;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static rs.raf.domaci4.PregledServlet.izabraniObroci;


@WebServlet(name = "IzborServlet", value = "/izaberi-jelo")
public class IzborServlet extends HttpServlet {

    private final TypeReference<ConcurrentHashMap<String, String>> typeRef = new TypeReference<>() {
    };
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<String> dani = List.of("ponedeljak", "utorak", "sreda", "cetvrtak", "petak");
    private static Map<String, List<String>> fileObroci = new ConcurrentHashMap<>(mapInit());
    public static List<String> IDs = new CopyOnWriteArrayList<>();

    public void init() {
        loadFromFile();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        String id = req.getSession().getId();

        //Ako nije izabrao
        if (!IDs.contains(id)) {

            out.println("<html><body>");
            out.println("<h1>" + "IT Restoran" + "</h1>");
            out.println("<p><b>Odaberite vas rucak:</b></p>");

            for (String dan : dani) {

                String rdan = convertToDan(dan);

                out.println("<label for=\"" + dan + "\">" + rdan + ":</label><br>");
                out.println("<select name=\"" + dan + "\" id=\"" + dan + "\" form=\"form\"><br>");

                for (String obrok : fileObroci.get(dan)) {
                    out.println("<option value=\"" + obrok + "\">" + obrok + "</option><br>");
                }

                out.println("</select><br>");
            }
            out.println("<br><form method=\"POST\" action=\"/izaberi-jelo\" id=\"form\">\n" +
                    "  <input type=\"submit\">\n" +
                    "</form>\n");
            out.println("</body></html>");

            //ako ima cookie
        } else {
            out.println("<html><body>");
            out.println("<h1> IT Restoran </h1>");
            out.println("<h2>Vas izbor:</h2>");

            Map<String, String> izbor = objectMapper.readValue(readCookie(req).get(), typeRef);

            for (Map.Entry<String, String> entry : izbor.entrySet()) {
                out.println("<p>" + entry.getKey() + ": " + entry.getValue() + "</p>");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> korisnikovIzbor = new HashMap();
        String id = req.getSession().getId();

        for (String dan : dani) {
            String rDan = convertToDan(dan);
            Obrok obrok = new Obrok(req.getParameter(dan));
            korisnikovIzbor.put(rDan, obrok.getJelo());

            Optional<Obrok> optionalObrok = izabraniObroci.get(dan).stream()
                    .filter(o -> o.equals(obrok))
                    .findFirst();

            if (optionalObrok.isPresent()) {
                Obrok existingObrok = optionalObrok.get();
                existingObrok.setBroj(existingObrok.getBroj() + 1);
            } else {
                izabraniObroci.get(dan).add(obrok);
            }
        }

        String jelaJson = objectMapper.writeValueAsString(korisnikovIzbor);
        byte[] encodedBytes = Base64.getEncoder().encode(jelaJson.getBytes());
        String encodedJson = new String(encodedBytes);

        IDs.add(id);
        resp.addCookie(new Cookie("jela", encodedJson));
        resp.sendRedirect("/conformation.html");
    }

    private String convertToDan(String dan) {
        char first = (char) (dan.charAt(0) - 32);
        StringBuilder sb = new StringBuilder(dan);
        sb.setCharAt(0, first);
        return sb.toString();
    }

    private Optional<String> readCookie(HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(c -> "jela".equals(c.getName()))
                .map(cookie -> {
                    String encodedJson = cookie.getValue();
                    byte[] decodedBytes = Base64.getDecoder().decode(encodedJson.getBytes());
                    return new String(decodedBytes);
                })
                .findAny();
    }

    private void loadFromFile() {
        Scanner scanner = null;
        String path = "/Users/vpernar/Desktop/WP/domaci4/src/main/java/";

        try {
            for (String dan : dani) {
                scanner = new Scanner(new File(path + dan + ".txt"));
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    fileObroci.get(dan).add(line);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found");
            throw new RuntimeException(e);
        }

        System.out.println("--------------------------Obroci------------------------");
        for (Map.Entry<String, List<String>> entry : fileObroci.entrySet())
            System.out.println(entry.getKey() + " " + entry.getValue());
        System.out.println("--------------------------------------------------------");
    }

    private static Map<String, List<String>> mapInit() {
        return Map.of("ponedeljak", new ArrayList<>(),
                "utorak", new ArrayList<>(),
                "sreda", new ArrayList<>(),
                "cetvrtak", new ArrayList<>(),
                "petak", new ArrayList<>());
    }

    public void destroy() {
    }
}