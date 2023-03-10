package com.application.fusamate.rest.controller;

import com.application.fusamate.configuration.Constants;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;


@RestController
@CrossOrigin
public class FileImageUploadController {
    @PostMapping("/upload-file")
    public ResponseEntity uploadImage(@RequestParam("file")MultipartFile file) throws IOException {
        System.out.println(file.getOriginalFilename());
        System.out.println(file.getName());
        System.out.println(file.getContentType());
        System.out.println  (file.getSize());
        String Path_Directory = "/var/local/backend-fusamate/fusamate-be/src/main/resources/static/image";
//        String Path_Directory = "/Users/abc/Downloads/Workspace/projectFPT/fusamate-be/src/main/resources/static/image";

        Files.copy(file.getInputStream(), Paths.get(Path_Directory+ File.separator+file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
        Map map = new HashMap();
        map.put("url", Constants.BASE_URL_IMAGE +file.getOriginalFilename());
        return ResponseEntity.ok(map);
    }
}
