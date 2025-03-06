package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.repository.JournalEntryRepository;
import net.engineeringdigest.journalApp.repository.UserRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;
    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    private  static  final Logger logger = LoggerFactory.getLogger(JournalEntryService.class);

    @Transactional
    public void saveNewEntry(JournalEntry journalEntry, String userName) {
        try {
            journalEntry.setDate(LocalDateTime.now());
            journalEntryRepository.save(journalEntry);
            User user = userRepository.findByUserName(userName);
            user.getJournalEntryList().add(journalEntry);
/*
         we have saved in journal entries but if there will be any issue and below line does not run and failed then it will save in journal_entries
        but not in user, so it is inconsistency, so we will use transaction to avoid this issue like either all or none.
*/
            userRepository.save(user);
        }
        catch (Exception e){
            System.out.println("Error occured");
            throw new RuntimeException("An error occured while saving the entry");

        }

    }
    public void saveEntry(JournalEntry journalEntry) {
        journalEntryRepository.save(journalEntry);
    }



    public List<JournalEntry> getAll() {
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> getById(ObjectId objectId) {
        return journalEntryRepository.findById(objectId);
    }

    public void deleteEntry(ObjectId id , String userName) {
        // since its mongoDb so we have to do cascade delete meaning only deleting from journal entries will not delete id ref from user so we have to del id of journal list data from user as wel
        //like username,password,journalEntries:[dbref objectId("1234565432")]
        User user = userRepository.findByUserName(userName);
        boolean removed = user.getJournalEntryList().removeIf(x -> x.getId().equals(id));
        if(removed){
            userService.saveUser(user);
            journalEntryRepository.deleteById(id);

        }

    }



}
