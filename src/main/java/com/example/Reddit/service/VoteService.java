package com.example.Reddit.service;

import com.example.Reddit.dto.VoteDto;
import com.example.Reddit.exception.PostNotFoundException;
import com.example.Reddit.exception.SpringRedditException;
import com.example.Reddit.model.Post;
import com.example.Reddit.model.Vote;
import com.example.Reddit.repository.PostRepository;
import com.example.Reddit.repository.VoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.Reddit.model.VoteType.UPVOTE;

@Service
@AllArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final AuthService authService;

    @Transactional
    public void vote(VoteDto voteDto) {
        Post post = postRepository.findById(voteDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException("Post Not Found With ID - " + voteDto.getPostId()));

        Optional<Vote> voteByPostAndUser = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post, authService.getCurrentUser());
        if(voteByPostAndUser.isPresent() && voteByPostAndUser.get().getVoteType().equals(voteDto.getVoteType())) {
            throw new SpringRedditException("You have already " + voteByPostAndUser.get().getVoteType() + "D for this post!");
        }
        if(UPVOTE.equals(voteDto.getVoteType())) {
            post.setVoteCount(post.getVoteCount() + 1);
        }
        else {
            post.setVoteCount(post.getVoteCount() - 1);
        }

        postRepository.save(post);
        voteRepository.save(mapToVote(voteDto, post));
    }

    public Vote mapToVote(VoteDto voteDto, Post post) {
        return Vote.builder()
                .voteType(voteDto.getVoteType())
                .post(post)
                .user(authService.getCurrentUser())
                .build();
    }
}
