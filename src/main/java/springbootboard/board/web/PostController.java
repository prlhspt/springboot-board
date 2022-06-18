package springbootboard.board.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;
import springbootboard.board.config.auth.LoginUser;
import springbootboard.board.config.auth.dto.SessionUser;
import springbootboard.board.domain.board.AttachmentService;
import springbootboard.board.domain.board.CommentService;
import springbootboard.board.domain.board.FileType;
import springbootboard.board.domain.board.PostService;
import springbootboard.board.domain.board.dto.*;
import springbootboard.board.util.FileStore;

import javax.validation.Valid;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/post")
@Controller
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final AttachmentService attachmentService;
    private final FileStore fileStore;

    @GetMapping
    public String readBoard(Model model, @PageableDefault Pageable pageable) {
        PostSearchCond cond = new PostSearchCond();
        Page<PostListResponseDto> posts = postService.findPostList(cond, pageable);
        model.addAttribute("posts", posts);
        return "post/home";
    }

    @GetMapping("/new")
    public String createForm(@ModelAttribute("postSaveRequestDto") PostSaveRequestDto postSaveRequestDto) {
        return "post/createBoardForm";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("postSaveRequestDto") PostSaveRequestDto postSaveRequestDto
            , BindingResult bindingResult, @LoginUser SessionUser user) throws IOException {

        List<MultipartFile> imageFiles = postSaveRequestDto.getImageFiles();
        for (MultipartFile imageFile : imageFiles) {
            if (!imageFile.isEmpty() && !imageFile.getContentType().contains("image")) {
                bindingResult.reject("onlyImageFile");
            }
        }

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "post/createBoardForm";
        }


        postService.post(postSaveRequestDto, user.getName());

        return "redirect:/post";
    }

    @GetMapping("/{postId}")
    public String detailPost(@PathVariable("postId") Long postId, Model model,
                             @ModelAttribute("commentSaveRequestDto") CommentSaveRequestDto commentSaveRequestDto) {

        postService.addViewCount(postId);

        PostResponseDto detailPost = postService.findDetailPost(postId);
        List<CommentResponseDto> comments = commentService.findComment(postId);
        List<AttachmentResponseDto> attachments = attachmentService.findAttachment(postId);

        for (CommentResponseDto comment : comments) {
            detailPost.getComments().add(comment);
        }

        for (AttachmentResponseDto attachment : attachments) {
            if (attachment.getFileType() == FileType.FILE) {
                detailPost.setAttachFile(attachment);
            } else {
                detailPost.getImageFiles().add(attachment);
            }
        }

        model.addAttribute("detailPost", detailPost);

        return "post/detailPost";
    }

    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(filename));
    }

    @GetMapping("/attach/{filename}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable String filename) throws MalformedURLException {

        UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(filename));

        log.info("filename={}", filename);

        String encodedUploadFileName = UriUtils.encode(filename, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }

}
