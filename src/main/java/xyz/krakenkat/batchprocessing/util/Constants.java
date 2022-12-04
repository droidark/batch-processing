package xyz.krakenkat.batchprocessing.util;

public final class Constants {

    public static final String DELIMITER = "|";

    public static final String ISBN = "000-0000000000";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String FILE_EXTENSION = ".csv";

    public static final String[] TITLES_HEADER = {
            "PUBLISHER",
            "NAME",
            "KEY",
            "COVER",
            "DEMOGRAPHY",
            "FORMAT",
            "TYPE",
            "FREQUENCY",
            "STATUS",
            "TOTAL ISSUES",
            "RELEASE DATE",
            "GENRES",
            "AUTHORS"
    };

    public static final String[] ISSUES_HEADER = {"TITLE",
            "NAME",
            "KEY",
            "NUMBER",
            "COVER",
            "PAGES",
            "PRINTED_PRICE",
            "CURRENCY",
            "RELEASE_DATE",
            "SHORT_REVIEW",
            "ISBN10",
            "EDITION",
            "VARIANT"
    };

    private Constants() {}
}
