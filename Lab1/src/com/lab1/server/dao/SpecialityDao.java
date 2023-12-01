package com.lab1.server.dao;

import com.lab1.server.domain.Speciality;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SpecialityDao {
    private DataSource dataSource;

    public SpecialityDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void removeById(int id) {
        try(Connection connection = dataSource.getConnection();) {
            String sql = "DELETE FROM specialities WHERE id = ?";
            String sql2 = "DELETE FROM vacancies WHERE speciality_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            statement.execute();
            statement = connection.prepareStatement(sql2);
            statement.setInt(1, id);
            statement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public Speciality findById(int id) {
        Speciality speciality = new Speciality();
        try(Connection connection = dataSource.getConnection();) {
            String sql = "SELECT * FROM specialities WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            result.next();
            speciality.setId(result.getInt(1));
            speciality.setTitle(result.getString(2));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return speciality;
    }

    public List<Speciality> findAll() {
        List<Speciality> specialities = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();) {
            String sql = "SELECT * FROM specialities";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Speciality speciality = new Speciality();
                speciality.setId(result.getInt(1));
                speciality.setTitle(result.getString(2));
                specialities.add(speciality);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return specialities;
    }

    public void create(Speciality speciality) {
        try(Connection connection = dataSource.getConnection();) {
            String sql = "INSERT INTO specialities (id, title) VALUES (nextval('specialities_id_seq'), ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, speciality.getTitle());
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
