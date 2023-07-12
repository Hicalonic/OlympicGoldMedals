package com.codecademy.goldmedal.respositories;

import com.codecademy.goldmedal.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@EnableJpaRepositories
public interface CountryRepository extends JpaRepository<Country,Long> {


    public Optional<Country> getByName(String name);

    public List<Country> getByOrderByNameAsc();
    public List<Country> getByOrderByNameDesc();

    public List<Country> getByOrderByGdpAsc();
    public List<Country> getByOrderByGdpDesc();
    public List<Country> getByOrderByPopulationAsc();
    public List<Country> getByOrderByPopulationDesc();
    public List<Country> getAllByOrderByNameAsc();
}
