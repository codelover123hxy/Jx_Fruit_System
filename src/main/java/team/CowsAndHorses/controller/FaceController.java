package team.CowsAndHorses.controller;

import org.springframework.web.bind.annotation.*;
import team.CowsAndHorses.dto.AjaxResult;

import java.io.File;

@RestController
@CrossOrigin
@RequestMapping("/api/face")

public class FaceController {

    @PostMapping("/judge")
    public boolean judgeFace(@RequestParam String url1, @RequestParam String url2) {
        return judge(url1, url2);
    }

    public boolean judge(String url1, String url2) {
        File file1 = new File(url1);
        boolean flag = false;
//        String baseURL = "localhost:8080";
//        String url = baseURL + "/api/face/judge";
        return flag;
    }
}
