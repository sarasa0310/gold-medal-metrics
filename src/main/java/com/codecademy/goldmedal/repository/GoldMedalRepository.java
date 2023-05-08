package com.codecademy.goldmedal.repository;

import com.codecademy.goldmedal.model.GoldMedal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoldMedalRepository extends JpaRepository<GoldMedal, Integer> {

    List<GoldMedal> findByCountryOrderByYear(String countryName);

    List<GoldMedal> findByCountryOrderBySeason(String countryName);

    List<GoldMedal> findByCountryOrderByCity(String countryName);

    List<GoldMedal> findByCountryOrderByName(String countryName);

    List<GoldMedal> findByCountryOrderByEvent(String countryName);

}
