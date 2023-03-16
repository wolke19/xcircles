import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Path file = Paths.get("input/test9.IGC");
        int[] lr = lrTurns(movementArray(arrayErsteller(file)), interval(file),38);
        System.out.println("L/R: " + lr[0] + "/" + lr[1]);
    }
    public static ArrayList<String> arrayErsteller (Path file) throws IOException {
        ArrayList<String> locationArray = new ArrayList<String>();
        Scanner fileScanner = new Scanner(file);

        for (int i = 0; fileScanner.hasNext(); i++){
            String line = fileScanner.nextLine();
            if (line.charAt(0) == 'B'){
               locationArray.add(line);
            }
        }


        fileScanner.close();
        return locationArray;

    }
    public static int interval(Path file) throws IOException {
        Scanner igcScanner = new Scanner(file);
        int[] intervalArray = new int[2];
        int counter = 0;
        for (int i = 0; i < 200; i++){
            String line = igcScanner.nextLine();
            if (i >=198){
                    intervalArray[counter] = Integer.parseInt(line.substring(5,7));
                    counter++;
            }
        }
        igcScanner.close();
        return intervalArray[1] - intervalArray[0];
    }
    public static int[][] movementArray(ArrayList<String> locationArray) throws IOException {

        int vectorArray[][] = new int[locationArray.size()][2];


        for (int i = 0; i < vectorArray.length - 1; i++) {

            String oldLine = locationArray.get(i);
            String line = locationArray.get(i+1);

            vectorArray[i][0] = Integer.parseInt(line.substring(7, 14)) - Integer.parseInt(oldLine.substring(7, 14));
            vectorArray[i][1] = Integer.parseInt(line.substring(15, 23)) - Integer.parseInt(oldLine.substring(15, 23));

//              Drehrichtung unter Beachtung der N/S E/W Koordinatenangaben berichtigen
            char northSouth = line.charAt(14);
            char westEast = line.charAt(23);
            if (northSouth == 'S') {
                vectorArray[i][0] = -vectorArray[i][0];
            }
            if (westEast == 'W'){
                vectorArray[i][1] = -vectorArray[i][1];
            }
        }
        return vectorArray;
    }
    public static int[] lrTurns(int[][] v, int interval, int turnDuration){
        int[] leftRightTurns = new int[2];
        int timeCounter = 0;
        double angleCounter = 0;

        for (int i = 1; i < v.length; i++){
            angleCounter += richtungsAenderung(v[i-1][0], v[i-1][1], v[i][0], v[i][1]);
            timeCounter ++;
            if (timeCounter > (turnDuration / interval) && angleCounter < 360 && angleCounter > -360){
                angleCounter = 0;
                timeCounter = 0;
            }
            else if (angleCounter >= 360){
                leftRightTurns[1] ++;
                angleCounter -= 360;
                timeCounter = 0;
            } else if (angleCounter <= -360) {
                leftRightTurns[0] ++;
                angleCounter += 360;
                timeCounter = 0;
            }
        }
        return leftRightTurns;
    }
    public static double richtungsAenderung (int n1, int e1, int n2, int e2){
        double kreuzprodukt = n1 * e2 - e1 * n2;
        if (kreuzprodukt == 0) {
            return 0;
        }
        double skalarprodukt = n1 * n2 + e1 * e2;
        double betragV1 = Math.sqrt(n1 * n1 + e1 * e1);
        double betragV2 = Math.sqrt(n2 * n2 + e2 * e2);

        double richtunsAenderung = Math.toDegrees(Math.acos((skalarprodukt / (betragV1 * betragV2))));
        if (kreuzprodukt < 0) {
            richtunsAenderung *= -1;
        }
        return richtunsAenderung;
    }
}