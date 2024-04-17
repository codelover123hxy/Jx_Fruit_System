package team.CowsAndHorses.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MailSenderInfo {
      private String mailServerHost;
      private String mailServerPort;
      private String attachFileNames;
      private Boolean validate;
      private String username;
      private String password;
      private String fromAddress;
      private String toAddress;
      private String subject;
      private String content;
}
