package com.ecommerce.post.service;

import com.ecommerce.post.dto.PageResponse;
import com.ecommerce.post.dto.request.PostRequest;
import com.ecommerce.post.dto.response.PostResponse;
import com.ecommerce.post.entity.PostEntity;
import com.ecommerce.post.entity.TagEntity;
import com.ecommerce.post.repository.PostRepository;
import com.ecommerce.post.repository.TagRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostService {
    DateTimeFormatter dateTimeFormatter;
    PostRepository postRepository;
    TagService tagService;
    ModelMapper modelMapper;

    public PostResponse createPost(PostRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        PostEntity post = PostEntity.builder()
                .content(request.getContent())
                .title(request.getTitle())
                .affiliateLink(request.getAffiliateLink())
                .hashTags(request.getHashTags())
                .userId(authentication.getName())
                .createdDate(Instant.now())
                .modifiedDate(Instant.now())
                .build();

        post = postRepository.save(post);

        request.getHashTags().forEach(tagService::createTag);

        return modelMapper.map(post, PostResponse.class);
    }

    public PageResponse<PostResponse> getMyPosts(int page, int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<PostEntity> pageData = postRepository.findAllByUserId(userId, pageable);
        List<PostResponse> postList = mapToPostResponseList(pageData.getContent());

        return PageResponse.<PostResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(postList)
                .build();
    }


    public PageResponse<PostResponse> getPostsByTag(List<String> tags, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<PostEntity> pageData = postRepository.findAllByHashTags(tags, pageable);
        List<PostResponse> postList = mapToPostResponseList(pageData.getContent());

        return PageResponse.<PostResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(postList)
                .build();
    }


    private List<PostResponse> mapToPostResponseList(List<PostEntity> posts) {
        return posts.stream()
                .map(post -> {
                    PostResponse response = modelMapper.map(post, PostResponse.class);
                    response.setCreated(dateTimeFormatter.format(post.getCreatedDate()));
                    return response;
                })
                .toList();
    }

}