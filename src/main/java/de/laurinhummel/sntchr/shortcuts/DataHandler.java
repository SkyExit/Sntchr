package de.laurinhummel.sntchr.shortcuts;

import net.dv8tion.jda.api.entities.Member;
import org.mapdb.DB;
import org.mapdb.DBException;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

public class DataHandler {
    static DB db;

    //Sets value of new users to zero
    private static void validateUserBalance(Member member) {
        try { db = DBMaker.fileDB("data.db").checksumHeaderBypass().make(); }
            catch (DBException.FileLocked e) { }
        HTreeMap myMap = db.hashMap("economy").createOrOpen();
        myMap.putIfAbsent(member.getUser().getId(), 0);
    }

    public static int getUserBalance(Member member) {
        try { db = DBMaker.fileDB("data.db").checksumHeaderBypass().make(); }
            catch (DBException.FileLocked e) { }
        HTreeMap myMap = db.hashMap("economy").createOrOpen();

        validateUserBalance(member);

        return (int) myMap.get(member.getUser().getId());
    }

    public static void setUserBalance(Member member, int value) {
        try { db = DBMaker.fileDB("data.db").checksumHeaderBypass().make(); }
            catch (DBException.FileLocked e) { }
        HTreeMap myMap = db.hashMap("economy").createOrOpen();
        myMap.put(member.getUser().getId(), value);
    }

    public static boolean changeUserBalance(Member member, int value) {
        boolean valid = false;
        try { db = DBMaker.fileDB("data.db").checksumHeaderBypass().make(); }
            catch (DBException.FileLocked e) { }
        HTreeMap myMap = db.hashMap("economy").createOrOpen();
        validateUserBalance(member);
        int currentVAL = (int) myMap.get(member.getUser().getId());
        if((currentVAL + value) >= 0) {
            setUserBalance(member, currentVAL + value);
            valid = true;
        }
        return valid;
    }
}
