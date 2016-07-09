import java.util.*;

@SuppressWarnings("all")

class Solution {
    private static String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ?";

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int letterWidth = in.nextInt();
        int letterHeight = in.nextInt();
        in.nextLine(); // blank line
        String textToPrint = in.nextLine().toUpperCase();

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < letterHeight; i++) {
            String nextRow = in.nextLine();
            stringBuilder.append(nextRow);
        }
        String alphabetArt = stringBuilder.toString();

        Map<String, ASCIILetter> letterToArtMap = parseInputArt(letterWidth,
                                                                letterHeight,
                                                                ALPHABET,
                                                                alphabetArt);
        System.out.println(stringAsAsciiArt(textToPrint, letterToArtMap, letterHeight));
    }

    static Map<String, ASCIILetter> parseInputArt(int letterWidth,
                                                  int letterHeight,
                                                  String alphabetOfCharacters,
                                                  String alphabetArt) {
        Map<String, ASCIILetter> letterToArtMap = new HashMap<>();

        List<String> alphabetAsList = new ArrayList<>(Arrays.asList(alphabetOfCharacters.split("")));
        for (String letter : alphabetAsList) {
            letterToArtMap.put(letter, new ASCIILetter(letter));
        }

        int rowLength = letterWidth * alphabetOfCharacters.length();
        for (int rowNumber = 0; rowNumber < letterHeight; rowNumber++) {
            for (int letterNumber = 0; letterNumber < alphabetOfCharacters.length(); letterNumber++) {
                int substringIndexStart = rowLength * rowNumber + letterNumber * letterWidth;
                String rowToAddToArt = alphabetArt.substring(substringIndexStart, substringIndexStart + letterWidth);
                String letter = alphabetAsList.get(letterNumber);
                ASCIILetter letterArt = letterToArtMap.get(letter);
                letterArt.addRow(rowToAddToArt);
                letterToArtMap.remove(letter);
                letterToArtMap.put(letter, letterArt);
            }
        }
        return letterToArtMap;
    }

    static String stringAsAsciiArt(String stringToPrint,
                                   Map<String, ASCIILetter> letterToMapArt,
                                   int letterHeight) {
        List<String> stringToPrintAsList = new ArrayList<>(Arrays.asList(stringToPrint.split("")));
        StringBuilder stringBuilder = new StringBuilder();
        for (int rowNumber = 0; rowNumber < letterHeight; rowNumber++) {
            for (String letter : stringToPrintAsList) {
                ASCIILetter asciiArtForThisLetter = letterToMapArt.get(letter);
                String rowArt;
                if (asciiArtForThisLetter == null) {
                    rowArt = letterToMapArt.get("?").getArtByRow(rowNumber);
                } else {
                    rowArt = letterToMapArt.get(letter).getArtByRow(rowNumber);
                }
                stringBuilder.append(rowArt);
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}

class ASCIILetter {
    private String _characterCode;
    private List<String> _artByRow = new ArrayList<>();

    ASCIILetter(String characterCode) {
        _characterCode = characterCode;
    }

    void addRow(String newRow) {
        _artByRow.add(newRow);
    }

    String getArtByRow(int rowIndex) {
        return _artByRow.get(rowIndex);
    }

    String getArt() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String row : _artByRow) {
            stringBuilder.append(row).append("\n");
        }
        return stringBuilder.toString();
    }

    String getCharacterCode() {
        return _characterCode;
    }
}