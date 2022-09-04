package com.example.Reddit.repository;

import com.example.Reddit.model.Post;
import com.example.Reddit.model.User;
import com.example.Reddit.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);
}
