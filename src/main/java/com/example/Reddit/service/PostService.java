package com.example.Reddit.service;

import com.example.Reddit.dto.PostRequest;
import com.example.Reddit.dto.PostResponse;
import com.example.Reddit.exception.PostNotFoundException;
import com.example.Reddit.exception.SubredditNotFoundException;
import com.example.Reddit.model.Post;
import com.example.Reddit.model.Subreddit;
import com.example.Reddit.model.User;
import com.example.Reddit.repository.CommentRepository;
import com.example.Reddit.repository.PostRepository;
import com.example.Reddit.repository.SubredditRepository;
import com.example.Reddit.repository.UserRepository;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final SubredditRepository subredditRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Transactional
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id.toString()));
        return mapToDto(post);
    }

    @Transactional
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(toList());
    }

    public void save(PostRequest postRequest) {
        Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName())
                .orElseThrow(() -> new SubredditNotFoundException(postRequest.getSubredditName()));
        postRepository.save(mapToPost(postRequest, subreddit, authService.getCurrentUser()));
    }

    @Transactional
    public List<PostResponse> getPostsBySubreddit(Long subredditId) {
        Subreddit subreddit = subredditRepository.findById(subredditId)
                .orElseThrow(() -> new SubredditNotFoundException(subredditId.toString()));
        List<Post> posts = postRepository.findAllBySubreddit(subreddit);
        return posts.stream().map(this::mapToDto).collect(toList());
    }

    public List<PostResponse> getPostsByUserName(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return postRepository.findByUser(user)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private Post mapToPost(PostRequest postRequest, Subreddit subreddit, User user) {
        return Post.builder()
                .postName(postRequest.getPostName())
                .url(postRequest.getUrl())
                .description(postRequest.getDescription())
                .subreddit(subreddit)
                .voteCount(0)
                .createdDate(Instant.now())
                .user(user)
                .build();
    }

    private PostResponse mapToDto(Post post) {
        return PostResponse.builder().id(post.getPostId())
                .postName(post.getPostName())
                .description(post.getDescription())
                .url(post.getUrl())
                .userName(post.getUser().getUsername())
                .subredditName(post.getSubreddit().getName())
                .voteCount(post.getVoteCount())
                .commentCount(commentCount(post))
                .duration(getDuration(post))
                .build();
    }

    Integer commentCount(Post post) {
        return commentRepository.findByPost(post).size();
    }

    String getDuration(Post post) {
        return TimeAgo.using(post.getCreatedDate().toEpochMilli());
    }



}
