package com.module.project.service;

import com.module.project.repository.ServicePackageRepository;
import com.module.project.repository.ServiceTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceAddOnService {
    private final ServiceTypeRepository serviceTypeRepository;
    private final ServicePackageRepository servicePackageRepository;

}
