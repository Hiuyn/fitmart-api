package dev.fitmart.FItMart.components.upload;

import dev.fitmart.FItMart.exception.ApiException;
import dev.fitmart.FItMart.exception.BaseResponse;
import dev.fitmart.FItMart.exception.ResponseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:3000")
public class UploadController {

    private static final String UPLOAD_DIR = "C:/uploads/";

    @PostMapping("/uploads")
    public ResponseEntity<BaseResponse<String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Tạo thư mục nếu chưa có
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String filePath = UPLOAD_DIR + fileName;

            // Lưu file
            file.transferTo(new File(filePath));

            // Trả về đường dẫn vật lý
            String fileUrl = "http://localhost:8080/api/v1/uploads/" + fileName;
            return ResponseUtils.success(fileUrl);

        } catch (IOException e) {
            e.printStackTrace();
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi lưu file");
        }
    }
}

