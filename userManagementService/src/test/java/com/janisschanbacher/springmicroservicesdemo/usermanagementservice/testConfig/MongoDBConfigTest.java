package com.janisschanbacher.springmicroservicesdemo.usermanagementservice.testConfig;

import com.janisschanbacher.springmicroservicesdemo.usermanagementservice.document.User;
import com.janisschanbacher.springmicroservicesdemo.usermanagementservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackageClasses = UserRepository.class)
@Configuration
public class MongoDBConfigTest {

    /**
     * Seed the database using the commandline interface.
     *
     * @param userRepository userRepository that is used to add users to the database
     * @return CommandLineRunner that will seed the database
     */
    @Bean
    CommandLineRunner commandLineRunner(UserRepository userRepository) {
        return strings -> {
            userRepository.save(new User("testUser", "testFirst", "testLast", "password", "USER"));
            userRepository.save(new User("testAdmin", "adminFirst", "adminLast", "password", "ADMIN"));
        };
    }

}
