package com.lab1.server.dao;

import com.lab1.server.domain.Vacancy;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VacancyDao {
    private SpecialityDao specialityDao;
    private DataSource dataSource;

    public VacancyDao(DataSource dataSource) {
        this.dataSource = dataSource;
        specialityDao = new SpecialityDao(dataSource);
    }

    public List<Vacancy> findAll() {
        List<Vacancy> vacancies = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();) {
            String sql = "SELECT * FROM vacancies";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Vacancy vacancy = new Vacancy();
                vacancy.setId(result.getInt(1));
                vacancy.setSpeciality(specialityDao.findById(result.getInt(2)));
                vacancy.setCompany(result.getString(3));
                vacancy.setPosition(result.getString(4));
                vacancy.setHigherAgeLimit(result.getInt(5));
                vacancy.setLowerAgeLimit(result.getInt(6));
                vacancy.setSalary(result.getInt(7));
                vacancies.add(vacancy);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vacancies;
    }
    
    public void create(Vacancy vacancy) {
        try(Connection connection = dataSource.getConnection();) {
            String sql = "INSERT INTO vacancies (id, speciality_id, company, position, higher_age_limit, lower_age_limit, salary) " +
                    "VALUES (nextval('vacancies_id_seq'), ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, vacancy.getSpeciality().getId());
            statement.setString(2, vacancy.getCompany());
            statement.setString(3, vacancy.getPosition());
            statement.setInt(4, vacancy.getHigherAgeLimit());
            statement.setInt(5, vacancy.getLowerAgeLimit());
            statement.setInt(6, vacancy.getSalary());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeById(int id) {
        try(Connection connection = dataSource.getConnection();) {
            String sql = "DELETE FROM vacancies WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Vacancy findById(int id) {
        Vacancy vacancy = new Vacancy();
        try(Connection connection = dataSource.getConnection();) {
            String sql = "SELECT * FROM vacancies WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            result.next();
            vacancy.setId(result.getInt(1));
            vacancy.setSpeciality(specialityDao.findById(result.getInt(2)));
            vacancy.setCompany(result.getString(3));
            vacancy.setPosition(result.getString(4));
            vacancy.setHigherAgeLimit(result.getInt(5));
            vacancy.setLowerAgeLimit(result.getInt(6));
            vacancy.setSalary(result.getInt(7));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vacancy;
    }

    public List<Vacancy> findWithConditions(int speciality_id, String company, String position, int age, int salary) {
        List<Vacancy> vacancies = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();) {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM vacancies WHERE company LIKE concat(concat('%', ?), '%') AND position LIKE concat(concat('%', ?), '%') ");
            if (speciality_id != 0)
                sql.append(" AND speciality_id = ").append(speciality_id);

            if (age != 0)
                sql.append(" AND ").append(age).append(" <= higher_age_limit AND ").append(age).append(" >= lower_age_limit ");

            if (salary != 0)
                sql.append(" AND salary = ").append(salary);

            PreparedStatement statement = connection.prepareStatement(sql.toString());
            statement.setString(1, company);
            statement.setString(2, position);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Vacancy vacancy = new Vacancy();
                vacancy.setId(result.getInt(1));
                vacancy.setSpeciality(specialityDao.findById(result.getInt(2)));
                vacancy.setCompany(result.getString(3));
                vacancy.setPosition(result.getString(4));
                vacancy.setHigherAgeLimit(result.getInt(5));
                vacancy.setLowerAgeLimit(result.getInt(6));
                vacancy.setSalary(result.getInt(7));
                vacancies.add(vacancy);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vacancies;
    }
}
