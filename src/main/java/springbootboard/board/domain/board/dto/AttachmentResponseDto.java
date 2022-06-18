package springbootboard.board.domain.board.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import springbootboard.board.domain.board.FileType;

@Data
public class AttachmentResponseDto {

    private FileType fileType;
    private String storeFileName;
    private String storeFilePath;
    private String uploadFileName;

    @QueryProjection
    public AttachmentResponseDto(FileType fileType, String storeFileName, String storeFilePath, String uploadFileName) {
        this.fileType = fileType;
        this.storeFileName = storeFileName;
        this.storeFilePath = storeFilePath;
        this.uploadFileName = uploadFileName;
    }
}
