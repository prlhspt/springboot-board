package springbootboard.board.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;
import springbootboard.board.config.auth.LoginUser;
import springbootboard.board.config.auth.dto.SessionUser;
import springbootboard.board.domain.board.*;
import springbootboard.board.service.AttachmentService;
import springbootboard.board.service.CommentService;
import springbootboard.board.service.PostService;
import springbootboard.board.util.FileStore;
import springbootboard.board.web.dto.*;

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
    public String readBoard(Model model, @PageableDefault Pageable pageable,
                            @ModelAttribute("searchRequestDto") SearchRequestDto searchRequestDto,
                            @RequestParam(value = "page", required = false) String page,
                            @LoginUser SessionUser user) {

        PostSearchCond cond = new PostSearchCond();

        if (searchRequestDto.getSearchType() != null && searchRequestDto.getKeyword() != null) {
            if (searchRequestDto.getSearchType().equals("title")) {
                cond.setTitle(searchRequestDto.getKeyword());
            }
            if (searchRequestDto.getSearchType().equals("content")) {
                cond.setContent(searchRequestDto.getKeyword());
            }
            if (searchRequestDto.getSearchType().equals("writer")) {
                cond.setWriter(searchRequestDto.getKeyword());
            }
        }

        Page<PostListResponseDto> posts = postService.findPostList(cond, pageable);
        model.addAttribute("posts", posts);
        model.addAttribute("page", page);
        model.addAttribute("searchRequestDto", searchRequestDto);

        if (user != null) {
            model.addAttribute("username", user.getName());
        }

        return "post/home";
    }

    @GetMapping("/{postId}")
    public String detailPost(@PathVariable Long postId, Model model,
                             @ModelAttribute("commentSaveRequestDto") CommentSaveRequestDto commentSaveRequestDto,
                             @PageableDefault Pageable pageable,
                             @LoginUser SessionUser user) {

        postService.addViewCount(postId);

        PostResponseDto detailPost = postService.findDetailPost(postId);
        Page<CommentResponseDto> comments = commentService.findComment(postId, pageable);
        List<AttachmentResponseDto> attachments = attachmentService.findAttachment(postId);

        detailPost.setComments(comments);

        for (AttachmentResponseDto attachment : attachments) {
            if (attachment.getFileType() == FileType.FILE) {
                detailPost.setAttachFile(attachment);
            } else {
                detailPost.getImageFiles().add(attachment);
            }
        }

        model.addAttribute("detailPost", detailPost);

        if (user != null) {
            model.addAttribute("username", user.getName());
        }

        return "post/detailPost";
    }

    @GetMapping("/new")
    public String createPostForm(@ModelAttribute("postSaveRequestDto") PostSaveRequestDto postSaveRequestDto) {
        return "post/createPostForm";
    }

    @PostMapping("/new")
    public String createPost(@Valid @ModelAttribute("postSaveRequestDto") PostSaveRequestDto postSaveRequestDto,
            BindingResult bindingResult, @LoginUser SessionUser user, RedirectAttributes redirectAttributes) throws IOException {

        List<MultipartFile> imageFiles = postSaveRequestDto.getImageFiles();
        for (MultipartFile imageFile : imageFiles) {
            if (!imageFile.isEmpty() && !imageFile.getContentType().contains("image")) {
                bindingResult.reject("OnlyImageFile");
            }
        }

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "post/createPostForm";
        }

        Long postId = postService.post(postSaveRequestDto, user.getName());
        redirectAttributes.addAttribute("postId", postId);

        return "redirect:/post/{postId}";
    }

    @GetMapping("/{postId}/edit")
    public String updatePostForm(@PathVariable Long postId, Model model) {

        PostSaveRequestDto postSaveRequestDto = postService.findOneDto(postId);
        model.addAttribute("postSaveRequestDto", postSaveRequestDto);

        return "post/updatePostForm";
    }

    @PostMapping("/{postId}/edit")
    public String updatePost(@PathVariable Long postId,
                             @Valid @ModelAttribute("postSaveRequestDto") PostSaveRequestDto postSaveRequestDto,
                             BindingResult bindingResult, RedirectAttributes redirectAttributes,
                             @LoginUser SessionUser user) throws IOException {

        Post post = postService.findOne(postId);

        if (!user.getName().equals(post.getMember().getUsername())) {
            throw new AccessDeniedException("사용 권한이 없습니다.");
        }

        List<MultipartFile> imageFiles = postSaveRequestDto.getImageFiles();
        for (MultipartFile imageFile : imageFiles) {
            if (!imageFile.isEmpty() && !imageFile.getContentType().contains("image")) {
                bindingResult.reject("OnlyImageFile");
            }
        }

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "post/updatePostForm";
        }

        postService.updatePost(postId, postSaveRequestDto);

        redirectAttributes.addAttribute("postId", postId);

        return "redirect:/post/{postId}";
    }

    @DeleteMapping("/{postId}")
    public String deletePost(@PathVariable("postId") Long postId,
                                @LoginUser SessionUser user) {

        Post post = postService.findOne(postId);

        if (!user.getName().equals(post.getMember().getUsername())) {
            throw new AccessDeniedException("사용 권한이 없습니다.");
        }

        postService.delete(postId);
        return "redirect:/post";

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
