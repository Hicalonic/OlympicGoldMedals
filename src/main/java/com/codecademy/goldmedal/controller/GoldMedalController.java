package com.codecademy.goldmedal.controller;

import com.codecademy.goldmedal.model.*;
import com.codecademy.goldmedal.respositories.CountryRepository;
import com.codecademy.goldmedal.respositories.GoldMedalRepository;
import org.apache.commons.text.WordUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/countries")
public class GoldMedalController {
    private final GoldMedalRepository goldMedalRepository;
    private final CountryRepository countryRepository;

    public GoldMedalController(GoldMedalRepository goldMedalRepository, CountryRepository countryRepository) {
        this.goldMedalRepository = goldMedalRepository;
        this.countryRepository = countryRepository;
    }

    @GetMapping
    public CountriesResponse getCountries(@RequestParam(name="sortBy", required = false) String sort_by,@RequestParam(name="ascending", required = false) String ascending) {
        boolean ascendingOrder = ascending.equalsIgnoreCase("y");
        return new CountriesResponse(getCountrySummaries(sort_by.toLowerCase(), ascendingOrder));
    }

    @GetMapping("/{country}")
    public CountryDetailsResponse getCountryDetails(@PathVariable(name = "country") String country) {
        String countryName = WordUtils.capitalizeFully(country);
        return getCountryDetailsResponse(countryName);
    }

    @GetMapping("/{country}/medals")
    public CountryMedalsListResponse getCountryMedalsList(@PathVariable String country,@RequestParam(name="sortBy", required = false) String sort_by,@RequestParam(name="ascending", required = false) String ascending) {
        String countryName = WordUtils.capitalizeFully(country);
        boolean ascendingOrder = ascending.equalsIgnoreCase("y");
        return getCountryMedalsListResponse(countryName, sort_by.toLowerCase(), ascendingOrder);
    }

    private CountryMedalsListResponse getCountryMedalsListResponse(String countryName, String sortBy, boolean ascendingOrder) {
        List<GoldMedal> medalsList;
        switch (sortBy) {
            case "year":
                if(ascendingOrder) {
                    medalsList = this.goldMedalRepository.findByCountryOrderByYearAsc(countryName);
                } else {
                    medalsList = this.goldMedalRepository.findByCountryOrderByYearDesc(countryName);
                }
                break;
            case "season":
                if(ascendingOrder) {
                    medalsList = this.goldMedalRepository.findByCountryOrderBySeasonAsc(countryName);
                } else {
                    medalsList = this.goldMedalRepository.findByCountryOrderBySeasonDesc(countryName);
                }
                break;
            case "city":
                if(ascendingOrder) {
                    medalsList = this.goldMedalRepository.findByCountryOrderByCityAsc(countryName);
                } else {
                    medalsList = this.goldMedalRepository.findByCountryOrderByCityDesc(countryName);
                }
                break;
            case "name":
                if(ascendingOrder) {
                    medalsList = this.goldMedalRepository.findByCountryOrderByNameAsc(countryName);
                } else {
                    medalsList = this.goldMedalRepository.findByCountryOrderByNameDesc(countryName);
                }
                break;
            case "event":
                if(ascendingOrder) {
                    medalsList = this.goldMedalRepository.findByCountryOrderByEventAsc(countryName);
                } else {
                    medalsList = this.goldMedalRepository.findByCountryOrderByEventDesc(countryName);
                }
                break;
            default:
                medalsList = new ArrayList<>();
                break;
        }

        return new CountryMedalsListResponse(medalsList);
    }

    private CountryDetailsResponse getCountryDetailsResponse(String countryName) {
        Optional<Country> countryOptional = this.countryRepository.getByName(countryName);
        if (countryOptional.isEmpty()) {
            return new CountryDetailsResponse(countryName);
        }

        Country country = countryOptional.get();
        int goldMedalCount = this.goldMedalRepository.getMedalCountByCountry(country.getName());

        List<GoldMedal> summerWins = this.goldMedalRepository.getMedalListByCountryOnSummerOrderByYearAsc(country.getName());
        Integer numberSummerWins = summerWins.size() > 0 ? summerWins.size() : null;

        int totalSummerEvents = this.goldMedalRepository.getTotalNumberOfEventsByCountryOnSummer(country.getName());
        Float percentageTotalSummerWins = totalSummerEvents != 0 && numberSummerWins != null ? (float) summerWins.size() / totalSummerEvents : null;
        int yearFirstSummerWin = summerWins.size() > 0 ? summerWins.get(0).getYear() : null;

        List<GoldMedal> winterWins = this.goldMedalRepository.getMedalListByCountryOnWinter(country.getName());
        Integer numberWinterWins = winterWins.size() > 0 ? winterWins.size() : null;
        int totalWinterEvents = this.goldMedalRepository.getTotalNumberOfEventsByCountryOnWinterOrderByYearAsc(country.getName());
        Float percentageTotalWinterWins = totalWinterEvents != 0 && numberWinterWins != null ? (float) winterWins.size() / totalWinterEvents : null;
        int yearFirstWinterWin = winterWins.size() > 0 ? winterWins.get(0).getYear() : null;

        int numberEventsWonByFemaleAthletes = this.goldMedalRepository.getTotalNumberOfFemaleWinsByCountry(country.getName());
        int numberEventsWonByMaleAthletes = this.goldMedalRepository.getTotalNumberOfMaleWinsByCountry(country.getName());

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

    private List<CountrySummary> getCountrySummaries(String sortBy, boolean ascendingOrder) {
        List<Country> countries;
        switch (sortBy) {
            case "name":
                countries = ascendingOrder ? this.countryRepository.getByOrderByNameAsc() : this.countryRepository.getByOrderByNameDesc();
                break;
            case "gdp":
                countries = ascendingOrder ? this.countryRepository.getByOrderByGdpAsc() : this.countryRepository.getByOrderByGdpDesc();
                break;
            case "population":
                countries = ascendingOrder ? this.countryRepository.getByOrderByPopulationAsc() : this.countryRepository.getByOrderByPopulationDesc();
                break;
            case "medals":
            default:
                countries = this.countryRepository.getAllByOrderByNameAsc();
                break;
        }

        List<CountrySummary> countrySummaries = getCountrySummariesWithMedalCount(countries);

        if (sortBy.equalsIgnoreCase("medals")) {
            countrySummaries = sortByMedalCount(countrySummaries, ascendingOrder);
        }

        return countrySummaries;
    }

    private List<CountrySummary> sortByMedalCount(List<CountrySummary> countrySummaries, boolean ascendingOrder) {
        return countrySummaries.stream()
                .sorted((t1, t2) -> ascendingOrder ?
                        t1.getMedals() - t2.getMedals() :
                        t2.getMedals() - t1.getMedals())
                .collect(Collectors.toList());
    }

    private List<CountrySummary> getCountrySummariesWithMedalCount(List<Country> countries) {
        List<CountrySummary> countrySummaries = new ArrayList<>();
        for (var country : countries) {
            int goldMedalCount = this.goldMedalRepository.getMedalCountByCountry(country.getName());
            countrySummaries.add(new CountrySummary(country, goldMedalCount));
        }
        return countrySummaries;
    }
}
