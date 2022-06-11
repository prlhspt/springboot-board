package springbootboard.board.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springbootboard.board.config.auth.LoginUser;
import springbootboard.board.config.auth.dto.SessionUser;
import springbootboard.board.domain.board.CommentService;
import springbootboard.board.domain.board.PostService;
import springbootboard.board.domain.board.dto.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/post")
@Controller
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    @GetMapping
    public String home(Model model) {
        PostSearchCond cond = new PostSearchCond();
        List<PostListResponseDto> posts = postService.findPostList(cond);
        model.addAttribute("posts", posts);
        return "post/home";
    }

    @GetMapping("/new")
    public String createForm(@ModelAttribute("postSaveRequestDto") PostSaveRequestDto postSaveRequestDto) {
        return "post/createBoardForm";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("postSaveRequestDto") PostSaveRequestDto postSaveRequestDto
            , BindingResult bindingResult, @LoginUser SessionUser user) {
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
        List<CommentResponseDto> comment = commentService.findComment(postId);

        System.out.println("PostController.detailPost");
        detailPost.setComment(comment);

        model.addAttribute("detailPost", detailPost);

        return "post/detailPost";
    }

    @PostMapping("/comment/{postId}/new")
    public String createComment(@PathVariable("postId") Long postId,
                                @Valid @ModelAttribute("commentSaveRequestDto") CommentSaveRequestDto commentSaveRequestDto,
                                RedirectAttributes redirectAttributes,
                                @LoginUser SessionUser user) {
        commentService.register(commentSaveRequestDto, postId, user.getName());
        redirectAttributes.addAttribute("postId", postId);

        return "redirect:/post/{postId}";

    }

//    @PostMapping("/comment/{postId}/{commentId}/new")
//    public String createChildComment() {
//
//    }

}
