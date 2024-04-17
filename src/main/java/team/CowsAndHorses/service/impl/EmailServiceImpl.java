package team.CowsAndHorses.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import team.CowsAndHorses.dto.Email;
import team.CowsAndHorses.service.EmailService;
import team.CowsAndHorses.util.EmailUtils;
import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.smtpHost}")
    private String smtpHost;

    @Override
    public void sendEmail(Email email) throws Exception {
        String to = email.getTo();
        String subject = email.getTitle();
        String body = email.getContent();
        List<String> attachments = List.of("E:\\桌面\\ZZOnline.rar"); // 附件的路径，多个附件也不怕
        EmailUtils emailUtils = EmailUtils.entity(smtpHost, username, password, to, null, subject, body, attachments);
        emailUtils.send(); // 发送！
    }
}
