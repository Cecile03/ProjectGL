package com.example.backend.security;

import com.example.backend.dao.GradeTypesDao;
import com.example.backend.dao.RoleDao;
import com.example.backend.model.GradeTypes;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import com.example.backend.model.Role;
import com.example.backend.model.Role.RoleName;

import java.util.Arrays;
import java.util.List;

// Configuration to add default roles in database
@Configuration
public class DataInitializer {

    private final RoleDao roleRepository;

    private final GradeTypesDao gradeTypesDao;

    @Autowired
    public DataInitializer(RoleDao roleRepository, GradeTypesDao gradeTypesDao) {
        this.roleRepository = roleRepository;
        this.gradeTypesDao = gradeTypesDao;
    }

    @PostConstruct
    public void initData() {
        initRoles();
        initGradeTypes();
    }

    public void initRoles() {
        if (roleRepository.count() == 0) {
            List<Role> defaultRoles = Arrays.asList(
                    new Role(RoleName.SS),
                    new Role(RoleName.PL),
                    new Role(RoleName.OL),
                    new Role(RoleName.OS),
                    new Role(RoleName.TC)
            );
            roleRepository.saveAll(defaultRoles);
        }
    }

    public void initGradeTypes() {
        if (gradeTypesDao.count() == 0) {
            List<GradeTypes> defaultGradeTypes = Arrays.asList(
                    new GradeTypes(GradeTypes.GradeTypesEnum.OTPR),
                    new GradeTypes(GradeTypes.GradeTypesEnum.SPCO),
                    new GradeTypes(GradeTypes.GradeTypesEnum.TESO),
                    new GradeTypes(GradeTypes.GradeTypesEnum.PRMO),
                    new GradeTypes(GradeTypes.GradeTypesEnum.SUPR),
                    new GradeTypes(GradeTypes.GradeTypesEnum.TEBM),
                    new GradeTypes(GradeTypes.GradeTypesEnum.SSBM),
                    new GradeTypes(GradeTypes.GradeTypesEnum.SSPR),
                    new GradeTypes(GradeTypes.GradeTypesEnum.TCPR)
            );
            gradeTypesDao.saveAll(defaultGradeTypes);
        }
    }

}
