package com.lab1.server;

import javax.sql.DataSource;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.lab1.server.domain.Speciality;
import com.lab1.server.domain.Vacancy;
import com.lab1.server.service.SpecialityService;
import com.lab1.server.service.VacancyService;

public class Controller implements Runnable {
    private Socket socket;
    private Server server;
    private String name;
    private VacancyService vacancyService;
    private SpecialityService specialityService;
    private Gson json;

    public Controller(Socket socket, DataSource dataSource, Server server) {
        this.socket = socket;
        this.server = server;
        this.specialityService = new SpecialityService(dataSource);
        this.vacancyService = new VacancyService(dataSource);
        this.json = new Gson();
    }

    public String getMessageType(String message) {
        return message.split("\n", 2)[0];
    }

    public String getMessageBody(String message) {
        return message.split("\n", 2)[1];
    }

    @Override
    public void run() {
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
            while (true) {
                String request = receive(bufferedReader);
                String type = getMessageType(request);
                switch (type) {
                    case "[INIT]":
                        init(getMessageBody(request));
                        break;
                    case "[GET]":
                        get(getMessageBody(request));
                        break;
                    case "[POST]":
                        post(getMessageBody(request));
                        break;
                    case "[END]":
                        end();
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receive(BufferedReader reader) {
        StringBuilder builder = new StringBuilder();
        try {
            String line;
            while (!(line = reader.readLine()).equals("")) {
                builder.append(line + "\n");
            }
        } catch (SocketException e) {
            server.removeConnectionByName(this.name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public void send(String message) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        try {
            bw.write(message+"\n\n");
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init(String body) {
        String name = body.split("\\s")[0];
        this.name = name;
        server.renameUser(Thread.currentThread(), name);
    }

    public void get(String body) {
        String[] lines = body.split("\\n");
        String domain = lines[0];
        String action = lines[1];
        String info = "";
        if (lines.length > 2)
            info = lines[2];

        String result = "";

        switch (domain) {
            case "vacancy":
                result = vacancyGetRequest(action, info);
                break;
            case "speciality":
                result = specialityGetRequest(action);
                break;
        }

        try {
            send(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void post(String body) {
        String[] lines = body.split("\\n");
        String domain = lines[0];
        String action = lines[1];
        String info = "";
        if (lines.length > 2)
            info = lines[2];

        switch (domain) {
            case "vacancy":
                vacancyPostRequest(action, info);
                break;
            case "speciality":
                specialityPostRequest(action, info);
                break;
        }
    }

    public void end() {
        server.disconnect(this.name);
    }

    public String vacancyGetRequest(String action, String conditions) {
        String res = "";
        String[] words = action.split(" ");
        switch (words[0]) {
            case "getall":
                res = json.toJson(vacancyService.getAll());
                break;
            case "getbyconditions":
                int speciality_id = 0;
                String company = "";
                String position = "";
                int age = 0;
                int salary = 0;
                String args[] = words[1].split(" ");
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
                    }
                }
                res = json.toJson(vacancyService.getAllWithConditions(speciality_id, company, position, age, salary));
                break;
        }
        return res;
    }

    public String specialityGetRequest(String action) {
        String res = "";
        String[] words = action.split(" ");
        switch (words[0]) {
            case "getall":
                res = json.toJson(specialityService.getAll());
                break;
            case "getbyid":
                res = json.toJson(specialityService.getById(Integer.parseInt(words[1])));
                break;
        }
        return res;
    }

    public String vacancyPostRequest(String action, String info) {
        String res = "";
        String[] words = action.split(" ");
        switch (words[0]) {
            case "add":
                Vacancy vacancy = json.fromJson(info, Vacancy.class);
                vacancyService.create(vacancy);
                break;
            case "delete":
                vacancyService.remove(Integer.parseInt(words[1]));
                break;
        }
        return res;
    }

    public String specialityPostRequest(String action, String info) {
        String res = "";
        String[] words = action.split(" ");
        switch (words[0]) {
            case "add":
                Speciality speciality = json.fromJson(info, Speciality.class);
                specialityService.create(speciality);
                break;
            case "delete":
                specialityService.remove(Integer.parseInt(words[1]));
                break;
        }
        return res;
    }
}
