package com.codecademy.goldmedal.repository;

import com.codecademy.goldmedal.model.GoldMedal;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoldMedalRepository extends JpaRepository<GoldMedal, Long> {

    List<GoldMedal> findByCountry(String country, Sort sort);

    List<GoldMedal> findByCountryAndSeason(String country, String season);

    List<GoldMedal> findByCountryAndSeasonOrderByYear(String country, String season);

    List<GoldMedal> findBySeasonOrderByYear(String season);

    int countByCountry(String country);

    int countBySeason(String season);

    int countByCountryAndGender(String country, String gender);

}
