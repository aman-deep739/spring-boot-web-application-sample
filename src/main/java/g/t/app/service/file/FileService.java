package g.t.app.service.file;

import g.t.app.config.AppProperties;
import g.t.app.domain.ReceivedFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    private final Path rootLocation;

    public FileService(AppProperties appProperties) {
        this.rootLocation = Paths.get(appProperties.getFileStorage().getUploadFolder());
    }

    public String store(ReceivedFile.FileGroup fileGroup, @NotNull MultipartFile file) {

        try {

            String fileIdentifier = getCleanedFileName(file.getOriginalFilename());

            Path targetPath = getStoredFilePath(fileGroup, fileIdentifier);

            file.transferTo(targetPath);

            return fileIdentifier;

        } catch (Exception e) {
            throw new StorageException("Failed to store file " + file, e);
        }
    }

    public Resource loadAsResource(ReceivedFile.FileGroup fileGroup, String fileIdentifier) {
        try {
            Path targetPath = getStoredFilePath(fileGroup, fileIdentifier);

            Resource resource = new UrlResource(targetPath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new Exception("Could not read file: " + targetPath);

            }
        } catch (Exception e) {
            throw new RetrievalException("Could not read file: " + fileIdentifier + " , group " + fileGroup, e);
        }
    }

    private String getCleanedFileName(String originalName) throws Exception {
        if (originalName == null || originalName.isEmpty()) {
            throw new Exception("Failed to store empty file " + originalName);
        }

        if (originalName.contains("..")) {
            // This is a security check
            throw new Exception("Cannot store file with relative path outside current directory " + originalName);
        }

        return UUID.randomUUID().toString();
    }

    private String getSubFolder(ReceivedFile.FileGroup fileGroup) throws Exception {
        if (fileGroup == ReceivedFile.FileGroup.NOTE_ATTACHMENT) {
            return "attachments";
        }

        throw new Exception("File group subfolder " + fileGroup + " is not implemented");
    }

    private Path getStoredFilePath(ReceivedFile.FileGroup fileGroup, String fileIdentifier) throws Exception {
        return rootLocation.resolveSibling(getSubFolder(fileGroup)).resolve(fileIdentifier);
    }
}
