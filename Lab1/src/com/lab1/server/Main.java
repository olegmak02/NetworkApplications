package com.lab1.server;

import com.google.gson.Gson;
import com.lab1.server.domain.Speciality;
import com.lab1.server.domain.Vacancy;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    private final static int PORT = 8888;
    private static Server server;
    private static final Gson json = new Gson();

    public static void main(String[] args) throws IOException {
        server = new Server(PORT);
        server.start();

        Scanner scanner = new Scanner(System.in);
        String line;
        while(true) {
            line = scanner.nextLine().trim();
            String[] tokens = line.split("\\s+");
            switch (tokens[0]) {
                case "disconnect":
                    disconnect(tokens);
                    break;
                case "add":
                    add(tokens);
                    break;
                case "remove":
                    remove(tokens);
                    break;
                case "list":
                    list(tokens);
                    break;
                case "help":
                    help();
                    break;
                default:
                    System.out.println("Unknown command. Type help to get available commands.");
                    break;
            }
        }
    }

    public static void disconnect(String[] args) {
        try {
            server.disconnect(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Unable to parse id of user to disconnect.");
        }
    }

    public static void add(String[] args) {
        switch (args[1]) {
            case "vacancy":
                Vacancy vacancy = new Vacancy();
                Speciality spec = new Speciality();
                spec.setId(Integer.parseInt(args[2]));
                vacancy.setSpeciality(spec);
                vacancy.setCompany(args[3]);
                vacancy.setPosition(args[4]);
                vacancy.setHigherAgeLimit(Integer.parseInt(args[5]));
                vacancy.setLowerAgeLimit(Integer.parseInt(args[6]));
                vacancy.setSalary(Integer.parseInt(args[7]));
                server.addVacancy(vacancy);
                break;
            case "speciality":
                if (args.length < 3) {
                    help();
                    return;
                }

                Speciality speciality = new Speciality();
                speciality.setTitle(args[2]);
                server.addSpeciality(speciality);
                break;
            default:
                System.out.println("Command add have one of two arguments: 'vacancy' or 'speciality'");
        }
    }

    public static void remove(String[] args) {
        switch (args[1]) {
            case "vacancy":
                server.removeVacancy(Integer.parseInt(args[2]));
                break;
            case "speciality":
                server.removeSpeciality(Integer.parseInt(args[2]));
                break;
        }
    }

    public static void list(String[] args) {
        if (args.length < 2) {
            help();
            return;
        }

        switch (args[1]) {
            case "vacancies":
                System.out.println("id    #speciality    company    position    higher age    lower age    salary");
                for (Vacancy vacancy: server.listVacancy(args)) {
                    System.out.println(vacancy.getId() + "   " +
                            vacancy.getSpeciality().getId() + "     " +
                            vacancy.getCompany() + "   " +
                            vacancy.getPosition() + "   " +
                            vacancy.getHigherAgeLimit() + "   " +
                            vacancy.getLowerAgeLimit() + "   " +
                            vacancy.getSalary());
                };
                break;
            case "specialities":
                System.out.println("id    title");
                for (Speciality speciality: server.listSpeciality()) {
                    System.out.println(speciality.getId() + "   " + speciality.getTitle());
                }
                break;
        }
    }

    public static void help() {
        String info = "You can use following commands to work with server:\n" +
                "'disconnect [id]', where id - id of client that you want to disconnect\n" +
                "...";
        System.out.println(info);
    }
}
