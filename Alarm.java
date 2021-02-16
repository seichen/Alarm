import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.Timer;

public class Gui extends Thread {

    /*
     * Declare the components of the main frame
     */

    private static JFrame alarmFrame;

    private static JPanel top;
    private static JButton newAlarm;

    private static JPanel box;
    private static LinkedList<JButton> trash;
    private static LinkedList<JButton> edit;
    private static LinkedList<JLabel> alarm;

    /*
     * Declare the components of alarm maker
     */

    private static JFrame alarmOptions;

    private static JPanel panel;

    private static JLabel n = new JLabel("Name:");
    private static JTextArea nameArea;

    private static JLabel t = new JLabel("Time:");
    private static JPanel times;
    private static JComboBox h;
    private static JLabel c;
    private static JComboBox min;

    private static JLabel d = new JLabel("Days:");
    private static JPanel daysPanel;
    private static LinkedList<JCheckBox> daysList;

    private static JLabel m = new JLabel("Message:");
    private static JTextArea messageArea;

    private static JButton done;
    private static JButton doneEdit;

    /*
     * Declare rest of variables
     */

    private static LinkedList<Alarm> alarms;
    private static String fn = "C:\\Users\\Sabrina\\OneDrive - purdue.edu\\Alarm\\src\\alarms";
    private static int curEdit = 0;


    public static void main(String args[]) {

        Gui thread = new Gui();
        thread.start();

    }

