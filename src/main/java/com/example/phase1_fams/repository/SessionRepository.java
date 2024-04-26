package com.example.phase1_fams.repository;

import com.example.phase1_fams.model.Class;
import com.example.phase1_fams.model.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    List<Session> findBySessionDate(LocalDate date);

    List<Session> findBySessionDateBetweenOrderBySessionDateAsc(LocalDate start, LocalDate end);

    @Query("SELECT s from Session s where s.aClass.id = :classId")
    List<Session> findByAClassId(Long classId);
    void deleteAllByaClass(Class aClass);

    @Query("SELECT s FROM Session s WHERE " +
            "(:keywords IS NULL OR :keywords = '' OR (LOWER(s.aClass.name) LIKE CONCAT('%', LOWER(:keywords), '%')) OR (LOWER(s.aClass.code) LIKE CONCAT('%', LOWER(:keywords), '%'))) AND " +
            "(:locations IS NULL OR s.location IN :locations) AND " +
            "(:classTime IS NULL OR s.aClass.classTime IN :classTime) AND " +
            "(s.sessionDate >= :fromDate) AND " +
            "(s.sessionDate <= :toDate) AND " +
            "(:statuses IS NULL OR s.aClass.status IN :statuses) AND " +
            "(:trainer IS NULL OR s.trainerName IN :trainer) AND " +
            "(:fsus IS NULL OR s.aClass.fsu IN :fsus)")
    List<Session> findFilteredSession(@Param("keywords") String keywords,
                                    @Param("locations") List<String> locations,
                                    @Param("classTime") List<String> classTime,
                                    @Param("fromDate") LocalDate fromDate,
                                    @Param("toDate") LocalDate toDate,
                                    @Param("statuses") List<String> statuses,
                                    @Param("fsus") List<String> fsus,
                                    @Param("trainer") List<String> trainer);
}
