package com.pm.patient_service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pm.patient_service.dto.PatientRequestDTO;
import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.exception.EmailAlreadyExistsException;
import com.pm.patient_service.exception.PatientNotFoundException;
import com.pm.patient_service.mapper.PatientMapper;
import com.pm.patient_service.model.Patient;
import com.pm.patient_service.repository.PatientRepository;
@Service
public class PatientService {
    PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository){
        this.patientRepository = patientRepository;
    }

    public List<PatientResponseDTO> getPatients(){
        List<Patient> patients = patientRepository.findAll();

       // List<PatientResponseDTO> patientResponseDTOs = patients.stream().map(patient -> PatientMapper.toDto(patient)).toList();

        List<PatientResponseDTO> patientResponseDTOs = new ArrayList<>();

        for (Patient patient: patients) {
            PatientResponseDTO patientResponseDTO = PatientMapper.toDto(patient);
            patientResponseDTOs.add(patientResponseDTO);
        }
        return patientResponseDTOs; 
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO){
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())){
            throw new EmailAlreadyExistsException("Email already exists: " + patientRequestDTO.getEmail());
        }
        Patient newPatient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));
        return PatientMapper.toDto(newPatient); 
    }

    public PatientResponseDTO updatePatient(UUID Id, PatientRequestDTO patientRequestDTO){
        Patient patient = patientRepository.findById(Id).orElseThrow(
            () -> new PatientNotFoundException("Patient not found with ID : " +Id)
        );

        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), Id)){
            throw new EmailAlreadyExistsException("Email already exists: " + patientRequestDTO.getEmail());
        }

        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setDateOfBirth(patient.getDateOfBirth());

        Patient updatedPatient = patientRepository.save(patient);
        return PatientMapper.toDto(updatedPatient);

    }

    public void deletePatient(UUID id){
        if(!patientRepository.existsById(id)){
            throw new PatientNotFoundException("Patient not found with ID: " +id);
        }
        patientRepository.deleteById(id);
    }
}
