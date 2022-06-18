package springbootboard.board.domain.board.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import springbootboard.board.domain.board.Post;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class PostSaveRequestDto {

    @NotBlank
    private String title;
    @NotBlank
    private String content;

    private MultipartFile attachFile;
    private List<MultipartFile> imageFiles;

    public PostSaveRequestDto(String title, String content, MultipartFile attachFile, List<MultipartFile> imageFiles) {
        this.title = title;
        this.content = content;
        this.attachFile = attachFile;
        this.imageFiles = imageFiles;
    }

    @Builder
    public Post toEntity() {
        return Post.builder()
                .title(title)
                .content(content)
                .build();
    }
}
