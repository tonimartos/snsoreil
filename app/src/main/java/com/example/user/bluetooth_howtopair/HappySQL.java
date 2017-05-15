package com.example.user.bluetooth_howtopair;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HappySQL {
    private static Map<Class, Class> basicMap;

    public static Object sql2VO(SQLiteDatabase db, String sql, Class clazz) {
        return cursor2VO(db.rawQuery(sql, null), clazz);
    }

    public static Object sql2VO(SQLiteDatabase db, String sql, String[] selectionArgs, Class clazz) {
        return cursor2VO(db.rawQuery(sql, selectionArgs), clazz);
    }

    public static List sql2VOList(SQLiteDatabase db, String sql, Class clazz) {
        return cursor2VOList(db.rawQuery(sql, null), clazz);
    }

    public static List sql2VOList(SQLiteDatabase db, String sql, String[] selectionArgs, Class clazz) {
        return cursor2VOList(db.rawQuery(sql, selectionArgs), clazz);
    }

    public static Object cursor2VO(Cursor c, Class clazz) {
        Object obj = null;
        if (c != null) {
            try {
                c.moveToNext();
                obj = setValues2Fields(c, clazz);
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("ERROR @\uff1acursor2VO");
            } finally {
                c.close();
            }
        }
        return obj;
    }

    public static List cursor2VOList(Cursor c, Class clazz) {
        if (c == null) {
            return null;
        }
        List list = new LinkedList();
        while (c.moveToNext()) {
            try {
                list.add(setValues2Fields(c, clazz));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("ERROR @\uff1acursor2VOList");
                return null;
            } finally {
                c.close();
            }
        }
        return list;
    }

    private static Object setValues2Fields(Cursor c, Class clazz) throws Exception {
        String[] columnNames = c.getColumnNames();
        Object obj = clazz.newInstance();
        for (Field _field : clazz.getFields()) {
            Class<? extends Object> typeClass = _field.getType();
            int j = 0;
            while (j < columnNames.length) {
                String columnName = columnNames[j];
                typeClass = getBasicClass(typeClass);
                if (!isBasicType(typeClass)) {
                    _field.set(obj, setValues2Fields(c, typeClass));
                    break;
                } else if (columnName.equalsIgnoreCase(_field.getName())) {
                    String _str = c.getString(c.getColumnIndex(columnName));
                    if (_str != null) {
                        if (_str == null) {
                            _str = "";
                        }
                        Object attribute = typeClass.getConstructor(new Class[]{String.class}).newInstance(new Object[]{_str});
                        _field.setAccessible(true);
                        _field.set(obj, attribute);
                    }
                } else {
                    j++;
                }
            }
        }
        return obj;
    }

    private static boolean isBasicType(Class typeClass) {
        if (typeClass.equals(Integer.class) || typeClass.equals(Long.class) || typeClass.equals(Float.class) || typeClass.equals(Double.class) || typeClass.equals(Boolean.class) || typeClass.equals(Byte.class) || typeClass.equals(Short.class) || typeClass.equals(String.class)) {
            return true;
        }
        return false;
    }

    public static Class<? extends Object> getBasicClass(Class typeClass) {
        Class _class = (Class) basicMap.get(typeClass);
        if (_class == null) {
            return typeClass;
        }
        return _class;
    }

    static {
        basicMap = new HashMap();
        basicMap.put(Integer.TYPE, Integer.class);
        basicMap.put(Long.TYPE, Long.class);
        basicMap.put(Float.TYPE, Float.class);
        basicMap.put(Double.TYPE, Double.class);
        basicMap.put(Boolean.TYPE, Boolean.class);
        basicMap.put(Byte.TYPE, Byte.class);
        basicMap.put(Short.TYPE, Short.class);
    }
}

