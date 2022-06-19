package springbootboard.board.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springbootboard.board.config.auth.LoginUser;
import springbootboard.board.config.auth.dto.SessionUser;
import springbootboard.board.domain.board.Comment;
import springbootboard.board.domain.board.CommentService;
import springbootboard.board.domain.board.dto.CommentSaveRequestDto;

import javax.validation.Valid;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/comment")
@Controller
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}/new")
    public String createComment(@PathVariable("postId") Long postId,
                                @Valid @ModelAttribute("commentSaveRequestDto") CommentSaveRequestDto commentSaveRequestDto,
                                @RequestParam(value = "page", required = false) String page,
                                RedirectAttributes redirectAttributes,
                                @LoginUser SessionUser user) {

        commentService.register(commentSaveRequestDto, postId, user.getName());
        redirectAttributes.addAttribute("postId", postId);
        redirectAttributes.addAttribute("page", page);

        return "redirect:/post/{postId}";

    }

    @PostMapping("/{postId}/{commentId}/{ancestorId}/new")
    public String createChildComment(@PathVariable("postId") Long postId,
                                     @PathVariable("commentId") Long commentId,
                                     @PathVariable("ancestorId") Long ancestorId,
                                     @Valid @ModelAttribute("commentSaveRequestDto") CommentSaveRequestDto commentSaveRequestDto,
                                     @RequestParam(value = "page", required = false) String page,
                                     RedirectAttributes redirectAttributes,
                                     @LoginUser SessionUser user) {

        commentService.register(commentSaveRequestDto, postId, user.getName(), commentId, ancestorId);
        redirectAttributes.addAttribute("postId", postId);
        redirectAttributes.addAttribute("page", page);

        return "redirect:/post/{postId}";

    }

    @DeleteMapping("/{postId}/{commentId}")
    public String deleteComment(@PathVariable("postId") Long postId,
                                     @PathVariable("commentId") Long commentId,
                                     RedirectAttributes redirectAttributes,
                                     @LoginUser SessionUser user) {

        Comment comment = commentService.findByCommentId(commentId);


        if (!user.getName().equals(comment.getMember().getUsername())) {
            throw new AccessDeniedException("사용 권한이 없습니다.");
        }

        commentService.delete(commentId);

        redirectAttributes.addAttribute("postId", postId);

        return "redirect:/post/{postId}";

    }

}
