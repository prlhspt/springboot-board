package springbootboard.board.domain.board;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import springbootboard.board.domain.BaseEntity;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Attachment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_id")
    private Long id;

    @Column(name = "store_file_path", nullable = false)
    private String storeFilePath;

    @Column(name = "store_file_name", nullable = false)
    private String storeFileName;

    @Column(name = "upload_file_name", nullable = false)
    private String uploadFileName;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    private FileType fileType;

    @Column(nullable = false)
    private boolean deleted;

    @Column(nullable = false)
    private Long size;

    @ManyToOne(fetch = LAZY)
    private Post post;

    @Builder
    public Attachment(Long id, String storeFilePath, String storeFileName, String uploadFileName, FileType fileType, Long size, Post post) {
        this.id = id;
        this.storeFilePath = storeFilePath;
        this.storeFileName = storeFileName;
        this.uploadFileName = uploadFileName;
        this.fileType = fileType;
        this.deleted = false;
        this.size = size;
        this.post = post;
    }

    public void delete() {
        this.deleted = true;
    }

    public void setPost(Post post) {
        this.post = post;
        post.getAttachments().add(this);
    }
}
