package com.example.Reddit.repository;

import com.example.Reddit.model.Post;
import com.example.Reddit.model.Subreddit;
import com.example.Reddit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllBySubreddit(Subreddit subreddit);
    List<Post> findByUser(User user);
}
