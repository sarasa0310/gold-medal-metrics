package com.codecademy.goldmedal.controller;

import com.codecademy.goldmedal.model.*;
import com.codecademy.goldmedal.repository.CountryRepository;
import com.codecademy.goldmedal.repository.GoldMedalRepository;
import org.apache.commons.text.WordUtils;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/countries")
public class GoldMedalController {

    private final GoldMedalRepository goldMedalRepository;

    private final CountryRepository countryRepository;

    public GoldMedalController(GoldMedalRepository goldMedalRepository,
                               CountryRepository countryRepository) {
        this.goldMedalRepository = goldMedalRepository;
        this.countryRepository = countryRepository;
    }

    @GetMapping
    public CountriesResponse getCountries(@RequestParam String sort_by, @RequestParam String ascending) {
        var ascendingOrder = ascending.equalsIgnoreCase("y");

        return new CountriesResponse(getCountrySummaries(sort_by.toLowerCase(), ascendingOrder));
    }

    @GetMapping("/{country}")
    public CountryDetailsResponse getCountryDetails(@PathVariable String country) {
        String countryName = WordUtils.capitalizeFully(country);

        return getCountryDetailsResponse(countryName);
    }

    @GetMapping("/{country}/medals")
    public CountryMedalsListResponse getCountryMedalsList(@PathVariable String country,
                                                          @RequestParam String sort_by,
                                                          @RequestParam String ascending) {
        String countryName = WordUtils.capitalizeFully(country);
        var ascendingOrder = ascending.equalsIgnoreCase("y");

        return getCountryMedalsListResponse(countryName, sort_by.toLowerCase(), ascendingOrder);
    }

    private List<CountrySummary> getCountrySummaries(String sortBy, boolean ascendingOrder) {
        List<Country> countries;

        if (sortBy.equalsIgnoreCase("medals")) {
            countries = countryRepository.findAll();
        } else {
            Sort sort = (ascendingOrder) ? Sort.by(sortBy) : Sort.by(sortBy).descending();
            countries = countryRepository.findAll(sort);
        }

        var countrySummaries = getCountrySummariesWithMedalCount(countries);

        if (sortBy.equalsIgnoreCase("medals")) {
            countrySummaries = sortByMedalCount(countrySummaries, ascendingOrder);
        }

        return countrySummaries;
    }

    private List<CountrySummary> getCountrySummariesWithMedalCount(List<Country> countries) {
        List<CountrySummary> countrySummaries = new ArrayList<>();

        for (var country : countries) {
            var goldMedalCount = goldMedalRepository.countByCountry(country.getName());
            countrySummaries.add(new CountrySummary(country, goldMedalCount));
        }

        return countrySummaries;
    }

    private List<CountrySummary> sortByMedalCount(List<CountrySummary> countrySummaries, boolean ascendingOrder) {
        return countrySummaries
                .stream()
                .sorted((t1, t2) -> ascendingOrder ?
                        t1.getMedals() - t2.getMedals() :
                        t2.getMedals() - t1.getMedals())
                .collect(Collectors.toList());
    }

    private CountryDetailsResponse getCountryDetailsResponse(String countryName) {
        var countryOptional = countryRepository.findByName(countryName);

        if (countryOptional.isEmpty()) {
            return new CountryDetailsResponse(countryName);
        }

        var country = countryOptional.get();

        var goldMedalCount = goldMedalRepository.countByCountry(countryName);

        var summerWins = goldMedalRepository.findByCountryAndSeasonOrderByYear(countryName, "Summer");
        var numberSummerWins = summerWins.size() > 0 ? summerWins.size() : null;
        var totalSummerEvents = goldMedalRepository.countBySeason("Summer");
        var percentageTotalSummerWins = totalSummerEvents != 0 && numberSummerWins != null ? (float) summerWins.size() / totalSummerEvents : null;
        var yearFirstSummerWin = summerWins.size() > 0 ? summerWins.get(0).getYear() : null;

        var winterWins = goldMedalRepository.findByCountryAndSeason(countryName, "Winter");
        var numberWinterWins = winterWins.size() > 0 ? winterWins.size() : null;
        var totalWinterEvents = goldMedalRepository.findBySeasonOrderByYear("Winter").size();
        var percentageTotalWinterWins = totalWinterEvents != 0 && numberWinterWins != null ? (float) winterWins.size() / totalWinterEvents : null;
        var yearFirstWinterWin = winterWins.size() > 0 ? winterWins.get(0).getYear() : null;

        var numberEventsWonByFemaleAthletes = goldMedalRepository.countByCountryAndGender(countryName, "Women");
        var numberEventsWonByMaleAthletes = goldMedalRepository.countByCountryAndGender(countryName, "Men");

        return new CountryDetailsResponse(
                countryName,
                country.getGdp(),
                country.getPopulation(),
                goldMedalCount,
                numberSummerWins,
                percentageTotalSummerWins,
                yearFirstSummerWin,
                numberWinterWins,
                percentageTotalWinterWins,
                yearFirstWinterWin,
                numberEventsWonByFemaleAthletes,
                numberEventsWonByMaleAthletes);
    }

    private CountryMedalsListResponse getCountryMedalsListResponse(String countryName, String sortBy, boolean ascendingOrder) {
        Sort sort = (ascendingOrder) ? Sort.by(sortBy) : Sort.by(sortBy).descending();

        List<GoldMedal> medalsList = goldMedalRepository.findByCountry(countryName, sort);

        return new CountryMedalsListResponse(medalsList);
    }

}
