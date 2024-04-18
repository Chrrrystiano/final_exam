package com.example.exam.service;

import com.example.exam.exceptions.InvalidDataFileException;
import com.example.exam.model.pensioner.Pensioner;
import com.example.exam.repository.PensionerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PensionerService {
    private final PensionerRepository pensionerRepository;

    @Autowired
    public PensionerService(PensionerRepository pensionerRepository) {
        this.pensionerRepository = pensionerRepository;
    }

    @Transactional
    public void savePensioner(Pensioner pensioner) {
        try {
            pensionerRepository.save(pensioner);
        } catch (NumberFormatException e) {
            throw new InvalidDataFileException("Error processing record due to number format issue: " + e.getMessage());
        }
    }

}
