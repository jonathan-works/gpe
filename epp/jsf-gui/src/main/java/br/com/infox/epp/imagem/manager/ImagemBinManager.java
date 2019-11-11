package br.com.infox.epp.imagem.manager;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioPerfil;
import br.com.infox.epp.imagem.dao.ImagemBinDAO;
import br.com.infox.epp.imagem.entity.ImagemBin;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.path.PathResolver;

@Name(ImagemBinManager.NAME)
@AutoCreate
public class ImagemBinManager extends Manager<ImagemBinDAO, ImagemBin> {
    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(ImagemBinManager.class);
    public static final String NAME = "imagemBinManager";

    @In
    private PathResolver pathResolver;

    public void persistImageBin(ImagemBin imagemBin) throws DAOException {
        getDao().persistImageBin(imagemBin);
    }

    private String[] getImagesDir(final String path, final UsuarioPerfil usuarioPerfil) {
        if (usuarioPerfil != null && usuarioPerfil.getPerfilTemplate().getLocalizacao() != null) {
            String idEstrutura = "";
            if (usuarioPerfil.getLocalizacao() != null) {
                idEstrutura = String.valueOf(usuarioPerfil.getLocalizacao().getIdLocalizacao());
            }
            return new String[] {
                path,
                MessageFormat.format("{0}/l{1}e{2}", path, usuarioPerfil.getPerfilTemplate().getLocalizacao().getIdLocalizacao(), idEstrutura).replace("//", "/") };
        }
        return new String[] { path };
    }

    public String[] getImagesDir(String imagesRelativePath) {
        return getImagesDir(pathResolver.getRealPath(imagesRelativePath), Authenticator.getUsuarioPerfilAtual());
    }

    public String[] getDBPath(String imagesRelativePath) {
        return getImagesDir(imagesRelativePath, Authenticator.getUsuarioPerfilAtual());
    }

    public String[] getImagesPath(String imagesRelativePath) {
        return getImagesDir(pathResolver.getContextPath(imagesRelativePath), Authenticator.getUsuarioPerfilAtual());
    }

    private void createDir(String imagesDir) {
        File dir = new File(imagesDir);
        if (!dir.exists()) {
            boolean result = dir.mkdirs();
            LOG.warn(MessageFormat.format("Diretorio {0} criado? {1}", dir, result));
        }
    }

    private String getNewFileConflict(String nome) {
        int localPonto = nome.lastIndexOf('.');
        String ext = nome.substring(localPonto);
        String pre = nome.substring(0, localPonto);
        return pre + "_" + ext;
    }

    public void saveFile(ImagemBin imagem, String imagensRelativePath) throws IOException {
        String[] imagesDir = getImagesDir(imagensRelativePath);
        File directory = new File(imagesDir[imagesDir.length - 1]);
        directory.mkdirs();
        saveFile(imagem.getImagem(), new File(directory, imagem.getNomeArquivo()));
    }

    public void saveFile(byte[] bytesOrigem, File fileDestino) throws IOException {
        if (fileDestino.exists()) {
            if (fileDestino.length() != bytesOrigem.length) {
                fileDestino = new File(getNewFileConflict(fileDestino.getAbsolutePath()));
            } else {
                throw new IOException(MessageFormat.format("Arquivo já existente: {0}{1}", fileDestino.getAbsolutePath(), fileDestino.getName()));
            }
        }
        getDao().saveFile(bytesOrigem, fileDestino);
        LOG.info(MessageFormat.format("Arquivo instanciado com sucesso: {0}{1}", fileDestino.getAbsolutePath(), fileDestino.getName()));
    }

    public List<String> getImages(String imagensRelativePath) {
        String[] imagensDir = getImagesDir(imagensRelativePath);
        List<String> files = new ArrayList<String>();
        for (int i = 0; i < imagensDir.length; i++) {
            createDir(imagensDir[i]);

            File dir = new File(imagensDir[i]);
            if (!dir.canRead()) {
                return null;
            }
            String[] filesImg = dir.list(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    return (name.toLowerCase().endsWith(".jpg") 
                            || name.toLowerCase().endsWith(".png")
                            || name.toLowerCase().endsWith(".gif")
                            || name.toLowerCase().endsWith(".jpeg") );
                }

            });
            String[] imagensPath = getImagesPath(imagensRelativePath);
            for (int j = 0; j < filesImg.length; j++) {
                filesImg[j] = imagensPath[i] + "/" + filesImg[j];
                files.add(filesImg[j]);
            }
        }

        return files;
    }

    public void createImageFiles() {
        final List<ImagemBin> list = getDao().getTodasAsImagens();

        for (ImagemBin imagemBin : list) {
            String imagemDir = pathResolver.getRealPath(imagemBin.getFilePath());
            createDir(imagemDir);
            File fileDestino = new File(imagemDir, imagemBin.getNomeArquivo());

            if (fileDestino.exists()) {
                continue;
            }
            try {
                saveFile(imagemBin.getImagem(), fileDestino);
            } catch (IOException e) {
                LOG.warn(MessageFormat.format("Erro ao adicionar arquivo: {0} {1}", fileDestino.getAbsolutePath(), fileDestino.getName()), e);
            }

        }
    }

}
