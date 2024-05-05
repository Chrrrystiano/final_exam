package com.example.exam.service;

import com.example.exam.exceptions.InvalidDataFileException;
import com.example.exam.model.pensioner.Pensioner;
import com.example.exam.repository.PensionerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PensionerService {
    private final PensionerRepository pensionerRepository;

    @Transactional
    public void savePensioner(Pensioner pensioner) {
        try {
            pensionerRepository.save(pensioner);
        } catch (NumberFormatException e) {
            throw new InvalidDataFileException("Error processing record due to number format issue: " + e.getMessage());
        }
    }

}
