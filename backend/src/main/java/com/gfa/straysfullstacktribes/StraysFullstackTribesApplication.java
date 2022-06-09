
package com.gfa.straysfullstacktribes;

import com.gfa.straysfullstacktribes.models.AppUser;
import com.gfa.straysfullstacktribes.models.Kingdom;
import com.gfa.straysfullstacktribes.repositories.AppUserRepository;
import com.gfa.straysfullstacktribes.repositories.KingdomRepository;
import com.gfa.straysfullstacktribes.repositories.TroopRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@AllArgsConstructor
public class StraysFullstackTribesApplication implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final KingdomRepository kingdomRepository;
    private final TroopRepository troopRepository;
    private final BCryptPasswordEncoder encoder;


    private void addSampleData() {

        appUserRepository.save(new AppUser("Karel von Lichtenstein",  "karelvonlichtenstein@gmail.com",  encoder.encode("password1"), "xxxxx.yyyyy.zzzzz"));
        appUserRepository.save(new AppUser("Franta Lhotak",  "frantalhotak@gmail.com",  encoder.encode("password2"),"xxxx1.yyyy1.zzzz1"));
        appUserRepository.save(new AppUser("Franta Pepa",  "frantapepajednicka@gmail.com",  encoder.encode("password3"),  "xxxx2.yyyy2.zzzz2"));

        kingdomRepository.save(new Kingdom("Lichtenstein", 1L, 3L, appUserRepository.getById(1L), 10L, 10L, 10, 10));
        kingdomRepository.save(new Kingdom("Lhotkov", 2L, 4L, appUserRepository.getById(2L), 10L, 10L, 10, 10));
        kingdomRepository.save(new Kingdom("Pepikov", 6L, 8L, appUserRepository.getById(3L), 10L, 10L, 10, 10));
    }

    @Override
    public void run(String... args) throws Exception {
        //addSampleData();

        /*for (int i = 0; i < 10; i++) {
            troopRepository.save(new Troop(TroopType.KNIGHT, kingdomRepository.getById(1L)));
            troopRepository.save(new Troop(TroopType.KNIGHT, kingdomRepository.getById(2L)));

            troopRepository.save(new Troop(TroopType.FOOTMAN, kingdomRepository.getById(1L)));
            troopRepository.save(new Troop(TroopType.FOOTMAN, kingdomRepository.getById(2L)));

            troopRepository.save(new Troop(TroopType.SCOUT, kingdomRepository.getById(1L)));
            troopRepository.save(new Troop(TroopType.SCOUT, kingdomRepository.getById(2L)));
        }*/
    }

    public static void main(String[] args) {
        SpringApplication.run(StraysFullstackTribesApplication.class, args);
    }
}