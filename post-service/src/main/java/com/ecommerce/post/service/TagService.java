package com.ecommerce.post.service;

import com.ecommerce.event.dto.TagEvent;
import com.ecommerce.post.entity.TagEntity;
import com.ecommerce.post.repository.TagRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.boot.Banner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TagService {
    TagRepository tagRepository;
    final KafkaTemplate<String, TagEvent> tagKafkaTemplate;
    ModelMapper modelMapper;

    public void createTag(String tagName){
        TagEntity tagSave;
        if (!tagRepository.existsByName(tagName)) {
            tagSave= tagRepository.save(TagEntity.builder()
                    .name(tagName)
                    .count(1)
                    .build());
        } else {
            // Hoặc cập nhật count nếu đã tồn tại (nếu bạn có nhu cầu thống kê)
            TagEntity tag = tagRepository.findByName(tagName);
            tag.setCount(tag.getCount() + 1);
            tagSave = tagRepository.save(tag);
        }
        tagKafkaTemplate.send("tag-sync", modelMapper.map(tagSave, TagEvent.class));
    }

    public List<String> getTopTag(){
        List<TagEntity> popularTags = tagRepository.findTop10ByOrderByCountDesc();

        List<String> topTags = new ArrayList<>();
        for (TagEntity tag : popularTags) {
            topTags.add(tag.getName());
        }
        return topTags;
    }
}