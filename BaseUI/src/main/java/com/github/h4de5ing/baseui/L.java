package com.github.h4de5ing.baseui;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.Locale;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

@Deprecated
public class L {
    private static String TAG = "gh0st";
    private static boolean LOG_DEBUG = false;
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final int VERBOSE = 2;
    private static final int DEBUG = 3;
    private static final int INFO = 4;
    private static final int WARN = 5;
    private static final int ERROR = 6;
    private static final int ASSERT = 7;
    private static final int JSON = 8;
    private static final int XML = 9;

    private static final int JSON_INDENT = 4;

    public static void init(boolean isDebug, String tag) {
        TAG = tag;
        LOG_DEBUG = isDebug;
    }

    public static void v(String msg) {
        log(VERBOSE, null, msg);
    }

    public static void v(String tag, String msg) {
        log(VERBOSE, tag, msg);
    }

    public static void d(String msg) {
        log(DEBUG, null, msg);
    }

    public static void d(String tag, String msg) {
        log(DEBUG, tag, msg);
    }

    public static void i(Object... msg) {
        StringBuilder sb = new StringBuilder();
        for (Object obj : msg) {
            sb.append(obj);
            sb.append(",");
        }
        log(INFO, null, String.valueOf(sb));
    }

    public static void w(String msg) {
        log(WARN, null, msg);
    }

    public static void w(String tag, String msg) {
        log(WARN, tag, msg);
    }

    public static void e(String msg) {
        log(ERROR, null, msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        log(ERROR, tag, msg + '\n' + Log.getStackTraceString(tr));
    }

    public static void e(Throwable tr) {
        log(ERROR, TAG, '\n' + Log.getStackTraceString(tr));
    }

    public static void a(String msg) {
        log(ASSERT, null, msg);
    }

    public static void a(String tag, String msg) {
        log(ASSERT, tag, msg);
    }

    public static void json(String json) {
        log(JSON, null, json);
    }

    public static void json(String tag, String json) {
        log(JSON, tag, json);
    }

    public static void xml(String xml) {
        log(XML, null, xml);
    }

    public static void xml(String tag, String xml) {
        log(XML, tag, xml);
    }

    private static void log(int logType, String tagStr, Object objects) {
        String[] contents = wrapperContent(tagStr, objects);
        String tag = contents[0];
        String msg = contents[1];
        String headString = contents[2];
        if (LOG_DEBUG) {
            switch (logType) {
                case VERBOSE:
                case DEBUG:
                case INFO:
                case WARN:
                case ERROR:
                case ASSERT:
                    printDefault(logType, tag, headString + msg);
                    break;
                case JSON:
                    printJson(tag, msg, headString);
                    break;
                case XML:
                    printXml(tag, msg, headString);
                    break;
                default:
                    break;
            }
        } else {
            if (logType == ERROR)
                write2File(objects, "errlog");
        }
    }

    public static void writeLog(Object err, String fileName) {
        write2File(err, fileName);
    }

    private static void write2File(Object err, String fileName) {

        Date curDate = new Date(System.currentTimeMillis());
        String strContent = String.format(Locale.CHINA, "%tF %tT\t:\t%s\n", curDate, curDate, err);
        try {
            File dir = new File("/sdcard/KYAssetsLog/");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, String.format(Locale.CHINA, "%tY%tm%td_%s_%s.txt", curDate, curDate, curDate, "log", fileName));
            if (!file.exists()) {
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File.");
        }
    }


    private static void printDefault(int type, String tag, String msg) {
        if (TextUtils.isEmpty(tag)) {
            tag = TAG;
        }
        if (!LOG_DEBUG && type == ERROR) {
            write2File(msg, "error");
        }
        printLine(tag, true);
        int maxLength = 4000;
        int countOfSub = msg.length();
        if (countOfSub > maxLength) {
            for (int i = 0; i < countOfSub; i += maxLength) {
                if (i + maxLength < countOfSub) {
                    printSub(type, tag, msg.substring(i, i + maxLength));
                } else {
                    printSub(type, tag, msg.substring(i, countOfSub));
                }
            }
        } else {
            printSub(type, tag, msg);
        }
        printLine(tag, false);
    }

    private static void printSub(int type, String tag, String msg) {
        switch (type) {
            case VERBOSE:
                Log.v(tag, msg);
                break;
            case DEBUG:
                Log.d(tag, msg);
                break;
            case INFO:
                Log.i(tag, msg);
                break;
            case WARN:
                Log.w(tag, msg);
                break;
            case ERROR:
                Log.e(tag, msg);
                break;
            case ASSERT:
                Log.wtf(tag, msg);
                break;
        }
    }

    private static void printJson(String tag, String json, String headString) {
        if (TextUtils.isEmpty(json)) {
            d("Empty/Null json content");
            return;
        }
        if (TextUtils.isEmpty(tag)) {
            tag = TAG;
        }
        String message;

        try {
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                message = jsonObject.toString(JSON_INDENT);
            } else if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                message = jsonArray.toString(JSON_INDENT);
            } else {
                message = json;
            }
        } catch (JSONException e) {
            message = json;
        }

