import java.util.ArrayList;

public class Alarm {
    String name;
    int hour, minute;
    ArrayList<Integer> days;
    String message;

    public Alarm(String n, int h, int m, ArrayList<Integer> d, String me) {
        name = n;
        hour = h;
        minute = m;
        days = d;
        message = me;
    }

    public boolean isTime(int hour, int minute, int day) {
        for (int i = 0; i < days.size(); i++) {
            if (days.get(i) == day) {
                if (hour == this.hour && minute == this.minute) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean equals(Alarm a) {
        if (a.name.equals(name) && a.hour == hour && a.minute == minute && a.message.equals(message)) {
            if (days.size() == a.days.size()) {
                for (int i = 0; i < days.size(); i++) {
                    if (!days.get(i).equals(a.days.get(i))) {
                        return false;
                    }
                    if (i == (days.size() - 1)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void edit(String n, int h, int m, ArrayList<Integer> d, String me) {
        name = n;
        hour = h;
        minute = m;
        days = d;
        message = me;
    }
}
