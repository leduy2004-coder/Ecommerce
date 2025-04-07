package com.ecommerce.file.service;

import com.ecommerce.file.entity.PostImageEntity;
import com.ecommerce.file.entity.ProductImageEntity;
import com.ecommerce.file.entity.UserImageEntity;
import com.ecommerce.file.repository.PostRepository;
import com.ecommerce.file.repository.ProductRepository;
import com.ecommerce.file.repository.UserRepository;
import com.ecommerce.file.utility.AvatarType;
import org.example.ImageType;
import com.ecommerce.file.utility.ImageUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.CloudinaryResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileService {
    CloudinaryService cloudinaryService;
    UserRepository userRepository;
    PostRepository postRepository;
    ProductRepository productRepository;

    public CloudinaryResponse uploadFile(MultipartFile file, ImageType imageType, String id) {
        ImageUtils.assertAllowed(file, ImageUtils.IMAGE_PATTERN);
        String fileName = ImageUtils.getFileName(file.getOriginalFilename());
        CloudinaryResponse response = cloudinaryService.uploadFile(file, fileName);
        String imgId = "";
        if (imageType.equals(ImageType.POST)) {
            imgId = postRepository.save(PostImageEntity.builder()
                    .postId(id)
                    .name(fileName)
                    .url(response.getUrl())
                    .publicId(response.getPublicId())
                    .build()).getId();
        }else if(imageType.equals(ImageType.AVATAR)) {
            imgId =userRepository.save(UserImageEntity.builder()
                    .userId(id)
                    .name(fileName)
                    .avatarType(AvatarType.LOCAL)
                    .url(response.getUrl())
                    .publicId(response.getPublicId())
                    .build()).getId();
        }else if(imageType.equals(ImageType.PRODUCT)) {
            imgId =productRepository.save(ProductImageEntity.builder()
                    .productId(id)
                    .name(fileName)
                    .url(response.getUrl())
                    .publicId(response.getPublicId())
                    .build()).getId();
        }
        return CloudinaryResponse.builder()
                .id(imgId)
                .url(response.getUrl()).build();
    }
    public CloudinaryResponse createAvatar(String id, String url) {
            userRepository.save(UserImageEntity.builder()
                    .userId(id)
                    .avatarType(AvatarType.OAUTH2)
                    .url(url)
                    .build());
        return CloudinaryResponse.builder()
                .url(url).build();
    }

    public List<CloudinaryResponse> getAllById(String id, ImageType imageType) {
        if (imageType.equals(ImageType.POST)) {
            var list = postRepository.findAllByPostId(id);

            return list.stream()
                    .map(product -> CloudinaryResponse.builder()
                            .id(product.getId())
                            .url(product.getUrl())
                            .build())
                    .collect(Collectors.toList());
        }
        if (imageType.equals(ImageType.AVATAR)) {
            var list = userRepository.findAllByUserId(id);

            return list.stream()
                    .map(product -> CloudinaryResponse.builder()
                            .id(product.getId())
                            .url(product.getUrl())
                            .build())
                    .collect(Collectors.toList());
        }
        if (imageType.equals(ImageType.PRODUCT)) {
            var list = productRepository.findAllByProductId(id);
            if (list.isEmpty()) {
                return null;
            } else {
                return list.stream()
                        .map(product -> CloudinaryResponse.builder()
                                .id(product.getId())
                                .url(product.getUrl())
                                .build())
                        .collect(Collectors.toList());
            }

        }
        return null;
    }

    public Boolean deleteById(String id, ImageType imageType) {
        if (imageType.equals(ImageType.POST)) {
            var list = postRepository.findAllByPostId(id);
            if (list.isEmpty()) {
                return false;
            }
            // Xóa từng hình ảnh và xóa khỏi Cloudinary
            list.forEach(post -> {
                if (post.getPublicId() != null) {
                    cloudinaryService.deleteFile(post.getPublicId(), "image");
                }
                postRepository.deleteById(post.getId());
            });
            return true;
        }
        if (imageType.equals(ImageType.AVATAR)) {
            var list = userRepository.findAllByUserId(id);

            if (list.isEmpty()) {
                return false;
            }
            list.forEach(user -> {
                if (user.getPublicId() != null) {
                    cloudinaryService.deleteFile(user.getPublicId(), "image");
                }
                userRepository.deleteById(user.getId());
            });
            return true;
        }
        if (imageType.equals(ImageType.PRODUCT)) {
            var list = productRepository.findAllByProductId(id);
            if (list.isEmpty()) {
                return false;
            }
            list.forEach(product -> {
                // Xóa ảnh khỏi Cloudinary
                if (product.getPublicId() != null) {
                    cloudinaryService.deleteFile(product.getPublicId(), "image");
                }
                productRepository.deleteById(product.getId());
            });

            return true;
        }
        return false;
    }



}