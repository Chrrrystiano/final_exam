package com.example.exam.service;

import com.example.exam.exceptions.EmailValidationException;
import com.example.exam.exceptions.InvalidDataFileException;
import com.example.exam.exceptions.PeselValidationException;
import com.example.exam.model.pensioner.Pensioner;
import com.example.exam.repository.PensionerRepository;
import com.example.exam.repository.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PensionerService {
    private final PensionerRepository pensionerRepository;
    private final PersonRepository personRepository;

    @Autowired
    public PensionerService(PensionerRepository pensionerRepository, PersonRepository personRepository) {
        this.pensionerRepository = pensionerRepository;
        this.personRepository = personRepository;
    }

    @Transactional
    public void savePensioner(Pensioner pensioner) {
        if (personRepository.existingPesel(pensioner.getPesel())) {
            throw new PeselValidationException("Wrong PESEL number. This PESEL is already in the database.");
        }
        if (personRepository.existingEmail(pensioner.getEmail())) {
            throw new EmailValidationException("Wrong EMAIL number. This EMAIL is already in the database.");
        }
        try {
            pensionerRepository.save(pensioner);
        } catch (NumberFormatException e) {
            throw new InvalidDataFileException("Error processing record due to number format issue: " + e.getMessage());
        }
    }

}
