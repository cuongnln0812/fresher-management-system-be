package com.example.phase1_fams.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.phase1_fams.model.Class;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.phase1_fams.model.TrainingProgram;

@Repository
public interface TrainingProgramRepository extends JpaRepository<TrainingProgram, Long> {

    @Query("SELECT t FROM TrainingProgram t WHERE t.name = :name")
    Optional<TrainingProgram> findByName(String name);

    Optional<TrainingProgram> findById(Long id);

    Optional<TrainingProgram> findByIdAndStatus(Long id, int status);

    @Query("SELECT t FROM TrainingProgram t WHERE " +
            "(:keywords IS NULL OR :keywords = '' OR (LOWER(t.name) LIKE CONCAT('%', LOWER(:keywords), '%'))) AND " +
            "(:createdBy IS NULL OR t.createdBy IN :createdBy) AND " +
            "(:duration IS NULL OR t.duration = :duration) AND " +
            "(t.createdDate >= :startDate) AND " +
            "(t.createdDate <= :endDate) AND " +
            "(:statuses IS NULL OR t.status IN :statuses)")
    Page<TrainingProgram> searchWithDateNotNull(@Param("keywords") String keywords, @Param("createdBy") List<String> createdBy,
                                 @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
                                 @Param("duration") Integer duration, @Param("statuses") List<Integer> statuses, Pageable pageable);


    @Query("SELECT t FROM TrainingProgram t WHERE " +
            "(:keywords IS NULL OR :keywords = '' OR (LOWER(t.name) LIKE CONCAT('%', LOWER(:keywords), '%'))) AND " +
            "(:createdBy IS NULL OR t.createdBy IN :createdBy) AND " +
            "(:duration IS NULL OR t.duration = :duration) AND " +
            "(:statuses IS NULL OR t.status IN :statuses)")
    Page<TrainingProgram> searchWithDateNull(@Param("keywords") String keywords, @Param("createdBy") List<String> createdBy,
                                 @Param("duration") Integer duration, @Param("statuses") List<Integer> statuses, Pageable pageable);

    @Query("SELECT t FROM TrainingProgram t WHERE " +
            "(:keywords IS NULL OR :keywords = '' OR (LOWER(t.name) LIKE CONCAT('%', LOWER(:keywords), '%'))) AND " +
            "(:createdBy IS NULL OR t.createdBy IN :createdBy) AND " +
            "(:duration IS NULL OR t.duration = :duration) AND " +
            "(t.createdDate >= :startDate) AND " +
            "(t.createdDate <= :endDate) AND " +
            "(:statuses IS NULL OR t.status IN :statuses) AND t.status = 1")

    Page<TrainingProgram> searchWithDateNotNullWithStatusActive(@Param("keywords") String keywords, @Param("createdBy") List<String> createdBy,
                                                @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
                                                @Param("duration") Integer duration, @Param("statuses") List<Integer> statuses, Pageable pageable);


    @Query("SELECT t FROM TrainingProgram t WHERE " +
            "(:keywords IS NULL OR :keywords = '' OR (LOWER(t.name) LIKE CONCAT('%', LOWER(:keywords), '%'))) AND " +
            "(:createdBy IS NULL OR t.createdBy IN :createdBy) AND " +
            "(:duration IS NULL OR t.duration = :duration) AND " +
            "(:statuses IS NULL OR t.status IN :statuses) AND t.status = 1")
    Page<TrainingProgram> searchWithDateNullWithStatusActive(@Param("keywords") String keywords, @Param("createdBy") List<String> createdBy,
                                             @Param("duration") Integer duration, @Param("statuses") List<Integer> statuses, Pageable pageable);

    List<TrainingProgram> findAllByNameContainingIgnoreCaseAndStatus(String name, int status);

    List<TrainingProgram> findAllByStatus(int status);

    Optional<Integer> countAllByStatus(int status);

    @Query("SELECT c FROM TrainingProgram t JOIN Class c ON t.id = c.trainingProgram.id WHERE t.id = :id")
    Page<Class> getAllClassByTrainingProgram(@Param("id") Long id, PageRequest pageRequest);
}
