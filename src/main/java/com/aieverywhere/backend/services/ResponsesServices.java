package com.aieverywhere.backend.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.aieverywhere.backend.dto.RespResponseDTO;
import com.aieverywhere.backend.models.Responses;
import com.aieverywhere.backend.models.Users;
import com.aieverywhere.backend.repostories.RespRepo;
import com.aieverywhere.backend.repostories.RespSpecifications;
import com.aieverywhere.backend.repostories.UserRepo;

@Service
public class ResponsesServices {

    @Autowired
    private RespRepo respRepo;
    private UserRepo userRepo;

    public ResponsesServices(RespRepo respRepo, UserRepo userRepo) {
        this.respRepo = respRepo;
        this.userRepo = userRepo;
    }

    // Create a new response
    public Responses createResponse(Responses response) {
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdateAt(LocalDateTime.now());
        return respRepo.save(response);
    }

    // Read all responses
    public Map<String, Object> getPagedResponsesByPostId(Long postId, int page, int size) {
        Specification<Responses> resSpec = RespSpecifications.hasPostId(postId);
        Page<Responses> respPage = respRepo.findAll(resSpec, PageRequest.of(page, size));

        List<RespResponseDTO> respList = new ArrayList<>();
        for (Responses resp : respPage) {
            Long responseId = resp.getResponseId();
            Long userId = resp.getUserId();
            String content = resp.getContent();

            Users user = userRepo.getReferenceById(userId);
            String nickname = user.getNickName();
            String imgPath = user.getImagePath();

            Long likes = resp.getLikes();
            LocalDateTime createdAt = resp.getCreatedAt();
            LocalDateTime updateAt = resp.getUpdateAt();

            respList.add(new RespResponseDTO(responseId, postId, userId, content, nickname, imgPath, likes, createdAt,
                    updateAt));
        }

        Long totalItems = respRepo.count();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        Map<String, Object> respMap = new HashMap<>();
        respMap.put("respList", respList);
        respMap.put("currentPage", page);
        respMap.put("totalItems", totalItems);
        respMap.put("totalPages", totalPages);

        return respMap;
    }

    // get all responses by postId
    public List<Responses> getAllResponsesByPostId(Long postId) {
        Specification<Responses> spec = RespSpecifications.hasPostId(postId);
        return respRepo.findAll(spec);
    }

    // Read a single response by ID
    public Responses getResponseById(Long id) {
        return respRepo.getReferenceById(id);
    }

    // Update an existing response
    public Responses updateResponse(Long id, Responses responseDetails) {
        Responses response = respRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Response not found"));

        response.setContent(responseDetails.getContent());
        response.setPostId(responseDetails.getPostId());
        response.setUserId(responseDetails.getUserId());
        response.setUpdateAt(LocalDateTime.now());

        return respRepo.save(response);
    }

    // Delete a response
    public void deleteResponse(Long id) {
        respRepo.deleteById(id);
    }
}