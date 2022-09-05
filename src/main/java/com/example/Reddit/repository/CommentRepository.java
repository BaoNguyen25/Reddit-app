package com.example.Reddit.repository;

import com.example.Reddit.model.Comment;
import com.example.Reddit.model.Post;
import com.example.Reddit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);
    List<Comment> findAllByUser(User user);
}
