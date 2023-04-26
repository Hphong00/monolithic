package com.web.monolithic.service;

import com.web.monolithic.config.Constants;
import com.web.monolithic.domain.Authority;
import com.web.monolithic.domain.User;
import com.web.monolithic.repository.AuthorityRepository;
import com.web.monolithic.repository.UserRepository;
import com.web.monolithic.security.AuthoritiesConstants;
import com.web.monolithic.web.rest.ShippingResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.security.RandomUtil;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
    boolean alreadySetup = false;
    private final Logger log = LoggerFactory.getLogger(SetupDataLoader.class);
    final
    UserRepository userRepository;
    final
    PasswordEncoder passwordEncoder;
    final AuthorityRepository authorityRepository;

    public SetupDataLoader(PasswordEncoder passwordEncoder, UserRepository userRepository, AuthorityRepository authorityRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup) {
            return;
        }
        if ((!userRepository.findOneByLogin("admin").isPresent())) {
            log.debug("REST create to role on Application : {}");
            Authority adminau = new Authority();
            adminau.setName(AuthoritiesConstants.ADMIN);
            Authority userau = new Authority();
            userau.setName(AuthoritiesConstants.USER);
            Authority anyau = new Authority();
            anyau.setName(AuthoritiesConstants.ANONYMOUS);
            List<Authority> listau = new LinkedList<>();
            listau.add(adminau);
            listau.add(userau);
            listau.add(anyau);
            authorityRepository.saveAll(listau);

            log.debug("REST create user on Application : {}");
            User user = new User();
            user.setLogin("admin");
            user.setActivationKey("admin");
            user.setLangKey("admin");
            user.setActivated(true);
            user.setEmail("admin@gmail.com");
            user.setLastName("admin");
            user.setLastName("admin");
            user.setPassword(passwordEncoder.encode("admin"));
            user.setLastName("admin");
            user.setLangKey(Constants.DEFAULT_LANGUAGE);
            user.setResetKey(RandomUtil.generateResetKey());
            user.setResetDate(Instant.now());
            user.setActivated(true);
            Set<String> aut = new HashSet<>();
            aut.add(AuthoritiesConstants.ADMIN);
            aut.add(AuthoritiesConstants.USER);
            Set<Authority> authorities = aut
                .stream()
                .map(authorityRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            user.setAuthorities(authorities);
            userRepository.save(user);
            alreadySetup = true;
        } else {
            alreadySetup = true;
        }
    }
}
