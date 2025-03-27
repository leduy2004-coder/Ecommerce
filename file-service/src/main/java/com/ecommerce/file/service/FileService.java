package com.ecommerce.file.service;

import com.ecommerce.file.entity.PostImageEntity;
import com.ecommerce.file.entity.ProductImageEntity;
import com.ecommerce.file.entity.UserImageEntity;
import com.ecommerce.file.repository.PostRepository;
import com.ecommerce.file.repository.ProductRepository;
import com.ecommerce.file.repository.UserRepository;
import com.ecommerce.file.utility.ImageType;
import com.ecommerce.file.utility.ImageUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.CloudinaryResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileService {
    CloudinaryService cloudinaryService;
    UserRepository userRepository;
    PostRepository postRepository;
    ProductRepository productRepository;

    public CloudinaryResponse uploadFile(MultipartFile file, ImageType imageType, String id) throws IOException {
        ImageUtils.assertAllowed(file, ImageUtils.IMAGE_PATTERN);
        String fileName = ImageUtils.getFileName(file.getOriginalFilename());
        CloudinaryResponse response = cloudinaryService.uploadFile(file, fileName);
        if (imageType.equals(ImageType.POST)) {
            postRepository.save(PostImageEntity.builder()
                    .postId(id)
                    .name(fileName)
                    .url(response.getUrl())
                    .publicId(response.getPublicId())
                    .build());
        }else if(imageType.equals(ImageType.AVATAR)) {
            userRepository.save(UserImageEntity.builder()
                    .userId(id)
                    .name(fileName)
                    .url(response.getUrl())
                    .publicId(response.getPublicId())
                    .build());
        }else if(imageType.equals(ImageType.PRODUCT)) {
            productRepository.save(ProductImageEntity.builder()
                    .productId(id)
                    .name(fileName)
                    .url(response.getUrl())
                    .publicId(response.getPublicId())
                    .build());
        }
        return CloudinaryResponse.builder()
                .publicId(response.getPublicId())
                .url(response.getUrl()).build();
    }
}