    public void run() {
        alarms = new LinkedList<>();

        read();

        setMainFrame();

        setOptionFrame();

        /*
         * Set a timer to run every minute to check if it is time for an alarm
         */

        int milis = 60000;
        long time = System.currentTimeMillis();
        TTS speak = new TTS();

        java.util.Timer watch = new Timer();
        watch.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR);
                if (calendar.get(Calendar.AM_PM) == Calendar.PM) {
                    hour += 12;
                }
                int min = calendar.get(Calendar.MINUTE);
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                System.out.println("Ran at: " + hour + ":" + min + " on " + day);
                for (int i = 0; i < alarms.size(); i++) {
                    if (alarms.get(i).isTime(hour, min, day)) {
                        //ALARM
                        Sound t = new Sound(alarms.get(i).message);
                        t.start();

                        speak.setMessage(alarms.get(i).message);
                        speak.speak();
                    }
                }
            }
        }, milis - (time % milis), milis);

    }

    public void read() {

        try {
            BufferedReader br = new BufferedReader(new FileReader(fn));
            String name;
            int hour, minute;
            ArrayList<Integer> days = new ArrayList<>();
            String day;
            String message;

            while (true) {
                name = br.readLine();
                if (name == null || name.equals("eof")) {
                    return;
                }
                hour = Integer.parseInt(br.readLine());
                minute = Integer.parseInt(br.readLine());
                day = br.readLine();
                days = new ArrayList<>();
                for (int i = 0; i < day.length(); i++) {
                    days.add(Integer.parseInt(String.valueOf(day.charAt(i))));
                }
                message = br.readLine();
                alarms.add(new Alarm(name, hour, minute, days, message));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setMainFrame() {
        alarmFrame = new JFrame("Alarm");
        alarmFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        alarmFrame.setSize(400, 600);

        newAlarm = new JButton("add");
        top = new JPanel(new FlowLayout(FlowLayout.CENTER));
        top.add(newAlarm);

        trash = new LinkedList<>();
        edit = new LinkedList<>();
        alarm = new LinkedList<>();
        box = new JPanel(new GridLayout(0, 3));

        alarmFrame.getContentPane().add(BorderLayout.NORTH, top);
        alarmFrame.getContentPane().add(BorderLayout.SOUTH, box);

        alarmFrame.setVisible(true);

        for (Alarm a : alarms) {
            alarm.add(new JLabel(a.name));
            edit.add(new JButton("edit"));
            edit.get(edit.size() - 1).addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    editAlarmButton(evt);
                }
            });
            trash.add(new JButton("delete"));
            trash.get(trash.size() - 1).addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    deleteButton(evt);
                }
            });
            box.add(alarm.get(alarm.size() - 1));
            box.add(edit.get(edit.size() - 1));
            box.add(trash.get(trash.size() - 1));
            box.revalidate();
            box.repaint();
        }

        newAlarm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newAlarmButton(evt);
            }
        });
    }

    private void editAlarmButton(java.awt.event.ActionEvent evt) {

        for (int i = 0; i < alarms.size(); i++) {
            if (evt.getSource().equals(edit.get(i))) {
                curEdit = i;
                nameArea.setText(alarms.get(i).name);
                h.setSelectedIndex(alarms.get(i).hour);
                min.setSelectedIndex(alarms.get(i).minute);
                for (int j = 0; j < daysList.size(); j++) {
                    if (alarms.get(i).days.contains(j + 1)) {
                        daysList.get(j).setSelected(true);
                    } else {
                        daysList.get(j).setSelected(false);
                    }
                }
                messageArea.setText(alarms.get(i).message);
                alarmOptions.setSize(400, 600);
                panel.remove(done);
                panel.add(doneEdit);
                alarmOptions.setVisible(true);
            }
        }

    }

    private void deleteButton(java.awt.event.ActionEvent evt) {
        for (int i = 0; i < alarms.size(); i++) {
            if (evt.getSource().equals(trash.get(i))) {
                box.remove(trash.get(i));
                box.remove(edit.get(i));
                box.remove(alarm.get(i));
                alarms.remove(i);
                edit.remove(i);
                trash.remove(i);
                alarm.remove(i);
                box.revalidate();
                box.repaint();
                write(alarms);
            }
        }
    }

    public void write(LinkedList<Alarm> alarms) {

        try {

            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(fn)));
            String days;

            for (Alarm a : alarms) {
                bw.write(a.name + "\n");
                bw.write(String.valueOf(a.hour) + "\n");
                bw.write(String.valueOf(a.minute) + "\n");
                days = "";
                for (Integer d : a.days) {
                    days = days + d;
                }
                bw.write(days + "\n");
                bw.write(a.message + "\n");
            }
            bw.write("eof");
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void newAlarmButton(java.awt.event.ActionEvent evt) {

        nameArea.setText("");
        h.setSelectedIndex(0);
        min.setSelectedIndex(0);
        for (JCheckBox jCheckBox : daysList) {
            jCheckBox.setSelected(false);
        }
        messageArea.setText("");
        alarmOptions.setSize(400, 600);
        alarmOptions.setVisible(true);

    }

    public void setOptionFrame() {
        alarmOptions = new JFrame("Create your alarm");
        alarmFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        alarmFrame.setSize(400, 600);

        panel = new JPanel(new GridLayout(0,2));

        nameArea = new JTextArea();
        nameArea.setLineWrap(true);

        times = new JPanel(new GridLayout(1,3));
        LinkedList<String> hours = new LinkedList<>();
        LinkedList<String> minutes = new LinkedList<>();
        for (int i = 0; i < 24; i++) {
            if (i < 10) {
                hours.add("0" + i);
            } else {
                hours.add("" + i);
            }
        }
        for (int i = 0; i < 60; i++) {
            if (i < 10) {
                minutes.add("0" + i);
            } else {
                minutes.add("" + i);
            }
        }
        c = new JLabel(" : ");
        h = new JComboBox(hours.toArray());
        min = new JComboBox(minutes.toArray());
        times.add(h);
        times.add(c);
        times.add(min);

        daysPanel = new JPanel(new GridLayout(2,4));
        daysList = new LinkedList<>();
        daysList.add(new JCheckBox("Sun"));
        daysList.add(new JCheckBox("Mon"));
        daysList.add(new JCheckBox("Tue"));
        daysList.add(new JCheckBox("Wed"));
        daysList.add(new JCheckBox("Thu"));
        daysList.add(new JCheckBox("Fri"));
        daysList.add(new JCheckBox("Sat"));
        for (int i = 0; i < daysList.size(); i++) {
            daysPanel.add(daysList.get(i));
        }

        messageArea = new JTextArea();
        messageArea.setLineWrap(true);

        done = new JButton("Done");

        panel.add(n);
        panel.add(nameArea);
        panel.add(t);
        panel.add(times);
        panel.add(d);
        panel.add(daysPanel);
        panel.add(m);
        panel.add(messageArea);
        panel.add(done);

        alarmOptions.add(panel);

        done.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doneButton(evt);
            }
        });

        doneEdit = new JButton("Edit");

        doneEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doneEditButton(evt);
            }
        });
    }

    public void doneEditButton(java.awt.event.ActionEvent evt) {
        boolean equals = false;
        if (!nameArea.getText().equals("") && !messageArea.getText().equals("")) {
            for (int i = 0; i < alarms.size(); i++) {
                if (nameArea.getText().equals(alarms.get(i).name) && i != curEdit){
                    equals = true;
                    JOptionPane.showMessageDialog(null, "Cannot have alarm of same name");
                }
            }
            if (!equals) {
                if (daysList.get(0).isSelected() || daysList.get(1).isSelected() || daysList.get(2).isSelected()
                        || daysList.get(3).isSelected() || daysList.get(4).isSelected() ||
                        daysList.get(5).isSelected() || daysList.get(6).isSelected()) {
                    if (!messageArea.getText().equals("")) {
                        ArrayList<Integer> days = new ArrayList<>();
                        if (daysList.get(0).isSelected()) {
                            days.add(1);
                        }
                        if (daysList.get(1).isSelected()) {
                            days.add(2);
                        }
                        if (daysList.get(2).isSelected()) {
                            days.add(3);
                        }
                        if (daysList.get(3).isSelected()) {
                            days.add(4);
                        }
                        if (daysList.get(4).isSelected()) {
                            days.add(5);
                        }
                        if (daysList.get(5).isSelected()) {
                            days.add(6);
                        }
                        if (daysList.get(6).isSelected()) {
                            days.add(7);
                        }
                        alarms.get(curEdit).edit(nameArea.getText(), Integer.parseInt((String) h.getSelectedItem()), Integer.parseInt((String) min.getSelectedItem()), days, messageArea.getText());
                        alarmOptions.setVisible(false);
                        alarm.get(curEdit).setText(nameArea.getText());
                        box.revalidate();
                        box.repaint();
                        write(alarms);
                        panel.remove(doneEdit);
                        panel.add(done);
                    }
                }
            }
        }
    }

    public void doneButton(java.awt.event.ActionEvent evt) {
        boolean equals = false;
        if (!nameArea.getText().equals("") && !messageArea.getText().equals("")) {
            for (int i = 0; i < alarms.size(); i++) {
                if (nameArea.getText().equals(alarms.get(i).name)){
                    equals = true;
                    JOptionPane.showMessageDialog(null, "Cannot have alarm of same name");
                }
            }
            if (!equals) {
                if (daysList.get(0).isSelected() || daysList.get(1).isSelected() || daysList.get(2).isSelected()
                        || daysList.get(3).isSelected() || daysList.get(4).isSelected() ||
                        daysList.get(5).isSelected() || daysList.get(6).isSelected()) {
                    if (!messageArea.getText().equals("")) {
                        ArrayList<Integer> days = new ArrayList<>();
                        if (daysList.get(0).isSelected()) {
                            days.add(1);
                        }
                        if (daysList.get(1).isSelected()) {
                            days.add(2);
                        }
                        if (daysList.get(2).isSelected()) {
                            days.add(3);
                        }
                        if (daysList.get(3).isSelected()) {
                            days.add(4);
                        }
                        if (daysList.get(4).isSelected()) {
                            days.add(5);
                        }
                        if (daysList.get(5).isSelected()) {
                            days.add(6);
                        }
                        if (daysList.get(6).isSelected()) {
                            days.add(7);
                        }
                        alarms.add(new Alarm(nameArea.getText(), Integer.parseInt((String) h.getSelectedItem()), Integer.parseInt((String) min.getSelectedItem()), days, messageArea.getText()));
                        alarmOptions.setVisible(false);
                        alarm.add(new JLabel(nameArea.getText()));
                        edit.add(new JButton("edit"));
                        edit.get(edit.size() - 1).addActionListener(new java.awt.event.ActionListener() {
                            public void actionPerformed(java.awt.event.ActionEvent evt) {
                                editAlarmButton(evt);
                            }
                        });
                        trash.add(new JButton("delete"));
                        trash.get(trash.size() - 1).addActionListener(new java.awt.event.ActionListener() {
                            public void actionPerformed(java.awt.event.ActionEvent evt) {
                                deleteButton(evt);
                            }
                        });
                        box.add(alarm.get(alarm.size() - 1));
                        box.add(edit.get(edit.size() - 1));
                        box.add(trash.get(trash.size() - 1));
                        box.revalidate();
                        box.repaint();
                        write(alarms);
                    }
                }
            }
        }
    }
}
