package com.example.Reddit.dto;

import com.example.Reddit.model.VoteType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteDto {
    VoteType voteType;
    Long postId;
}
