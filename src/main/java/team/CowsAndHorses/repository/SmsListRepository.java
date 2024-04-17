package team.CowsAndHorses.repository;

import org.springframework.data.repository.CrudRepository;
import team.CowsAndHorses.dto.QttDto;

public interface SmsListRepository extends CrudRepository<QttDto, String> {

}

