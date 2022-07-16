package springbootboard.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springbootboard.board.web.dto.AttachmentResponseDto;
import springbootboard.board.domain.board.repository.AttachmentQueryRepository;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AttachmentService {

    private final AttachmentQueryRepository attachmentQueryRepository;

    public List<AttachmentResponseDto> findAttachment(Long postId) {
        return attachmentQueryRepository.findAttachmentByPostId(postId);
    }
}
