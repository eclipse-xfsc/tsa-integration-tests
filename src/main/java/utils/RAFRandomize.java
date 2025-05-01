//Copyright (c) 2023 Vereign AG [https://www.vereign.com]

package utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class RAFRandomize {

    public static final String MAIL_DOMAIN = "@example.com";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder().append(DateTimeFormat.forPattern("MM/dd/YYYY").getPrinter()).toFormatter();
    protected static final Integer MAX_MAIL_LENGTH = 70;
    private static final Random RANDOM = new Random(System.currentTimeMillis());    // NOSONAR
    private static final DateTimeFormatter DATE_TIME_FORMATTER2 =
            new DateTimeFormatterBuilder().append(DateTimeFormat.forPattern("YYYY-MM-dd").getPrinter()).toFormatter();
    private static final DateTimeFormatter DATE_TIME_FORMATTER3 =
            new DateTimeFormatterBuilder().append(DateTimeFormat.forPattern("MMddHHmmss").getPrinter()).toFormatter();

    private RAFRandomize() {
    }

    /**
     * Generate RANDOM set of chars with RANDOM length which limited by min/max values
     *
     * @param min - minimal length of string
     * @param max - maximum length of string
     * @return generated set of chars
     */
    public static String getAlphanumeric(int min, int max) {
        return RandomStringUtils.randomAlphanumeric(getInt(min, max));
    }

    /**
     * Generate RANDOM set of chars with length defined
     *
     * @param length - length of output string
     * @return generated set of chars
     */
    public static String getAlphanumeric(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

    /**
     * Generates RANDOM number in range
     *
     * @param min - lowest value to generate
     * @param max - highest value to generate
     * @return generated RANDOM number
     */
    public static int getInt(int min, int max) {
        return RANDOM.nextInt((max - min) + 1) + min;
    }

    /**
     * Generate RANDOM set of alphabetic chars with RANDOM length which limited by min/max values
     *
     * @param min - minimal length of string
     * @param max - maximum length of string
     * @return generated set of chars
     */
    public static String getAlphabetic(int min, int max) {
        return RandomStringUtils.randomAlphabetic(getInt(min, max));
    }

    /**
     * Generate RANDOM set of numeric chars with RANDOM length which limited by min/max values
     *
     * @param minLength - minimal length of string
     * @param maxLength - maximum length of string
     * @return generated set of chars
     */
    public static String getNumeric(int minLength, int maxLength) {
        return RandomStringUtils.randomNumeric(getInt(minLength, maxLength));
    }

    /**
     * Generate RANDOM boolean
     *
     * @return generated boolean value
     */
    public static Boolean getBool() {
        return RANDOM.nextBoolean();
    }

    /**
     * Generate RANDOM mail address using template {random value}@example.com
     *
     * @param min minimal length of random mail address not including '@example.com' length
     * @param max maximum length of random mail address not including '@example.com' length
     * @return generated mail address
     */
    public static String getMail(int min, int max) {
        int actual = getInt(min + MAIL_DOMAIN.length(), max + MAIL_DOMAIN.length());
        String beforeAt = RandomStringUtils.randomAlphanumeric(actual - MAIL_DOMAIN.length());
        String mailAddress = beforeAt + MAIL_DOMAIN;
        if (mailAddress.length() > MAX_MAIL_LENGTH) {
            mailAddress = mailAddress.substring(mailAddress.length() - MAX_MAIL_LENGTH, mailAddress.length());
        }
        return mailAddress;
    }

    /**
     * Randomly get value from input list
     *
     * @param inputList - list of values for random selection
     * @param <T>       - class of list to return value of this type
     * @return item from list of specified class or null if list is empty
     */
    public static <T> T getRandomValueFromList(List<T> inputList) {
        if (inputList.isEmpty()) {
            return null;
        }
        Integer arrayPosition = (inputList.size() > 1) ? getInt(0, inputList.size() - 1) : 0;
        return inputList.get(arrayPosition);
    }

    /**
     * @return string representation of current date in format "MM/dd/YYYY"
     */
    public static String getCurrentDateFormatted() {
        return DateTime.now().toString(DATE_TIME_FORMATTER);
    }

    /**
     * @return string representation of current date in format "MM-dd-YYYY"
     */
    public static String getCurrentDateFormatted2() {
        return DateTime.now().toString(DATE_TIME_FORMATTER2);
    }

    /**
     * @return string representation of current date in format "MMddHHmmss"
     */
    public static String getCurrentDateFormatted3() {
        return DateTime.now().toString(DATE_TIME_FORMATTER3);
    }

    /**
     * @return wrapper for return UUID-like string
     */
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Randomizes all Alphanumeric characters inside the <i>inputString</i>, while keeping the places of all other characters.
     * Numbers gets substituted with random numeric values.
     * Characters gets substituted with random alphabetic values.
     * <P/>
     * <br/> E.g.:
     <table>
     <col width="25%"/>
     <col width="75%"/>
     <thead>
     <tr><th></th><th>Result</th></tr>
     <thead>
     <tbody>
     <tr><td>originalString</td><td>21som...examp-l@t123.s34t#aaaaaa$bbbb space(tri_get</td></tr>
     <tr><td>generatedString</td><td>79MUp...KvAwB-g@T485.b21D#APRzar$DDmK Diaap(vka_nDZ</td></tr>
     </tbody>
     </table>
     * @param inputString The string that will be used as a base.
     * @return Returns the resulting string.
     */
    public static String retainSpecialCharsAndRandomizeAlphanumeric(String inputString){
        String generatedString;

        int formatLength = inputString.length();
        String[] specialChars = new String[formatLength];
        int[] specialCharsPositions = new int[formatLength];
        String[] regularChars = new String[formatLength];
        int[] regularCharsPositions = new int[formatLength];

        Pattern pSpecialChar = Pattern.compile("[^A-Za-z0-9]");
        Matcher mSpecialChar = pSpecialChar.matcher(inputString);

        Pattern pRegular = Pattern.compile("[A-Za-z0-9]");
        Matcher mRegular = pRegular.matcher(inputString);

        for (int i = 0; i < formatLength; i++) {
            if (mSpecialChar.find()) {
                specialCharsPositions[i] = mSpecialChar.start();
                specialChars[i] = mSpecialChar.group();
            }
            if (mRegular.find()) {
                regularCharsPositions[i] = mRegular.start();
                regularChars[i] = mRegular.group();
            }
        }

        StringBuilder sB = new StringBuilder(formatLength);

        for (int k = 0; k < formatLength; k++) {
            for (int m = 0; m < formatLength; m++) {

                if (specialCharsPositions[m] == k && specialChars[m] != null) {
                    sB.append(specialChars[m]);
                } else if (regularCharsPositions[m] == k && regularChars[m] != null) {

                    boolean isAlpha = false;
                    try {
                        Integer.parseInt(regularChars[m]);
                    } catch (NumberFormatException e) {
                        isAlpha = true;
                    }

                    if(isAlpha){
                        sB.append(RandomStringUtils.randomAlphabetic(1));
                    } else {
                        sB.append(RandomStringUtils.randomNumeric(1));
                    }



                }
            }

        }

        generatedString = sB.toString();

        return generatedString;
    }

}

