package com.lab1.server.service;

import com.lab1.server.dao.VacancyDao;
import com.lab1.server.domain.Speciality;
import com.lab1.server.domain.Vacancy;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

public class VacancyService {

    private VacancyDao vacancyDao;

    public VacancyService(DataSource dataSource) {
        this.vacancyDao = new VacancyDao(dataSource);
    }

    public void create(Vacancy vacancy) {
        this.vacancyDao.create(vacancy);
    }

    public void remove(int id) {
        this.vacancyDao.removeById(id);
    }

    public List<Vacancy> getAll() {
        return this.vacancyDao.findAll();
    }

    public Vacancy getById(int id) {
        return this.vacancyDao.findById(id);
    }

    public List<Vacancy> getAllWithConditions(int speciality_id, String company, String position, int age, int salary) {
        return this.vacancyDao.findWithConditions(speciality_id, company, position, age, salary);
    }
}
