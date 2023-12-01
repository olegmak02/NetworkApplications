package com.lab1.server.service;

import com.lab1.server.dao.SpecialityDao;
import com.lab1.server.domain.Speciality;

import javax.sql.DataSource;
import java.util.List;

public class SpecialityService {
    private SpecialityDao specialityDao;

    public SpecialityService(DataSource dataSource) {
        this.specialityDao = new SpecialityDao(dataSource);
    }

    public void remove(int id) {
        this.specialityDao.removeById(id);
    }

    public Speciality getById(int id) {
        return this.specialityDao.findById(id);
    }

    public List<Speciality> getAll() {
        return this.specialityDao.findAll();
    }

    public void create(Speciality speciality) {
        this.specialityDao.create(speciality);
    }
}
