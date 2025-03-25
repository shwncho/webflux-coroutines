package com.example.webflux.runner;

import com.example.webflux.common.repository.UserEntity;
import com.example.webflux.repository.UserR2dbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
//@Component
public class PersonInsertRunner implements CommandLineRunner {
    private final UserR2dbcRepository userR2dbcRepository;

    @Override
    public void run(String... args) throws Exception {
        var newUser = new UserEntity("simple", 20,"1","1q2w3e4r!");
        var savedUser = userR2dbcRepository.save(newUser).block();
        log.info("savedUser: {}", savedUser);
    }
}
