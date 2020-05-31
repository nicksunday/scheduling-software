package GUI.Reporting;

import javafx.event.ActionEvent;
import javafx.scene.text.Text;

import Database.Actions.Report;

public class Reporting {
    public Text reportTxt;

    public void uniqueAppts(ActionEvent actionEvent) {
        reportTxt.setText(Report.uniqueApptTypeByMonth());
    }

    public void activeCustomers(ActionEvent actionEvent) {
        reportTxt.setText(Report.activeCustomers());
    }

    public void activeUsers(ActionEvent actionEvent) {
        reportTxt.setText(Report.activeUsers());
    }
}
