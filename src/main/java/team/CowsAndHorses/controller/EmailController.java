package team.CowsAndHorses.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import team.CowsAndHorses.dto.AjaxResult;
import team.CowsAndHorses.dto.Email;
import team.CowsAndHorses.service.EmailService;

@RequiredArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/email")
public class EmailController {
    final EmailService emailService;

    @PostMapping("/send")
    public Object sendEmail(@RequestBody Email email) throws Exception {
        emailService.sendEmail(email);
        return AjaxResult.SUCCESS("发送成功", null);
    }
}
