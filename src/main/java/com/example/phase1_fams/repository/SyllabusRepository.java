package com.example.phase1_fams.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.phase1_fams.model.Syllabus;

@Repository
public interface SyllabusRepository extends JpaRepository<Syllabus, String> {
        Optional<Syllabus> findByCode(String code);

        @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Syllabus s WHERE s.code = ?1")
        boolean existsByCode(String code);

        @Query("SELECT DISTINCT l.code " +
                        "FROM Syllabus s " +
                        "JOIN s.daysUnits d " + // Assuming daysUnits is the correct field name in Syllabus
                        "JOIN d.trainingUnits u " + // Assuming trainingUnits is the correct field name in DaysUnit
                        "JOIN u.trainingContents c " + // Assuming trainingContents is the correct field name in// TrainingUnit
                        "JOIN c.objectiveCodes l WHERE s.code = :code") // Now directly joining with LearningObjective due to ManyToMany
        List<String> findDistinctObjectiveCodes(String code);

        @Query("SELECT s FROM Syllabus s WHERE " +
                "(:searchKey IS NULL OR :searchKey = '' OR (LOWER(s.name) LIKE CONCAT('%', LOWER(:searchKey), '%')) OR (LOWER(s.code) LIKE CONCAT('%', LOWER(:searchKey), '%'))) AND " +
                "(:createdBy IS NULL OR s.createdBy IN :createdBy) AND " +
                "(s.createdDate >= :startDate) AND " +
                "(s.createdDate <= :endDate) AND " +
                "(:duration IS NULL OR (SELECT COUNT(d.dayNumber) FROM DaysUnit d WHERE d.syllabus = s) = :duration) AND " +
                "(:outputStandards IS NULL OR (SELECT DISTINCT l.code " +
                                                        "FROM Syllabus sd "  +
                                                        "JOIN s.daysUnits d " + // Assuming daysUnits is the correct field name in Syllabus\n" +
                                                        "JOIN d.trainingUnits u " + // Assuming trainingUnits is the correct field name in DaysUnit\n" +
                                                        "JOIN u.trainingContents c " + // Assuming trainingContents is the correct field name in// TrainingUnit\n" +
                                                        "JOIN c.objectiveCodes l WHERE sd = s) IN :outputStandards) AND " +
                "(:statuses IS NULL OR s.status IN :statuses)")
        Page<Syllabus> searchByDateNotNull(@Param("searchKey") String searchKey, @Param("createdBy") List<String> createdBy,
                                          @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
                                          @Param("duration") Integer duration, @Param("outputStandards") List<String> outputStandards,
                                          @Param("statuses") List<Integer> statuses, Pageable pageable);

        @Query("SELECT s FROM Syllabus s WHERE " +
                "(:searchKey IS NULL OR :searchKey = '' OR (LOWER(s.name) LIKE CONCAT('%', LOWER(:searchKey), '%')) OR (LOWER(s.code) LIKE CONCAT('%', LOWER(:searchKey), '%'))) AND " +
                "(:createdBy IS NULL OR s.createdBy IN :createdBy) AND " +
                "(:duration IS NULL OR (SELECT COUNT(d.dayNumber) FROM DaysUnit d WHERE d.syllabus = s) = :duration) AND " +
                "(:outputStandards IS NULL OR EXISTS(" +
                "    SELECT 1 FROM Syllabus sd" +
                "    JOIN sd.daysUnits d" +
                "    JOIN d.trainingUnits u" +
                "    JOIN u.trainingContents c" +
                "    JOIN c.objectiveCodes l "+
                "    WHERE sd = s AND l.code IN :outputStandards)) AND " +
                "(:statuses IS NULL OR s.status IN :statuses)")
        Page<Syllabus> searchByDateNull(@Param("searchKey") String searchKey, @Param("createdBy") List<String> createdBy,
                                          @Param("duration") Integer duration, @Param("outputStandards") List<String> outputStandards,
                                          @Param("statuses") List<Integer> statuses, Pageable pageable);

        List<Syllabus> findAllByNameContainingIgnoreCaseAndStatus(String name, int status);

        List<Syllabus> findAllByStatus(int status);

        Optional<Integer> countAllByStatus(int status);
}
