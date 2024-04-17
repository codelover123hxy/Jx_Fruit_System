package team.CowsAndHorses.service;

import team.CowsAndHorses.dto.Email;

public interface EmailService {
    void sendEmail(Email email) throws Exception;
}
