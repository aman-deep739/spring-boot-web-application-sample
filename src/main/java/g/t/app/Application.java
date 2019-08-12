package g.t.app;

import g.t.app.config.Constants;
import g.t.app.domain.Authority;
import g.t.app.domain.User;
import g.t.app.repository.AuthorityRepository;
import g.t.app.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;

@SpringBootApplication
@Slf4j
public class Application {

    public static void main(String[] args) throws UnknownHostException {

        SpringApplication app = new SpringApplication(Application.class);
        app.setDefaultProperties(Map.of("spring.profiles.default", Constants.SPRING_PROFILE_DEVELOPMENT));
        Environment env = app.run(args).getEnvironment();

        log.info("Access URLs:\n----------------------------------------------------------\n\t" +
                "Local: \t\t\thttp://localhost:{}\n\t" +
                "External: \t\thttp://{}:{}\n\t" +
                "Environment: \t{} \n\t" +
                "----------------------------------------------------------",
            env.getProperty("server.port"),
            InetAddress.getLocalHost().getHostAddress(),
            env.getProperty("server.port"),
            Arrays.toString(env.getActiveProfiles())
        );
    }

    @Bean
    public CommandLineRunner initData(UserRepository authorRepository,

                                      PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository,
                                      UserRepository userRepository) {

        return args -> {



            /*
            user accounts
             */

            Authority adminAuthority = new Authority();
            adminAuthority.setName(Constants.ROLE_ADMIN);
            authorityRepository.save(adminAuthority);

            Authority userAuthority = new Authority();
            userAuthority.setName(Constants.ROLE_USER);
            authorityRepository.save(userAuthority);

            //String encodedPassword = passwordEncoder.encode("pass");
            String encodedPassword = "$2a$10$UtqWHf0BfCr41Nsy89gj4OCiL36EbTZ8g4o/IvFN2LArruHruiRXO"; // to make it faster

            User adminUser = new User("system", LocalDate.now().minusYears(10), "System", "Tiwari", "system@email");
            adminUser.setPassword(encodedPassword);
            adminUser.setAuthorities(authorityRepository.findByNameIn(Constants.ROLE_ADMIN, Constants.ROLE_USER));
            userRepository.save(adminUser);

            /*
            other users
             */

            User user1 = new User("user1", LocalDate.now().minusYears(10), "Ganesh", "Tiwari", "gt@email");
            //user1.setAvatar(readClassPathFile("static/img/male-coat.png"));
            user1.setPassword(encodedPassword);
            user1.setAuthorities(authorityRepository.findByNameIn(Constants.ROLE_USER));
            authorRepository.save(user1);


            User user2 = new User("user2", LocalDate.now().minusYears(1), "Jyoti", "Kattel", "jk@email");
            //user2.setAvatar(readClassPathFile("static/img/male-coat.png"));
            user2.setPassword(encodedPassword);
            user2.setAuthorities(authorityRepository.findByNameIn(Constants.ROLE_USER));
            userRepository.save(user2);
        };


    }

    private byte[] readClassPathFile(String location) throws IOException {
        ClassPathResource cpr = new ClassPathResource(location);
        return FileCopyUtils.copyToByteArray(cpr.getInputStream());
    }

}
