package br.com.infox.epp.assinaturaeletronica;

import java.io.IOException;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

@WebServlet(urlPatterns = AssinaturaEletronicaServlet.SERVLET_BASE_URL + "*", name = "AssinaturaEletronicaServlet")
@MultipartConfig(maxFileSize = 2*1024*1024, maxRequestSize = 4*1024*1024, fileSizeThreshold = 4*1024*1024)
public class AssinaturaEletronicaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    public static final String SERVLET_BASE_URL = "/assinaturaEletronica/imagem/";

    @Inject
    private AssinaturaEletronicaSearch assinaturaEletronicaSearch;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UUID uuid = getUuidFromPath(req, resp);
        if (uuid == null) {
            return;
        }

        AssinaturaEletronicaDTO assinaturaEletronicaDTO = assinaturaEletronicaSearch.getAssinaturaEletronicaDTOByUUID(uuid, true);
        if (assinaturaEletronicaDTO == null) {
            resp.sendError(Status.NOT_FOUND.getStatusCode());
        } else {

            resp.setStatus(Status.OK.getStatusCode());
            resp.setContentLength(assinaturaEletronicaDTO.getImagem().length);
            resp.setContentType(assinaturaEletronicaDTO.getContentType());
            resp.addHeader("Cache-Control", "private, max-age=1800, no-transform");
            resp.getOutputStream().write(assinaturaEletronicaDTO.getImagem());
            resp.flushBuffer();
        }
    }

    private UUID getUuidFromPath(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            return UUID.fromString(req.getPathInfo().substring(1));
        } catch (IllegalArgumentException e) {
            resp.sendError(Status.BAD_REQUEST.getStatusCode(), "UUID inválido.");
            return null;
        }
    }

}
