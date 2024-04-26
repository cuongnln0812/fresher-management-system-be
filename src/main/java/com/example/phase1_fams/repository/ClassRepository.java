package com.example.phase1_fams.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.phase1_fams.model.Class;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClassRepository extends JpaRepository<Class, Long> {
    Optional<Class> findById(Long id);

    @Query("SELECT c FROM Class c WHERE c.status = 'Scheduled' AND " +
            "(SELECT MIN(s.sessionDate) FROM Session s WHERE s.aClass = c) <= :now")
    List<Class> findScheduledClassesBefore(LocalDate now);

    @Query("SELECT c FROM Class c WHERE c.status = 'Opening' AND " +
            "(SELECT MAX(s.sessionDate) FROM Session s WHERE s.aClass = c) < :now")
    List<Class> findOpeningClassesAfter(LocalDate now);

    @Query("SELECT c FROM Class c WHERE " +
            "(:keywords IS NULL OR :keywords = '' OR (LOWER(c.name) LIKE CONCAT('%', LOWER(:keywords), '%')) OR (LOWER(c.code) LIKE CONCAT('%', LOWER(:keywords), '%'))) AND " +
            "(:locations IS NULL OR c.location IN :locations) AND " +
            "(:attendeeTypes IS NULL OR c.attendeeType IN :attendeeTypes) AND " +
            "((SELECT MIN(s.sessionDate) FROM Session s WHERE s.aClass = c) >= :fromDate) AND " +
            "((SELECT MAX(s.sessionDate) FROM Session s WHERE s.aClass = c) <= :toDate) AND " +
            "(:classTimes IS NULL OR c.classTime IN :classTimes) AND " +
            "(:statuses IS NULL OR c.status IN :statuses) AND " +
            "(:fsus IS NULL OR c.fsu IN :fsus)")
    Page<Class> findFilteredClassesWhenFromDateAndToDateNotNull(@Param("keywords") String keywords,
                                    @Param("locations") List<String> locations,
                                    @Param("attendeeTypes") List<String> attendeeTypes,
                                    @Param("fromDate") LocalDate fromDate,
                                    @Param("toDate") LocalDate toDate,
                                    @Param("classTimes") List<String> classTimes,
                                    @Param("statuses") List<String> statuses,
                                    @Param("fsus") List<String> fsus,
                                    Pageable pageable);

    @Query("SELECT c FROM Class c WHERE " +
            "(:keywords IS NULL OR :keywords = '' OR (LOWER(c.name) LIKE CONCAT('%', LOWER(:keywords), '%')) OR (LOWER(c.code) LIKE CONCAT('%', LOWER(:keywords), '%'))) AND " +
            "(:locations IS NULL OR c.location IN :locations) AND " +
            "(:attendeeTypes IS NULL OR c.attendeeType IN :attendeeTypes) AND " +
            "(:classTimes IS NULL OR c.classTime IN :classTimes) AND " +
            "(:statuses IS NULL OR c.status IN :statuses) AND " +
            "(:fsus IS NULL OR c.fsu IN :fsus)")
    Page<Class> findFilteredClassesWhenFromDateAndToDateNull(@Param("keywords") String keywords,
                                                             @Param("locations") List<String> locations,
                                                             @Param("attendeeTypes") List<String> attendeeTypes,
                                                             @Param("classTimes") List<String> classTimes,
                                                             @Param("statuses") List<String> statuses,
                                                             @Param("fsus") List<String> fsus,
                                                             Pageable pageable);

    Optional<Integer> countAllByStatus(String status);
}

