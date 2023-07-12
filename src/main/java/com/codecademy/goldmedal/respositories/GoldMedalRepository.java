package com.codecademy.goldmedal.respositories;


import com.codecademy.goldmedal.model.GoldMedal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@EnableJpaRepositories
public interface GoldMedalRepository extends JpaRepository<GoldMedal,Long> {

    @Query("SELECT COUNT(gm) FROM GoldMedal gm WHERE gm.country = ?1")
    public int getMedalCountByCountry(String country);

    @Query("SELECT gm FROM GoldMedal gm WHERE gm.country = ?1 AND gm.season = 'Summer' GROUP BY gm.year, gm.id ORDER BY gm.year ASC")
    public List<GoldMedal> getMedalListByCountryOnSummerOrderByYearAsc(String country);

    @Query("SELECT COUNT(DISTINCT gm.event) FROM GoldMedal gm WHERE gm.country = ?1 AND gm.season = 'Summer'")
    public int getTotalNumberOfEventsByCountryOnSummer(String country);

    @Query("SELECT gm FROM GoldMedal gm WHERE gm.country = ?1 AND gm.season = 'Winter'")
    public List<GoldMedal> getMedalListByCountryOnWinter(String country);

    @Query("SELECT  COUNT(DISTINCT gm.event) FROM GoldMedal gm WHERE gm.country = ?1 AND gm.season = 'Winter' GROUP BY gm.year, gm.id  ORDER BY gm.year ASC")
    public int getTotalNumberOfEventsByCountryOnWinterOrderByYearAsc(String country);

    @Query("SELECT COUNT(gm.name) FROM GoldMedal gm WHERE gm.country = ?1 AND gm.gender = 'Women'")
    public int getTotalNumberOfFemaleWinsByCountry(String country);


    @Query("SELECT COUNT(gm.name) FROM GoldMedal gm WHERE gm.country = ?1 AND gm.gender = 'Men'")
    public int getTotalNumberOfMaleWinsByCountry(String country);



    public List<GoldMedal> findByCountryOrderByYearAsc(String country);
    public List<GoldMedal> findByCountryOrderByYearDesc(String country);
    public List<GoldMedal> findByCountryOrderBySeasonAsc(String country);
    public List<GoldMedal> findByCountryOrderBySeasonDesc(String country);
    public List<GoldMedal> findByCountryOrderByCityAsc(String country);
    public List<GoldMedal> findByCountryOrderByCityDesc(String country);
    public List<GoldMedal> findByCountryOrderByNameAsc(String country);
    public List<GoldMedal> findByCountryOrderByNameDesc(String country);
    public List<GoldMedal> findByCountryOrderByEventAsc(String country);
    public List<GoldMedal> findByCountryOrderByEventDesc(String country);

}
