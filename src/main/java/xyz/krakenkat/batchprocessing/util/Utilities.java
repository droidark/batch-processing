package xyz.krakenkat.batchprocessing.util;

public class Utilities {

    public static String formatText(String text) {
        return text.replace("Â¿", "¿")
                .replace("Â¡", "¡")
                .replace("Â´", "'")
                .replace("Ã±", "ñ")
                .replace("Ã‘", "Ñ")
                .replace("Ã¡", "á")
                .replace("Ã©", "é")
                .replace("Ã\u00AD", "í")
                .replace("Ã³", "ó")
                .replace("Ãº", "ú")
                .replace("Ã\u0081", "Á")
                .replace("Ã‰", "É")
                .replace("Ã\u008D", "Í")
                .replace("Ã“", "Ó")
                .replace("Ãš", "Ú")
                .replace("â€¦", "...")
                .replace("â€“", "\"")
                .trim();
    }

    private Utilities() {}
}
