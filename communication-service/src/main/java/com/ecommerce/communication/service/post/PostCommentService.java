package com.ecommerce.communication.service.post;

import com.ecommerce.communication.dto.PageResponse;
import com.ecommerce.communication.dto.request.CommentPostCreateRequest;
import com.ecommerce.communication.dto.response.CommentPostCreateResponse;
import com.ecommerce.communication.dto.response.CommentPostGetResponse;
import com.ecommerce.communication.dto.response.CommentProductGetResponse;
import com.ecommerce.communication.entity.PostCommentEntity;
import com.ecommerce.communication.repository.PostCommentRepository;
import com.ecommerce.communication.repository.httpClient.ProfileClient;
import com.ecommerce.communication.service.DateTimeFormatter;
import com.ecommerce.communication.utility.GetInfo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.ProfileGetResponse;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PostCommentService {

    PostCommentRepository postCommentRepository;
    ModelMapper modelMapper;
    DateTimeFormatter dateTimeFormatter;
    ProfileClient profileClient;

    public CommentPostCreateResponse saveCommentPost(CommentPostCreateRequest commentCreateRequest) {
        String userId = GetInfo.getLoggedInUserName();

        PostCommentEntity postCommentEntity = modelMapper.map(commentCreateRequest, PostCommentEntity.class);
        postCommentEntity.setUserId(userId);

        PostCommentEntity postCommentSave = postCommentRepository.save(postCommentEntity);

        log.info("Comment successfully saved {}", postCommentSave.getComment());

        return CommentPostCreateResponse.builder()
                .postId(postCommentSave.getPostId())
                .comment(commentCreateRequest.getComment())
                .created(dateTimeFormatter.format(postCommentSave.getCreatedDate()))
                .parentId(commentCreateRequest.getParentId())
                .id(postCommentSave.getId())
                .build();
    }

    public PageResponse<CommentPostGetResponse> getCommentPost(int page, int size, String postId) {
        Sort sort = Sort.by("createdDate").descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        var listComments = postCommentRepository.findAllByPostId(postId,pageable);
        List<CommentPostGetResponse> postList = listComments.getContent().stream().map(comment -> {
            var postResponse = modelMapper.map(comment, CommentPostGetResponse.class);
            postResponse.setCreated(dateTimeFormatter.format(comment.getCreatedDate()));
            ProfileGetResponse profile = profileClient.getProfile(comment.getUserId()).getResult();
            postResponse.setUser(profile);
            return postResponse;
        }).toList();

        return PageResponse.<CommentPostGetResponse>builder()
                .currentPage(1)
                .pageSize(listComments.getSize())
                .totalPages(listComments.getTotalPages())
                .totalElements(listComments.getTotalElements())
                .data(postList)
                .build();
    }
}
