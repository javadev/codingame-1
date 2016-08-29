import java.util.*;
import java.io.*;
import java.math.*;

class Solution {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        final int NUMBER_OF_DATA_POINTS = in.nextInt(); // the number of temperatures to analyse
        in.nextLine();
        final String temperaturesAsString = in.nextLine(); // the n temperatures expressed as integers ranging from -273 to 5526

        if (NUMBER_OF_DATA_POINTS == 0) {
            System.out.println(0); // fail fast
        }

        List<String> listOfTemperaturesAsStrings = Arrays.asList(temperaturesAsString.split("\\s+"));
        List<Integer> listOfIntegers = new ArrayList<>();
        for (String temperatureAsString : listOfTemperaturesAsStrings) {
            listOfIntegers.add(Integer.parseInt(temperatureAsString));
        }

        int temperatureClosestToZero = 10000;
        for (int i : listOfIntegers) {
            if (absoluteValue(i) == absoluteValue(temperatureClosestToZero)
                    && (i != temperatureClosestToZero)) {
                temperatureClosestToZero = absoluteValue(temperatureClosestToZero);
            } else if (absoluteValue(i) < absoluteValue(temperatureClosestToZero)) {
                temperatureClosestToZero = i;
            }
        }
        System.out.println(temperatureClosestToZero);
    }

    public static int absoluteValue(int i) {
        if (i >= 0) {
            return i;
        } else {
            return i * (-1);
        }
    }
}