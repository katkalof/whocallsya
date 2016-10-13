package ru.yandex.whocallsya.service;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.provider.CallLog;

import rx.subjects.PublishSubject;


public class MissedCallContentObserver extends ContentObserver {

    private ContentResolver contentResolver;
    private String idlePhoneNumber;
    private PublishSubject<String> subject;
    private final String[] resultProjection = new String[]{
            CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DATE
    };

    public MissedCallContentObserver(Handler handler, ContentResolver contentResolver, String idlePhoneNumber, PublishSubject<String> subject) {
        super(handler);
        this.contentResolver = contentResolver;
        this.idlePhoneNumber = idlePhoneNumber;
        this.subject = subject;
    }

    @Override
    public boolean deliverSelfNotifications() {
        return true;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        Cursor cur = contentResolver.query(CallLog.Calls.CONTENT_URI,
                resultProjection,
                null,
                null,
                CallLog.Calls.DATE + " DESC LIMIT 1");
        if (cur != null) {
            int number = cur.getColumnIndex(CallLog.Calls.NUMBER);
            int type = cur.getColumnIndex(CallLog.Calls.TYPE);

            while (cur.moveToNext()) {
                String phNum = cur.getString(number);
                int callType = cur.getInt(type);
                if (!phNum.equals(idlePhoneNumber)) {
                    continue;
                }
                String result = "";
                switch (callType) {
                    case CallLog.Calls.INCOMING_TYPE:
                        result = "Incoming";
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        result = "Missed";
                        break;
                }
                subject.onNext(result);
            }
            cur.close();
        }
    }

}
