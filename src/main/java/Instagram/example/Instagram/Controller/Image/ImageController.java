package Instagram.example.Instagram.Controller.Image;

import Instagram.example.Instagram.Repository.Image.ImageRepository;
import Instagram.example.Instagram.Service.Image.ImageService;
import Instagram.example.Instagram.domain.Image.Image;
import Instagram.example.Instagram.domain.user.User;
import Instagram.example.Instagram.web.dto.Image.ImageUploadDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/v1/images")
public class ImageController {
    private ImageService imageService;
    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }
    //이미지 업로드
    @PostMapping("/upload")
    public String imageUpload(ImageUploadDTO imageUploadDTO, HttpServletRequest request) {
        HttpSession session = request.getSession();

        if (imageUploadDTO.getFile().isEmpty()) {
            String errorMessage = "이미지가 첨부되지 않음";
            //세션으로 에러메세지 전달 후 다시 upload 할 수 있도록
            session.setAttribute("uploadErrorMessage", errorMessage);
            return "redirect:/upload";
        }

        // 세션에서 사용자 정보를 가져옴
        User user = (User) session.getAttribute("currentUser");

        // 이미지 업로드 서비스 호출
        imageService.imageUpload(imageUploadDTO.getFile(),user);

        return "redirect:/user/" + user.getId();
    }
//태그로 이미지 조회
@GetMapping("/ByTag")
// "/api/v1/images/ByTags=태그명"으로 이미지 조회 가능
public List<Image> getImagesByTag(@RequestParam String tagName) {
    return imageService.getImagesByTag(tagName);
}
// 상세 이미지를 조회
@GetMapping("/{id}")
//@PathVariable : 클라이언트 요청으로 url경로 추출할 수 있도록
public ResponseEntity<Image> getImageById(@PathVariable int id) {
    // 이미지 서비스를 사용하여 이미지를 조회
    Optional<Image> image = imageService.getImageById(id);

    // 이미지가 존재 -> ResponseEntity로 감싸서 반환
    if (image.isPresent()) {
        return ResponseEntity.ok(image.get());
    } else {
        // 이미지가 존재하지 않는 경우 404 Not Found를 반환 : 다른 사용자가 이미지 삭제했을 경우
        return ResponseEntity.notFound().build();
        }
    }
}

