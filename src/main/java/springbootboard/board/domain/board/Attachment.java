package springbootboard.board.domain.board;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import springbootboard.board.domain.BaseEntity;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Attachment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_id")
    private Long id;

    @Column(name = "store_path", nullable = false)
    private String storePath;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "original_name", nullable = false)
    private String originalName;

    private String extension;

    @Column(nullable = false)
    private Long size;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @Builder
    public Attachment(String storePath, String storeName, String originalName, String extension, Long size, Post post) {
        this.storePath = storePath;
        this.storeName = storeName;
        this.originalName = originalName;
        this.extension = extension;
        this.size = size;
        this.post = post;
    }
}
