package com.example.dailychallenge.repsoitory.comment;

import static com.example.dailychallenge.util.fixture.ChallengeFixture.createChallenge;
import static com.example.dailychallenge.util.fixture.TokenFixture.EMAIL;
import static com.example.dailychallenge.util.fixture.TokenFixture.PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.comment.Comment;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.repository.ChallengeRepository;
import com.example.dailychallenge.repository.CommentRepository;
import com.example.dailychallenge.repository.UserRepository;
import com.example.dailychallenge.util.RepositoryTest;
import com.example.dailychallenge.vo.ResponseChallengeComment;
import com.example.dailychallenge.vo.ResponseUserComment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

class CommentRepositoryCustomImplTest extends RepositoryTest {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private UserRepository userRepository;

    private User savedUser;
    private Challenge challenge;

    @BeforeEach
    void beforeEach() {
        savedUser = User.builder()
                .userName("홍길동")
                .email(EMAIL)
                .password(PASSWORD)
                .build();
        userRepository.save(savedUser);

        challenge = createChallenge();
        challenge.setUser(savedUser);
        challengeRepository.save(challenge);
    }

    @Test
    @DisplayName("챌린지에 있는 댓글들을 조회하는 테스트")
    void searchCommentsByChallengeIdTest() {
        User otherUser = User.builder()
                .userName("김철수")
                .email("a@a.com")
                .password(PASSWORD)
                .build();
        userRepository.save(otherUser);
        for (int i = 0; i < 5; i++) {
            Comment comment = Comment.builder()
                    .content("댓글 내용" + i)
                    .build();
            comment.saveCommentChallenge(challenge);
            comment.saveCommentUser(otherUser);
            commentRepository.save(comment);
        }
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("likes").descending());

        Page<ResponseChallengeComment> results = commentRepository.searchCommentsByChallengeId(
                challenge.getId(), pageRequest);

        assertAll(() -> {
            assertThat(results).extracting("id").isNotEmpty();
            assertThat(results).extracting("content")
                    .containsExactly("댓글 내용0", "댓글 내용1", "댓글 내용2", "댓글 내용3", "댓글 내용4");
            assertThat(results).extracting("likes").containsOnly(0);
            assertThat(results).extracting("createdAt").isNotEmpty();
            assertThat(results).extracting("commentImgUrls").isNotEmpty();
            assertThat(results).extracting("commentOwnerUser").extracting("userName")
                    .containsOnly(otherUser.getUserName());
            assertThat(results).extracting("commentOwnerUser").extracting("email")
                    .containsOnly(otherUser.getEmail());
            assertThat(results).extracting("commentOwnerUser").extracting("userId")
                    .containsOnly(otherUser.getId());
        });
    }

    @Test
    @DisplayName("챌린지에 있는 댓글들을 조회하는 테스트")
    void searchCommentsByChallengeIdTest2() {
        User otherUser = User.builder()
                .userName("김철수")
                .email("a@a.com")
                .password(PASSWORD)
                .build();
        userRepository.save(otherUser);
        for (int i = 0; i < 5; i++) {
            Comment comment = Comment.builder()
                    .content("댓글 내용" + i)
                    .build();
            comment.saveCommentChallenge(challenge);
            comment.saveCommentUser(otherUser);
            commentRepository.save(comment);
        }
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("likes").descending());
        Challenge otherChallenge = createChallenge();
        challengeRepository.save(otherChallenge);

        Page<ResponseChallengeComment> results = commentRepository.searchCommentsByChallengeId(
                otherChallenge.getId(), pageRequest);

        assertThat(results).extracting("content").isEmpty();
    }

    @Test
    @DisplayName("유저가 챌린지에 작성한 댓글들을 조회하는 테스트")
    void searchCommentsByUserIdTest() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            Comment comment = Comment.builder()
                    .content("댓글 내용" + i)
                    .build();
            comment.saveCommentChallenge(challenge);
            comment.saveCommentUser(savedUser);
            Thread.sleep(1);
            commentRepository.save(comment);
        }
        Challenge otherChallenge = createChallenge();
        challengeRepository.save(otherChallenge);
        for (int i = 5; i < 8; i++) {
            Comment comment = Comment.builder()
                    .content("다른 댓글 내용" + i)
                    .build();
            comment.saveCommentChallenge(otherChallenge);
            comment.saveCommentUser(savedUser);
            Thread.sleep(1);
            commentRepository.save(comment);
        }
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("time").descending());

        Page<ResponseUserComment> results = commentRepository.searchCommentsByUserId(savedUser.getId(),
                pageRequest);

        assertAll(() -> {
            assertThat(results).extracting("id").isNotEmpty();
            assertThat(results).extracting("content")
                    .containsExactly("다른 댓글 내용7", "다른 댓글 내용6", "다른 댓글 내용5", "댓글 내용4",
                            "댓글 내용3", "댓글 내용2", "댓글 내용1", "댓글 내용0");
            assertThat(results).extracting("likes").containsOnly(0);
            assertThat(results).extracting("createdAt").isNotEmpty();
            assertThat(results).extracting("commentImgUrls").isNotEmpty();
            assertThat(results).extracting("challengeId").containsOnly(challenge.getId(), otherChallenge.getId());
            assertThat(results).extracting("challengeTitle")
                    .containsOnly(challenge.getTitle(), otherChallenge.getTitle());
        });
    }
}