        printLine(tag, true);
        message = headString + LINE_SEPARATOR + message;
        String[] lines = message.split(LINE_SEPARATOR);
        for (String line : lines) {
            printSub(DEBUG, tag, "|" + line);
        }
        printLine(tag, false);
    }

    private static void printXml(String tag, String xml, String headString) {
        if (TextUtils.isEmpty(tag)) {
            tag = TAG;
        }
        if (xml != null) {
            try {
                Source xmlInput = new StreamSource(new StringReader(xml));
                StreamResult xmlOutput = new StreamResult(new StringWriter());
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                transformer.transform(xmlInput, xmlOutput);
                xml = xmlOutput.getWriter().toString().replaceFirst(">", ">\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
            xml = headString + "\n" + xml;
        } else {
            xml = headString + "Log with null object";
        }

        printLine(tag, true);
        String[] lines = xml.split(LINE_SEPARATOR);
        for (String line : lines) {
            if (!TextUtils.isEmpty(line)) {
                printSub(DEBUG, tag, "|" + line);
            }
        }
        printLine(tag, false);
    }

    private static String[] wrapperContent(String tag, Object... objects) {
        if (TextUtils.isEmpty(tag)) {
            tag = TAG;
        }
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement targetElement = stackTrace[5];
        String className = targetElement.getClassName();
        String[] classNameInfo = className.split("\\.");
        if (classNameInfo.length > 0) {
            className = classNameInfo[classNameInfo.length - 1] + ".java";
        }
        String methodName = targetElement.getMethodName();
        int lineNumber = targetElement.getLineNumber();
        if (lineNumber < 0) {
            lineNumber = 0;
        }
        String methodNameShort = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
        String msg = (objects == null) ? "Log with null object" : getObjectsString(objects);
        String headString = "[(" + className + ":" + lineNumber + ")#" + methodNameShort + " ] ";
        return new String[]{tag, msg, headString};
    }

    private static String getObjectsString(Object... objects) {

        if (objects.length > 1) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\n");
            for (int i = 0; i < objects.length; i++) {
                Object object = objects[i];
                if (object == null) {
                    stringBuilder.append("param").append("[").append(i).append("]").append(" = ").append("null").append("\n");
                } else {
                    stringBuilder.append("param").append("[").append(i).append("]").append(" = ").append(object.toString()).append("\n");
                }
            }
            return stringBuilder.toString();
        } else {
            Object object = objects[0];
            return object == null ? "null" : object.toString();
        }
    }

    private static void printLine(String tag, boolean isTop) {
        if (isTop) {
            Log.d(tag, "╔═══════════════════════════════════════════════════════════════════════════════════════");
        } else {
            Log.d(tag, "╚═══════════════════════════════════════════════════════════════════════════════════════");
        }
    }
}
