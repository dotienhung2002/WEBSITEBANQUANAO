package com.application.fusamate.rest.controller;

import com.application.fusamate.configuration.Constants;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;


@RestController
@CrossOrigin
public class FileImageUploadController {
    @PostMapping("/upload-file")
    public ResponseEntity uploadImage(@RequestParam("file")MultipartFile file) throws IOException {
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap("cloud_name", "dipuod7ic", "api_key",
                "984759436885181", "api_secret", "jCLKd0nuPhe_MJASJ16e5dcUYXE", "secure", true));
        String link = "";
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            link = String.valueOf(uploadResult.get("url"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map map = new HashMap();
        map.put("url", link);
        return ResponseEntity.ok(map);
    }
}
