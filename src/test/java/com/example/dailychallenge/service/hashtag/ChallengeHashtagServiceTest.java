package com.example.dailychallenge.service.hashtag;

import com.example.dailychallenge.dto.ChallengeDto;
import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.entity.challenge.*;
import com.example.dailychallenge.entity.hashtag.ChallengeHashtag;
import com.example.dailychallenge.entity.hashtag.Hashtag;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.service.challenge.ChallengeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class ChallengeHashtagServiceTest {

    @Autowired
    private ChallengeService challengeService;
    @Value("${userImgLocation}")
    private String challengeImgLocation;
    @Autowired
    private HashtagService hashtagService;
    @Autowired
    private ChallengeHashtagService challengeHashtagService;

    MultipartFile createMultipartFiles() throws Exception {
        String path = challengeImgLocation +"/";
        String imageName = "challengeImage.jpg";
        MockMultipartFile multipartFile = new MockMultipartFile(path, imageName,
                "image/jpg", new byte[]{1, 2, 3, 4});
        return multipartFile;
    }

    public Challenge createChallenge() throws Exception {
        ChallengeDto challengeDto = ChallengeDto.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .challengeCategory(ChallengeCategory.STUDY.getDescription())
                .challengeLocation(ChallengeLocation.INDOOR.getDescription())
                .challengeDuration(ChallengeDuration.WITHIN_TEN_MINUTES.getDescription())
                .build();
        MultipartFile challengeImg = createMultipartFiles();

        return challengeService.saveChallenge(challengeDto, challengeImg);
    }

    public List<Hashtag> createHashtag() {
        List<String> hashtagDto = List.of("tag1");
        return hashtagService.saveHashtag(hashtagDto);
    }

    @Test
    @DisplayName("챌린지 해시태그 생성 테스트 - 연관관계 테스트")
    void createUserChallenge() throws Exception {
        Challenge challenge = createChallenge();
        List<Hashtag> hashtag = createHashtag();

        List<ChallengeHashtag> challengeHashtags = challengeHashtagService.saveChallengeHashtag(challenge, hashtag);

        assertEquals(challengeHashtags.get(0).getChallenge(),challenge);
        assertEquals(challengeHashtags.get(0).getHashtag(),hashtag.get(0));
    }
}