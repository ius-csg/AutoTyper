package org.iuscsg.autotyper;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

public class Typer implements Runnable
{
    private String textToPrint;
    private Map<String, Integer> specialChars;
    private Map<String, Integer> shiftKeys;
    private Robot robot;

    Typer(String textToPrint) {
        this.textToPrint = textToPrint;
        this.specialChars = this.createSpecialChars();
        this.shiftKeys = this.createShiftKeys();
        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        try {
            Thread.sleep(2000);
            typeKeys(this.textToPrint);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void typeKeys(String text) throws IllegalArgumentException {
        for (String key : text.split(""))
            typeKey(key);
    }

    public void typeKey(String key) {
        try {
            if(this.shiftKeys.containsKey(key))
                typeKeyWithShift(key, this.shiftKeys.get(key));
            else if(this.specialChars.containsKey(key))
                typeKeyWithKeyCode(key, this.specialChars.get(key));
            else if(isAlpha(key) && key.toUpperCase().equals(key))
                typeKeyWithShift(key, getKeyCode(key));
            else if(isAlphaNumeric(key))
                typeKeyWithKeyCode(key, getKeyCode(key));
            else if(key.equals("\n"))
                typeKeyWithKeyCode(key, KeyEvent.VK_ENTER);
            else if(key.equals("\r")) {
                // ignore this character
            }
            else
                System.out.println("Unknown Key: " + key);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch(NoSuchFieldException e ) {
            throw new IllegalArgumentException(key.toUpperCase() + " is invalid key\n"+"VK_"+key.toUpperCase() + " is not defined in java.awt.event.KeyEvent");
        }
    }

    private void typeKeyWithShift(String key, int keycode) {
        try {
            robot.keyPress(KeyEvent.VK_SHIFT);
            typeKeyWithKeyCode(key, keycode);
            robot.keyRelease(KeyEvent.VK_SHIFT);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid key: " + key + " with Shift key and keycode: " + keycode);
        }
    }

    private void typeKeyWithKeyCode(String key, int keycode) {
        try {
            robot.keyPress(keycode);
            robot.keyRelease(keycode);
            robot.delay(8);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid key: " + key + " with keycode: " + keycode);
        }
    }

    private HashMap<String, Integer> createSpecialChars() {
        HashMap<String, Integer> keyMappings = new HashMap<>();
        keyMappings.put(",", KeyEvent.VK_COMMA);
        keyMappings.put(".", KeyEvent.VK_PERIOD);
        keyMappings.put("/", KeyEvent.VK_SLASH);
        keyMappings.put("'", KeyEvent.VK_QUOTE);
        keyMappings.put("\\", KeyEvent.VK_BACK_SLASH);
        keyMappings.put("[", KeyEvent.VK_OPEN_BRACKET);
        keyMappings.put("]", KeyEvent.VK_CLOSE_BRACKET);
        keyMappings.put("-", KeyEvent.VK_MINUS);
        keyMappings.put("=", KeyEvent.VK_EQUALS);
        keyMappings.put(" ", KeyEvent.VK_SPACE);
        keyMappings.put(";", KeyEvent.VK_SEMICOLON);
        keyMappings.put("`", KeyEvent.VK_BACK_QUOTE);
        return keyMappings;
    }

    private HashMap<String, Integer> createShiftKeys() {
        HashMap<String, Integer> keyMappings = new HashMap<>();
        keyMappings.put("?", KeyEvent.VK_SLASH);
        keyMappings.put("<", KeyEvent.VK_COMMA);
        keyMappings.put(">", KeyEvent.VK_PERIOD);
        keyMappings.put(":", KeyEvent.VK_SEMICOLON);
        keyMappings.put("\"", KeyEvent.VK_QUOTE);
        keyMappings.put("{", KeyEvent.VK_OPEN_BRACKET);
        keyMappings.put("}", KeyEvent.VK_CLOSE_BRACKET);
        keyMappings.put("|", KeyEvent.VK_BACK_SLASH);
        keyMappings.put("!", KeyEvent.VK_1);
        keyMappings.put("@", KeyEvent.VK_2);
        keyMappings.put("#", KeyEvent.VK_3);
        keyMappings.put("$", KeyEvent.VK_4);
        keyMappings.put("%", KeyEvent.VK_5);
        keyMappings.put("^", KeyEvent.VK_6);
        keyMappings.put("&", KeyEvent.VK_7);
        keyMappings.put("*", KeyEvent.VK_8);
        keyMappings.put("(", KeyEvent.VK_9);
        keyMappings.put(")", KeyEvent.VK_0);
        keyMappings.put("_", KeyEvent.VK_MINUS);
        keyMappings.put("+", KeyEvent.VK_EQUALS);
        keyMappings.put("~", KeyEvent.VK_BACK_QUOTE);
        return keyMappings;
    }

    private boolean isAlpha(String text) {
        return text.matches("[a-zA-Z]+");
    }

    private boolean isAlphaNumeric(String text) {
        return text.matches("[a-zA-Z0-9]+");
    }

    private int getKeyCode(String key) throws NoSuchFieldException, IllegalAccessException {
        return KeyEvent.class.getField("VK_" + key.toUpperCase()).getInt(null);
    }
}
