package com.example.dailychallenge.service.challenge;

import com.example.dailychallenge.dto.ChallengeSearchCondition;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeStatus;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.userChallenge.UserChallengeDuplicate;
import com.example.dailychallenge.exception.users.UserNotFound;
import com.example.dailychallenge.repository.UserChallengeRepository;
import com.example.dailychallenge.vo.challenge.ResponseChallenge;
import com.example.dailychallenge.vo.challenge.ResponseUserChallenge;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class UserChallengeService {

    private final UserChallengeRepository userChallengeRepository;

    public UserChallenge saveUserChallenge(Challenge challenge, User user) {
        checkDuplicate(challenge, user);

        UserChallenge userChallenge = UserChallenge.builder()
                .challengeStatus(ChallengeStatus.TRYING)
                .build();
        userChallenge.setUser(user);
        userChallenge.setChallenge(challenge);

        userChallengeRepository.save(userChallenge);

        return userChallenge;
    }

    private void checkDuplicate(Challenge challenge, User user) {
        userChallengeRepository.findByChallengeIdAndUserId(challenge.getId(), user.getId())
                .ifPresent(userChallenge -> {
                    throw new UserChallengeDuplicate();
                });
    }

    public List<ResponseUserChallenge> searchByChallengeId(Long challengeId) {
        return userChallengeRepository.searchUserChallengeByChallengeId(challengeId);
    }

    public UserChallenge findByChallengeIdAndUserId(Long challengeId, Long userId){
        return userChallengeRepository.findByChallengeIdAndUserId(challengeId, userId)
                .orElseThrow(EntityNotFoundException::new);
    }

    public Page<ResponseChallenge> searchAll(Pageable pageable) {

        return userChallengeRepository.searchAllChallenges(pageable);
    }

    public Page<ResponseChallenge> searchByCondition(ChallengeSearchCondition condition,
                                                     Pageable pageable) {

        return userChallengeRepository.searchChallengesByCondition(condition, pageable);
    }
    public void challengeParticipate(UserChallenge savedUserChallenge) {
        savedUserChallenge.challengeParticipate();
    }

    public UserChallenge succeedInChallenge(Long userId, Long challengeId) {
        UserChallenge userChallenge = findByChallengeIdAndUserId(challengeId, userId);
        userChallenge.challengeSuccess();
        return userChallenge;
    }
}
