package com.example.dailychallenge.controller;

import com.example.dailychallenge.dto.CommentDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.comment.Comment;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.service.comment.CommentService;
import com.example.dailychallenge.service.challenge.ChallengeService;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.vo.ResponseComment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final UserService userService;
    private final ChallengeService challengeService;

    @PostMapping(value = "/{challengeId}/comment/new", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ResponseComment> createComment(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user,
            @PathVariable("challengeId") Long challengeId,
            @RequestPart @Valid CommentDto commentDto,
            @RequestPart(required = false) List<MultipartFile> commentDtoImg) {

        User findUser = userService.findByEmail(user.getUsername());
        Challenge challenge = challengeService.findById(challengeId);
        Comment comment = commentService.saveComment(commentDto, findUser, challenge,commentDtoImg);

        ResponseComment responseComment = ResponseComment.builder()
                .content(comment.getContent())
                .createdAt(comment.getCreated_at())
                .userId(comment.getUsers().getId())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseComment);
    }

    @PostMapping("/{challengeId}/comment/{commentId}")
    public void updateComment(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user,
            @PathVariable("commentId") Long commentId,
            @RequestPart @Valid CommentDto commentDto,
            @RequestPart(required = false) List<MultipartFile> commentDtoImg) {

        commentService.updateComment(commentId, commentDto, commentDtoImg);
    }

    @DeleteMapping("/{challengeId}/comment/{commentId}")
    public ResponseEntity<?> deleteComment(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user,
            @PathVariable("commentId") Long commentId){

        commentService.deleteComment(commentId);
        return ResponseEntity.status(HttpStatus.OK).body("댓글이 삭제되었습니다.");
    }

    @PostMapping("/{commentId}/like")
    public ResponseEntity<?> likeComment(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user,
            @PathVariable("commentId") Long commentId,
            @RequestParam Integer isLike) {

        Integer likeCount = commentService.likeUpdate(commentId, isLike);
        HashMap<String, Integer> responseMap = new HashMap<>();
        responseMap.put("isLike", likeCount);
        return ResponseEntity.status(HttpStatus.OK).body(responseMap);
    }

}
