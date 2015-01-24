package sk.adresa.eaukcia.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import sk.adresa.eaukcia.core.data.Event;
import sk.adresa.eaukcia.core.data.RequirementNode;

import sk.adresa.eaukcia.core.query.Paging;

/**
 * Pomocna trieda, obsahuje niekolko pomocnych metod.
 *
 *
 *
 */
public class Util {
    
    public static boolean isStringArray(String value){
        return value.contains(",");   
    }
    
    public static boolean isTheOnlyElement(Object element, List elements) {
        if (elements == null || elements.size() != 1 || !elements.get(0).equals(element)) {
            return false;
        }
        return true;
    }
    
    public static String formatDateToDMY(Date date) {
        if (date != null) {
            Calendar cal = new GregorianCalendar();
            cal.setTime(date);

            String dateString = Integer.toString(cal.get(Calendar.DATE));
            if (dateString.length() == 1) {
                dateString = "0" + dateString;
            }
            String monthString = Integer.toString((cal.get(Calendar.MONTH) + 1));
            if (monthString.length() == 1) {
                monthString = "0" + monthString;
            }

            return dateString + "." + monthString + "." + cal.get(Calendar.YEAR);
        } else {
            return "";
        }
    }
    
    public static String shiftSpaces(Object o){
        if(o == null) return "null";
        return o.toString().replaceAll("(\n *)", "$1    ");
    }

    public static String formatDate(Date d, String dateFormat) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(d);
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);
        return dateFormatter.format(cal.getTime());
    }

    public static String objectToString(Object object) {
        if (object == null) {
            return "\nnull";
        }

        if (object instanceof Object[]) {
            return arrayToString((Object[]) object);
        } else if (object instanceof List) {
            return (listToString((List) object));
        } else if (object instanceof Set) {
            return (setToString((Set) object));
        } else {

            return "\nClass:" + object.getClass().getName() + "\n" + object;
        }
    }

    public static String arrayToString(Object[] objects) {
        if (objects == null) {
            return "\nnull";
        }
        StringBuilder string = new StringBuilder();
        int i;
        for (i = 0; i < objects.length; i++) {
            string.append(objectToString(objects[i]));

        }
        return "\nArray[" + objects.length + "]{" + string.toString().replaceAll("(\n *)", "$1    ") + "\n}";
    }

    public static List getPagedSublist(List list, Paging paging) {
        if (list != null && !list.isEmpty()) {
            int startIx = 0;
            int endIx = 0;
            if (paging.getActualPage() > 1) {
                startIx = (paging.getActualPage() - 1) * paging.getRowsPerPage();
            }

            if (startIx < list.size()) {
                endIx = Math.min(list.size(), startIx + paging.getRowsPerPage());
            }

            if (startIx < list.size()) {
                return list.subList(startIx, endIx);
            }
        }

        return new ArrayList(0);
    }

    public static String listToString(List objects) {
        if (objects == null) {
            return "\nnull";
        }
        StringBuilder string =
                new StringBuilder();
        Iterator iterator = objects.iterator();
        while (iterator.hasNext()) {
            Object object = iterator.next();
            string.append(objectToString(object));
        }
        return "\nList[" + objects.size() + "]{" + string.toString().replaceAll("(\n *)", "$1    ") + "\n}";
    }

    public static String setToString(Set objects) {
        if (objects == null) {
            return "\nnull";
        }
        StringBuilder string =
                new StringBuilder();
        Iterator iterator = objects.iterator();
        while (iterator.hasNext()) {
            Object object = iterator.next();
            string.append(objectToString(object));
        }
        return "\nSet[" + objects.size() + "]{\n" + string.toString().replaceAll("(\n *)", "$1    ") + "\n}";
    }

    /**
     * Vrati objekty reprezentovane ako
     * retazec znakov - vracia ich typy (Class.getName()) a ich hodnoty v rozumnom 
     * formatovani a v co najprehladnejsej podobe
     * 
     * 
     * @param objects
     * @return
     */
    public static String arrayToStringWithClassNames(Object[] objects) {
        if (objects == null) {
            return "null";
        }
        StringBuilder string =
                new StringBuilder("Array[" + objects.length + "]{\n");
        int i;
        for (i = 0; i < objects.length; i++) {
            if (objects[i] != null) {
                if (objects[i] instanceof Object[]) {
                    string.append("Class: ").append(objects[i].getClass().getName()).append(" Value:").append(arrayToStringWithClassNames((Object[]) objects[i])).append("\n");
                } else {
                    string.append("Class: ").append(objects[i].getClass().getName()).append(" Value:").append(objects[i]).append("\n");
                }

            } else {
                string.append("Class: Unknown Value: null \n");
            }
        }
        string.append("}\n");
        return string.toString();
    }

    public static String calculateNextTime(String time) {
        int ix;
        int hours = Integer.parseInt(time.substring(0, (ix = time.indexOf(":"))));
        int minutes = Integer.parseInt(time.substring(ix + 1));
        int newMinutes = (minutes + 50) % 60;
        if (minutes > newMinutes) {
            hours++;
        }
        return hours + ":" + minutes;
    }

    public static String calculatePreviousTime(String time) {
        int ix;
        int hours = Integer.parseInt(time.substring(0, (ix = time.indexOf(":"))));
        int minutes = Integer.parseInt(time.substring(ix + 1));
        int newMinutes = (minutes + 10) % 60;
        if (minutes < newMinutes) {
            hours--;
        }
        return hours + ":" + minutes;
    }

    public static String generateRandomString(int length) {
        KeyGenerator generator;
        Cipher cip;
        StringBuilder sb = new StringBuilder();
        try {
            generator = KeyGenerator.getInstance("AES");
            generator.init(128);
            SecretKey key = generator.generateKey();
            cip = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cip.init(Cipher.ENCRYPT_MODE, key);


            String str = "QAa0bcLdUK2eHfJgTP8XhiFj61DOklNm9nBoI5pGqYVrs3CtSuMZvwWx4yE7zR";

            Random r = new Random();

            for (int i = 1; i <= length; i++) {
                int random = r.nextInt();
                int sum = 0;

                byte[] bArr = cip.doFinal(intToByteArray(random));
                for (int j = 0; j < bArr.length; j++) {
                    sum += Math.abs(bArr[j]);
                }

                sb.append(str.charAt(sum % str.length()));
            }

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return sb.toString();


        //return generator.generateKey().toString();

    }

    private static byte[] intToByteArray(final int integer) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeInt(integer);
        dos.flush();
        return bos.toByteArray();
    }
    // converting back with data output stream

    public static int byteArrayToInt(final byte[] bytes) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        DataInputStream in = new DataInputStream(bis);
        return in.readInt();
    }

    
    
    
}
