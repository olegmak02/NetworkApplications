package com.lab1.server;

import java.io.IOException;
import java.net.ServerSocket;

import com.lab1.server.domain.Speciality;
import com.lab1.server.domain.Vacancy;
import com.lab1.server.service.SpecialityService;
import com.lab1.server.service.VacancyService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.*;

public class Server extends Thread {
    private Map<String, Thread> threads = new HashMap<>();
    private int currentId = 0;
    private int port;
    private DataSource dataSource;
    private SpecialityService specialityService;
    private VacancyService vacancyService;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/lab1");
        config.setDriverClassName("org.postgresql.Driver");
        config.setUsername("postgres");
        config.setPassword("pass");
        config.setAutoCommit(true);
        config.setMaximumPoolSize(10);
        dataSource = new HikariDataSource(config);
        this.specialityService = new SpecialityService(dataSource);
        this.vacancyService = new VacancyService(dataSource);

        while (true) {
            try {
                Controller handler = new Controller(serverSocket.accept(), dataSource, this);
                Thread thread = new Thread(handler);
                threads.put(String.valueOf(currentId++), thread);
                thread.start();
                System.out.println("New connection " + currentId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addVacancy(Vacancy vacancy) {
        vacancy.setSpeciality(specialityService.getById(vacancy.getSpeciality().getId()));
        vacancyService.create(vacancy);
    }

    public void addSpeciality(Speciality speciality) {
        specialityService.create(speciality);
    }

    public void disconnect(String username) {
        threads.remove(username);
    }

    public void removeVacancy(int id) {
        vacancyService.remove(id);
    }

    public void removeSpeciality(int id) {
        specialityService.remove(id);
    }

    public List<Vacancy> listVacancy(String[] args) {
        if (args.length == 2) {
            return vacancyService.getAll();
        } else {
            int speciality_id = 0;
            String company = "";
            String position = "";
            int age = 0;
            int salary = 0;

            for (int i = 2; i < args.length; i++) {
                String[] arg = args[i].split("=");
                switch (arg[0]) {
                    case "spec":
                        speciality_id = Integer.parseInt(arg[1]);
                        break;
                    case "com":
                        company = arg[1];
                        break;
                    case "pos":
                        position = arg[1];
                        break;
                    case "age":
                        age = Integer.parseInt(arg[1]);
                        break;
                    case "sal":
                        salary = Integer.parseInt(arg[1]);
                        break;
                    default:
                        return new ArrayList<>();
                }
            }
            return vacancyService.getAllWithConditions(speciality_id, company, position, age, salary);
        }
    }

    public List<Speciality> listSpeciality() {
        return specialityService.getAll();
    }

    public void renameUser(Thread thread, String username) {
        Optional<Map.Entry<String, Thread>> thr = threads.entrySet().stream().filter(e -> e.getValue() == thread).findFirst();
        if (!thr.isPresent())
            return;
        threads.remove(thr.get().getKey());
        threads.put(username, thread);
    }

    public void removeConnectionByName(String name) {
        this.threads.remove(name);
    }
}
