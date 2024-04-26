package com.example.phase1_fams.repository;

import com.example.phase1_fams.model.Class;
import com.example.phase1_fams.model.ClassUser;
import com.example.phase1_fams.model.ClassUserKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassUserRepository extends JpaRepository<ClassUser, ClassUserKey> {
    void deleteAllByaClass(Class aClass);
}
