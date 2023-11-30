package com.pjh.newsfeedtest.file.controller;

import com.pjh.newsfeedtest.file.dto.UploadFileDTO;
import com.pjh.newsfeedtest.file.dto.UploadResultDTO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@Log4j2
@RequestMapping("/api/files")
public class UploadRestController {
    @Value("${com.sparta.newfeed.upload.path}")
    private String uploadPath;

    @Operation(summary = "이미지 파일 업로드", description = "Post요청을 통해 이미지를 업로드할 수 있다.")
    @PostMapping(value="/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<UploadResultDTO> upload(UploadFileDTO uploadFileDTO) {
        log.info(uploadFileDTO);

        if(uploadFileDTO.getFiles() != null) {
            final List<UploadResultDTO> list = new ArrayList<>();
            uploadFileDTO.getFiles().forEach(multipartFile ->  {
                String originalName = multipartFile.getOriginalFilename();
                log.info(multipartFile.getOriginalFilename());
                String uuid = UUID.randomUUID().toString();
                Path savePath = Paths.get(uploadPath, uuid + "_"+ originalName);
                boolean image = false;

                try {
                    multipartFile.transferTo(savePath);
                    if(Files.probeContentType(savePath).startsWith("image")) {
                        image = true;
                        File thumbFile = new File(uploadPath, "s_" + uuid + "_" + originalName);
                        Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200, 200);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                list.add(UploadResultDTO.builder()
                                .uuid(uuid)
                                .fileName(originalName)
                                .img(image).build());
            });
            return list;
        }

        return null;
    }
    @Operation(summary = "이미지 파일 조회", description = "GET요청을 통해 이미지를 조회할 수 있다.")
    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> viewFileGet(@PathVariable String fileName) {
        Resource resource = new FileSystemResource(uploadPath+File.separator + fileName);

        String resourceName = resource.getFilename();
        HttpHeaders httpHeaders = new HttpHeaders();

        try {
            httpHeaders.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        } catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().headers(httpHeaders).body(resource);
    }
    @Operation(summary = "이미지 파일 삭제", description = "DELETE요청을 통해 이미지를 삭제할 수 있다.")
    @DeleteMapping("/{fileName}")
    public Map<String, Boolean> removeFile(@PathVariable String fileName) {
        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
        String resourceName = resource.getFilename();

        Map<String, Boolean> resultMap = new HashMap<>();
        boolean removed = false;
        try {
            String contentType = Files.probeContentType(resource.getFile().toPath());
            removed  = resource.getFile().delete();

            if(contentType.startsWith("image")) {
                File thumbnailFile = new File(uploadPath + File.separator+ "s_" + fileName);
                thumbnailFile.delete();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        resultMap.put("result", removed);

        return resultMap;
    }
}
