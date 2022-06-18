package springbootboard.board.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import springbootboard.board.domain.board.FileType;
import springbootboard.board.domain.board.dto.AttachmentSaveRequestDto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;


    public List<AttachmentSaveRequestDto> storeFiles(List<MultipartFile> multipartFiles, FileType fileType) throws IOException {
        List<AttachmentSaveRequestDto> storeFileResult = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                storeFileResult.add(storeFile(multipartFile, fileType));
            }
        }
        return storeFileResult;
    }
    public AttachmentSaveRequestDto storeFile(MultipartFile multipartFile, FileType fileType) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);
        long fileSize = multipartFile.getSize();

        File file = new File(getFullPath(storeFileName));
        if (!file.exists()) {
            file.mkdirs();
        }

        multipartFile.transferTo(file);
        return new AttachmentSaveRequestDto(fileDir, storeFileName, originalFilename, fileType, fileSize);
    }

    public String getFullPath(String filename) {
        return Paths.get("").toAbsolutePath() + "/src/main/resources/static/" + fileDir + "/" + filename;
    }

    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }


}
