package com.lab1.client.panel;

import com.google.gson.Gson;
import com.lab1.client.Client;
import com.lab1.client.MessageBuilder;
import com.lab1.server.domain.Speciality;
import com.lab1.server.domain.Vacancy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class MainPanel extends JPanel {

    public MainPanel(Client client) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel errorLabel = new JLabel("Інформаційне поле");
        JTextArea errorArea = new JTextArea();
        JTextArea info = new JTextArea(50, 10);

        ScrollPane scroll = new ScrollPane();
        scroll.add(info);
        scroll.setSize(300, 400);

        info.setEditable(false);
        info.setVisible(true);

        JButton button = new JButton("Список вакансій");
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    client.send(new MessageBuilder().type("[GET]").action("getall").domain("vacancy").build());
                    String message = client.receive();
                    Gson json = new Gson();
                    Vacancy[] res = json.fromJson(message, Vacancy[].class);
                    StringBuilder outputInfo = new StringBuilder();
                    for (Vacancy vacancy: res) {
                        outputInfo.append("#" + vacancy.getId() + "   " + vacancy.getSpeciality().getTitle() + "   " + vacancy.getCompany() + "    " + vacancy.getPosition() + "    " + vacancy.getLowerAgeLimit() + "-" + vacancy.getHigherAgeLimit() + "     " + "$" + vacancy.getSalary() + "\n");
                    }
                    info.setText(outputInfo.toString());
                    info.setVisible(true);
                } catch (IOException ioException) {
                    errorArea.setText("Невдалось відправити запит.");
                }
            }
        });

        JButton button2 = new JButton("Список спеціальностей");
        button2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    client.send(new MessageBuilder().type("[GET]").action("getall").domain("speciality").build());
                    String message = client.receive();
                    Gson json = new Gson();
                    Speciality[] res = json.fromJson(message, Speciality[].class);
                    StringBuilder outputInfo = new StringBuilder();
                    for (Speciality speciality: res) {
                        outputInfo.append("#" + speciality.getId() + "         " + speciality.getTitle() + "\n");
                    }
                    info.setText(outputInfo.toString());
                    info.setVisible(true);
                } catch (IOException ioException) {
                    errorArea.setText("Невдалось відправити запит.");
                }
            }
        });

        JButton button3 = new JButton("Додати спеціальність");
        button3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Speciality speciality = new Speciality();

                JDialog dialog = new JDialog(client);
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.setSize(400, 300);
                dialog.setResizable(false);

                JLabel label1 = new JLabel("Назва спеціальності");
                JTextField textField1 = new JTextField(32);

                JButton button1 = new JButton("Додати спеціальність");
                button1.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        speciality.setTitle(textField1.getText());
                        if (speciality.getTitle() == null || speciality.getTitle().equals(""))
                            return;

                        Gson json = new Gson();
                        try {
                            client.send(new MessageBuilder().type("[POST]").action("add").domain("speciality").info(json.toJson(speciality)).build());
                        } catch (IOException ioException) {
                            errorArea.setText("Не вдалось відправити запит.");
                        }
                        dialog.dispose();
                    }
                });

                JPanel panel = new JPanel();
                dialog.setModal(true);
                panel.add(label1);
                panel.add(textField1);
                panel.add(button1);
                dialog.setContentPane(panel);
                dialog.setVisible(true);
            }
        });

        JButton button4 = new JButton("Додати вакансію");
        button4.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Vacancy vacancy = new Vacancy();

                JDialog dialog = new JDialog(client);
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.setSize(400, 400);
                dialog.setResizable(false);

                JLabel label1 = new JLabel("Номер вакансії");
                JTextField textField1 = new JTextField(32);

                JLabel label2 = new JLabel("Назва компанії");
                JTextField textField2 = new JTextField(32);

                JLabel label3 = new JLabel("Позиція");
                JTextField textField3 = new JTextField(32);

                JLabel label4 = new JLabel("Верхня вікова межа");
                JTextField textField4 = new JTextField(15);

                JLabel label5 = new JLabel("Нижня вікова межа");
                JTextField textField5 = new JTextField(15);

                JLabel label6 = new JLabel("Заробітна плата");
                JTextField textField6 = new JTextField(32);

                JButton button1 = new JButton("Додати вакансію");
                button1.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        Speciality s = new Speciality();
                        s.setId(Integer.parseInt(textField1.getText()));
                        vacancy.setSpeciality(s);
                        vacancy.setCompany(textField2.getText());
                        vacancy.setPosition(textField3.getText());
                        vacancy.setHigherAgeLimit(Integer.parseInt(textField4.getText()));
                        vacancy.setLowerAgeLimit(Integer.parseInt(textField5.getText()));
                        vacancy.setSalary(Integer.parseInt(textField6.getText()));
                        dialog.dispose();

                        if (vacancy.getSpeciality().getId() == 0 ||
                                vacancy.getCompany().equals("") ||
                                vacancy.getSalary() == 0 ||
                                vacancy.getPosition().equals("")) {
                            errorArea.setText("Заповніть всі поля для створення вакансії.");
                            return;
                        }

                        Gson json = new Gson();
                        try {
                            client.send(new MessageBuilder().type("[POST]").action("add").domain("vacancy").info(json.toJson(vacancy)).build());
                        } catch (IOException ioException) {
                            errorArea.setText("Не вдалось відправити запит.");
                        }
                    }
                });

                dialog.setModal(true);
                JPanel panel = new JPanel();
                panel.add(label1);
                panel.add(textField1);
                panel.add(label2);
                panel.add(textField2);
                panel.add(label3);
                panel.add(textField3);
                panel.add(label4);
                panel.add(textField4);
                panel.add(label5);
                panel.add(textField5);
                panel.add(label6);
                panel.add(textField6);
                panel.add(button1);
                dialog.setContentPane(panel);
                dialog.setVisible(true);

            }
        });

        JButton button5 = new JButton("Видалити спеціальність");
        button5.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JDialog dialog = new JDialog(client);
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.setSize(400, 400);
                dialog.setResizable(false);

                JLabel label1 = new JLabel("Номер спеціальності");
                JTextField textField1 = new JTextField(32);

                JButton button1 = new JButton("Додати вакансію");
                button1.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        int id  = Integer.parseInt(textField1.getText());

                        if (id < 0) {
                            errorArea.setText("Заповніть всі поля для видалення спеціальності.");
                            return;
                        }

                        try {
                            client.send(new MessageBuilder().type("[POST]").action("delete " + id).domain("speciality").build());
                        } catch (IOException ioException) {
                            errorArea.setText("Не вдалось відправити запит.");
                        }
                        dialog.dispose();
                    }
                });

                dialog.setModal(true);
                JPanel panel = new JPanel();
                panel.add(label1);
                panel.add(textField1);
                panel.add(button1);
                dialog.setContentPane(panel);
                dialog.setVisible(true);
            }
        });

        JButton button6 = new JButton("Видалити вакансію");
        button6.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JDialog dialog = new JDialog(client);
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.setSize(400, 400);
                dialog.setResizable(false);
                JLabel label1 = new JLabel("Номер спеціальності");
                JTextField textField1 = new JTextField(32);
                JButton button1 = new JButton("Додати вакансію");
                button1.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        int id  = Integer.parseInt(textField1.getText());

                        if (id < 0) {
                            errorArea.setText("Заповніть всі поля для видалення спеціальності.");
                            return;
                        }

                        try {
                            client.send(new MessageBuilder().type("[POST]").action("delete " + id).domain("vacancy").build());
                        } catch (IOException ioException) {
                            errorArea.setText("Не вдалось відправити запит.");
                        }
                        dialog.dispose();
                    }
                });

                dialog.setModal(true);
                JPanel panel = new JPanel();
                panel.add(label1);
                panel.add(textField1);
                panel.add(button1);
                dialog.setContentPane(panel);
                dialog.setVisible(true);
            }
        });

        JButton button7 = new JButton("Детальний пошук вакансії");
        button7.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JDialog dialog = new JDialog(client);
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.setSize(400, 400);
                dialog.setResizable(false);
                JLabel label1 = new JLabel("Номер спеціальності");
                JTextField textField1 = new JTextField(32);
                JLabel label2 = new JLabel("Компанія");
                JTextField textField2 = new JTextField(32);
                JLabel label3 = new JLabel("Позиція");
                JTextField textField3 = new JTextField(32);
                JLabel label4 = new JLabel("Вік");
                JTextField textField4 = new JTextField(32);
                JLabel label5 = new JLabel("Заробітна плата");
                JTextField textField5 = new JTextField(32);
                JButton button1 = new JButton("Додати вакансію");
                button1.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        StringBuilder conditions = new StringBuilder();

                        if (textField1.getText() != null && !textField1.getText().equals(""))
                            conditions.append("spec=" + textField1.getText() + " ");

                        if (textField2.getText() != null && !textField2.getText().equals(""))
                            conditions.append("com=" + textField2.getText() + " ");

                        if (textField3.getText() != null && !textField3.getText().equals(""))
                            conditions.append("pos=" + textField3.getText() + " ");

                        if (textField4.getText() != null && !textField4.getText().equals(""))
                            conditions.append("age=" + textField4.getText() + " ");

                        if (textField5.getText() != null && !textField5.getText().equals(""))
                            conditions.append("sal=" + textField5.getText() + " ");

                        try {
                            client.send(new MessageBuilder().type("[GET]").action("getbyconditions").domain("vacancy").info(conditions.toString()).build());
                        } catch (IOException ioException) {
                            errorArea.setText("Не вдалось відправити запит.");
                        }
                        dialog.dispose();
                    }
                });

                dialog.setModal(true);
                JPanel panel = new JPanel();
                panel.add(label1);
                panel.add(textField1);
                panel.add(label2);
                panel.add(textField2);
                panel.add(label3);
                panel.add(textField3);
                panel.add(label4);
                panel.add(textField4);
                panel.add(label5);
                panel.add(textField5);
                panel.add(button1);
                dialog.setContentPane(panel);
                dialog.setVisible(true);
            }
        });

        JButton quitButton = new JButton("Відключитись від серверу");
        quitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    client.send(new MessageBuilder().type("[END]").build());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                client.setStartPanel();
            }
        });

        this.add(quitButton);
        this.add(button);
        this.add(button2);
        this.add(button3);
        this.add(button4);
        this.add(button5);
        this.add(button6);
        this.add(scroll);
        this.add(errorLabel);
        this.add(errorArea);
        this.setVisible(true);
    }
}
