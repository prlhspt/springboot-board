package springbootboard.board.web.dto;

import lombok.Data;
import springbootboard.board.domain.board.Attachment;
import springbootboard.board.domain.board.FileType;

@Data
public class AttachmentSaveRequestDto {
    private String storeFilePath;
    private String storeFileName;
    private String uploadFileName;
    private FileType fileType;
    private long fileSize;

    public AttachmentSaveRequestDto(String storeFilePath, String storeFileName, String uploadFileName, FileType fileType, long fileSize) {
        this.storeFilePath = storeFilePath;
        this.storeFileName = storeFileName;
        this.uploadFileName = uploadFileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }

    public Attachment toEntity() {
        return Attachment.builder()
                .storeFilePath(storeFilePath)
                .storeFileName(storeFileName)
                .uploadFileName(uploadFileName)
                .fileType(fileType)
                .size(fileSize)
                .build();
    }

}
