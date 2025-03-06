package net.engineeringdigest.journalApp.service;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.repository.UserRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;
    private  static  final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private  static  final Logger logger = LoggerFactory.getLogger(UserService.class);


    public void saveUser(User users) {
        userRepository.save(users);
    }

    public void saveNewUser(User users) {
        try {
            users.setPassword(passwordEncoder.encode(users.getPassword()));
            users.setRoles(Arrays.asList("USER"));
            userRepository.save(users);
        }
        catch (Exception e ){
//            logger.error("An error occured for {} :", users.getUserName(), e);
            log.error("An error occured for {} :", users.getUserName(), e);
            logger.info("An info occured");
            logger.warn("An warn occured");
            logger.debug("An debug occured");
            logger.trace("An trace occured");

        }

    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public Optional<User> getById(ObjectId objectId) {
        return userRepository.findById(objectId);
    }

    public String deleteEntry(ObjectId id) {
        userRepository.deleteById(id);
        return "Entry Deleted";

    }

    public User updateEntry(ObjectId myId, User newEntry) {
        User oldEntry = getById(myId).orElse(null);
        if (oldEntry != null) {
            oldEntry.setUserName(!newEntry.getUserName().isEmpty() ? newEntry.getUserName() : oldEntry.getUserName());
            oldEntry.setPassword(!newEntry.getPassword().isEmpty() ? newEntry.getPassword() : oldEntry.getPassword());
            saveUser(oldEntry);

        }
        return oldEntry;
    }

    public User findByUserName(String useName) {
        return  userRepository.findByUserName(useName);
    }

    public void saveAdmin(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Arrays.asList("USER","ADMIN"));
        userRepository.save(user);
    }
}
