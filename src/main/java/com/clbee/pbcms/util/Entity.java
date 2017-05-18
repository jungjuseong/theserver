package com.clbee.pbcms.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.google.common.base.CaseFormat;

public class Entity extends HashMap implements Serializable {

	private static final long serialVersionUID = 1L;
	private boolean isMultPart = false;

	public Entity(){}

	public Entity(HttpServletRequest req) throws Exception {
		this.parseRequest(req);
	}

	public Entity(Map map) throws Exception {
		this.parseMap(map);
	}

	// ��� Ű�� org_file_name -> orgFileName���� ����
	public Entity toLowerCamelKeys() throws Exception {
		Entity e = new Entity();
		
    	Iterator it = entrySet().iterator();
    	String newkey="";
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            newkey = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, (String) pairs.getKey());
            e.setValue(newkey, (String) pairs.getValue());
        }
        return e;
	}
	
	// ��� Ű�� orgFileName -> org_file_name���� ����
	public Entity toUpperCamelKeys() throws Exception {
		Entity e = new Entity();
		
		Iterator it = entrySet().iterator();
		String newkey="";
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			newkey = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, (String) pairs.getKey());
			e.setValue(newkey, (String) pairs.getValue());
		}
		return e;
	}
	
    /**
     * ResultSet ���� �÷� �̸��� key�� �ؼ� �� ���� Entity �� �����ϴ� method
     *
     * @param ResultSet
     * @exception SQLException
     */
    public void parseResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int size = md.getColumnCount();

        for (int i = 1; i <= size; i++) {
            setValue(md.getColumnName(i), rs.getString(i));
        }
    }

    /**
     * ResultSet ���� �÷� �̸��� �ε����� key�� �ؼ� �� ���� Entity �� �����ϴ� method
     *
     * @param int
     * @param ResultSet
     * @exception SQLException
     */
    public void parseResultSet(int iIndex, ResultSet rs)
        throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int size = md.getColumnCount();

        String sColumnName = null;
        String sColumnValue = null;

        for (int i = 1; i <= size; i++) {
            setValue(new StringBuffer(md.getColumnName(i)).append(iIndex)
                                                          .toString(),
                rs.getString(i));
        }
    }

    /**
     * Request ���� parameter �� �̾Ƽ� Entity �� �����ϴ� method ��, 'a', 'v' ��
     * GenericServlet ���� ����ϹǷ� entity �� �������� �ʴ´�.
     *
     * @param HttpServletRequest
     * @exception Exception
     */
    public void parseRequest(HttpServletRequest pm_oRequest)
        throws ServletException {
        Enumeration im_oEnum = pm_oRequest.getParameterNames();
        String im_sParam = null;
        String[] im_sValues = null;

        //request ��ü�� �Ľ��Ͽ� ���ڿ��� �����. - get ��������� ������ �����ϱ� ����
        String paramString = "";

        while (im_oEnum.hasMoreElements()) {
            im_sParam = (String) im_oEnum.nextElement();
            im_sValues = pm_oRequest.getParameterValues(im_sParam);

            if (im_sValues.length == 1) {

            	String _val = im_sValues[0];

            	//get ������� �Ѿ�� ó���� ���ؼ��� ���ڵ��� �������ش�.
            	if(pm_oRequest.getMethod().toLowerCase().equals("get")) {
            		_val = StringUtil.asciiToKsc(im_sValues[0]);
            	}

                setValue(im_sParam, _val);

                //get������� ������ ������ ����
                //- ���ڼ��� �������� �� �ʿ��� �����͸� �����ؾ� �Ѵ�.
            	if(im_sParam.equals("pageNo") ||
            			im_sParam.equals("brdType") ||
            			im_sParam.equals("searchVal") ||
            			im_sParam.equals("searchKey") ||
            			im_sParam.indexOf("Seq")>-1) {
            		paramString += im_sParam +"="+ _val + "&";

            		setValue("parameters", paramString);
            	}
            } else {
            	if(im_sParam.equals("chk") || im_sParam.equals("menu")) {
            		setValue(im_sParam, im_sValues);
            	}

//            	setValue(im_sParam, im_sValues);
            }
        }

    }


    /**
     * �̿�� �Ķ���ͷ� �޴� Map�� Entity�� ���ε� ��Ų��.
     */
    public void parseMap(Map map) throws Exception {

    	Iterator it = map.keySet().iterator();

    	String key = "";
    	String val = "";

    	while(it.hasNext()) {

    		key = it.next().toString();
    		val = StringUtil.nvl(map.get(key), "");

    		System.out.println(key + " : " + val);

    		setValue(key, val);
    	}

    }


    public void setValue(String sKey, String sValue) {
        if (sValue != null) {
            put(sKey, sValue);
        }
    }

    public void setValue(String sKey, String[] sValues) {
        put(sKey, sValues);
    }

    public void setValue(String sKey, byte[] yValues) {
        String sValue = null;

        if (yValues != null) {
            sValue = new String(yValues);
        }

//        put(sKey.toUpperCase(), sValue);
        put(sKey, sValue);
    }

    public void setValue(String sKey, byte yValue) {
//        put(sKey.toUpperCase(), Byte.toString(yValue));
        put(sKey, Byte.toString(yValue));
    }

    public void setValue(String sKey, char[] cValues) {
        String sValue = null;

        if (cValues != null) {
            sValue = new String(cValues);
        }

//        put(sKey.toUpperCase(), sValue);
        put(sKey, sValue);
    }

    public void setValue(String sKey, char cValue) {
//        put(sKey.toUpperCase(), String.valueOf(cValue));
        put(sKey, String.valueOf(cValue));
    }

    public void setValue(String sKey, float fValue) {
//        put(sKey.toUpperCase(), String.valueOf(fValue));
        put(sKey, String.valueOf(fValue));
    }

    public void setValue(String sKey, boolean bValue) {
        put(sKey, String.valueOf(bValue));
    }

    public void setValue(String sKey, short tValue) {
        put(sKey, String.valueOf(tValue));
    }

    public void setValue(String sKey, int iValue) {
        put(sKey, String.valueOf(iValue));
    }

    public void setValue(String sKey, long lValue) {
        put(sKey, String.valueOf(lValue));
    }

    public void setValue(String sKey, double dValue) {
        put(sKey, String.valueOf(dValue));
    }

    public void setValue(String sKey, java.util.Date value) {
        String sValue = null;

        if (value != null) {
            ;
        }

        sValue = value.toString();

        put(sKey, sValue);
    }

    public void setValue(String sKey, Vector value) {
        put(sKey, value);
    }

    public void setValue(String sKey, List value) {
        put(sKey, value);
    }

    public void setValue(String sKey, Hashtable value) {
        put(sKey, value);
    }

    public void setValue(String sKey, Entity value) {
        put(sKey, value);
    }

    public void setValue(String sKey, HashMap value) {
    	put(sKey, value);
    }

    public void setValue(String sKey, Map value) {
        put(sKey, value);
    }





    public String getString(String sKey) {
        String sValue = null;
        Object obj = null;

        try {
            obj = get(sKey);

            if (obj instanceof String) {
                sValue = (String) obj;
            } else if (obj instanceof String[]) {
                sValue = ((String[]) obj)[0];
            } else {
            	sValue = obj.toString();
            }
        } catch (Exception e) {
            sValue = "";
        }

        return sValue;
    }

    /**
     *
     * Entity �� ����� String[] �� return �ϴ� method String �� ����Ǿ� ���� ��쿡�� length ��
     * 1�� String[] �� return �Ѵ�.
     *
     * @param String
     *            sKey
     * @return String[]
     * @exception Exception
     */
    public String[] getStrings(String sKey) {
        String[] sValues = null;
        Object obj = null;

        try {
//            obj = get(sKey.toUpperCase());
            obj = get(sKey);

            if (obj instanceof String) {
                sValues = new String[1];
                sValues[0] = (String) obj;
            } else {
                sValues = (String[]) obj;
            }
        } catch (Exception e) {
        }

        return sValues;
    }

    public byte getByte(String sKey) {
        byte yResult = (byte) 0;

        try {
//            yResult = Byte.parseByte((String) get(sKey.toUpperCase()));
            yResult = Byte.parseByte((String) get(sKey));
        } catch (Exception e) {
        }

        return yResult;
    }

    public byte[] getBytes(String sKey) {
        byte[] yResults = null;

        try {
//            yResults = ((String) get(sKey.toUpperCase())).getBytes();
            yResults = ((String) get(sKey)).getBytes();
        } catch (Exception e) {
        }

        return yResults;
    }

    public char getChar(String sKey) {
        char cResult = (char) 0;

        try {
//            cResult = ((String) get(sKey.toUpperCase())).charAt(0);
            cResult = ((String) get(sKey)).charAt(0);
        } catch (Exception e) {
        }

        return cResult;
    }

    public char[] getChars(String sKey) {
        char[] cResults = null;

        try {
//            cResults = ((String) get(sKey.toUpperCase())).toCharArray();
            cResults = ((String) get(sKey)).toCharArray();
        } catch (Exception e) {
        }

        return cResults;
    }

    public float getFloat(String sKey) {
        float fResult = 0;

        try {
//            fResult = Float.parseFloat((String) get(sKey.toUpperCase()));
            fResult = Float.parseFloat((String) get(sKey));
        } catch (Exception e) {
        }

        return fResult;
    }

    public boolean getBoolean(String sKey) {
        boolean bResult = false;

        try {
//            bResult = Boolean.getBoolean((String) get(sKey.toUpperCase()));
            bResult = Boolean.getBoolean((String) get(sKey));
        } catch (Exception e) {
        }

        return bResult;
    }

    public short getShort(String sKey) {
        short tResult = 0;
        Object o = get(sKey);
        try {
//            tResult = Short.parseShort((String) get(sKey.toUpperCase()));
        	o = ((String)o).replaceAll(",", "");
        	tResult = Short.parseShort((String) o);
        } catch (Exception e) {
        }

        return tResult;
    }

    public int getInt(String sKey) {
        int iResult = 0;

//        Object o = get(sKey.toUpperCase());
        Object o = get(sKey);

        try {
        	o = ((String)o).replaceAll(",", "");
            iResult = Integer.parseInt((String) o);
        } catch (Exception e) {
        }

        try {
            Class classType = o.getClass();

            if (classType == BigDecimal.class) {
                iResult = Integer.parseInt(o.toString());
            }
        } catch (Exception e) {
        }

        return iResult;
    }

    public long getLong(String sKey) {
        long lResult = 0;
//        Object o = get(sKey.toUpperCase());
        Object o = get(sKey);

        try {
        	o = ((String)o).replaceAll(",", "");
            lResult = Long.parseLong((String) o);
        } catch (Exception e) {
        }

        try {
            Class classType = o.getClass();

            if (classType == BigDecimal.class) {
                lResult = Long.parseLong(o.toString());
            }
        } catch (Exception e) {
        }

        return lResult;
    }

    public double getDouble(String sKey) {
        double dResult = 0;
//        Object o = get(sKey.toUpperCase());
        Object o = get(sKey);

        try {
        	o = ((String)o).replaceAll(",", "");
            dResult = Double.parseDouble((String) o);
        } catch (Exception e) {
        }

        try {
            Class classType = o.getClass();

            if (classType == BigDecimal.class) {
                dResult = Double.parseDouble(o.toString());
            }
        } catch (Exception e) {
        }

        return dResult;
    }

    public java.util.Date getDate(String sKey) {
        java.util.Date result = null;

        try {
//            String sDate = (String) get(sKey.toUpperCase());
            String sDate = (String) get(sKey);

            SimpleDateFormat formatter = new SimpleDateFormat(
                    "yyyy-MM-dd hh:mm:ss");
            ParsePosition pos = new ParsePosition(0);
            result = formatter.parse(sDate, pos);
        } catch (Exception e) {
        }

        return result;
    }

    public Vector getVector(String sKey) {
        Vector vResult = null;

        try {
//            vResult = (Vector) get(sKey.toUpperCase());
            vResult = (Vector) get(sKey);
        } catch (Exception e) {
        }

        return vResult;
    }

    public List getList(String sKey) {
        List alResult = null;

        try {
//            alResult = (ArrayList) get(sKey.toUpperCase());
            alResult = (List) get(sKey);
        } catch (Exception e) {
        	alResult = new ArrayList();
        }

        return alResult;
    }

    public Hashtable getHashtable(String sKey) {
    	Hashtable value = null;

    	try {
//            value = (Hashtable) get(sKey.toUpperCase());
    		value = (Hashtable) get(sKey);

    		if (value == null) {
    			value = new Hashtable();
    		}
    	} catch (Exception e) {
    		value = new Hashtable();
    	}

    	return value;
    }

    public Map getMap(String sKey) {
        Map value = null;

        try {
            value = (Map) get(sKey);

            if (value == null) {
                value = new HashMap();
            }
        } catch (Exception e) {
            value = new HashMap();
        }

        return value;
    }

    public Entity getEntity(String sKey) {
        Entity value = null;

        try {
//            value = (Entity) get(sKey.toUpperCase());
            value = (Entity) get(sKey);

            if (value == null) {
                value = new Entity();
            }
        } catch (Exception e) {
            value = new Entity();
        }

        return value;
    }

    public HashMap getHashMap(String sKey) {
        HashMap value = null;

        try {
//            value = (HashMap) get(sKey.toUpperCase());
            value = (HashMap) get(sKey);

            if (value == null) {
                value = new HashMap();
            }
        } catch (Exception e) {
            value = new HashMap();
        }

        return value;
    }






    public void removeValue(String sKey) {
        remove(sKey);
    }

    public String getKey(String sValue) {
        String sResult = null;

        Set keySet = entrySet();
        Object[] lists = keySet.toArray();

        String sKey = null;
        Object value = null;

        for (int i = 0; i < lists.length; i++) {
            sKey = (String) (((Map.Entry) lists[i]).getKey());
            value = get(sKey);

            if (value instanceof String &&
                    ((String) value).trim().equals(sValue)) {
                sResult = (String) sKey;

                break;
            }
        }

        return sResult;
    }

    public String getKey(String sValue, String sKeyPrefix) {
        sKeyPrefix = sKeyPrefix.toUpperCase();

        String sResult = null;

        Set keySet = entrySet();
        Object[] lists = keySet.toArray();

        String sKey = null;
        Object value = null;

        for (int i = 0; i < lists.length; i++) {
            sKey = (String) (((Map.Entry) lists[i]).getKey());
            value = get(sKey);

            if (sKey.startsWith(sKeyPrefix) && value instanceof String &&
                    ((String) value).trim().equals(sValue)) {
                sResult = (String) sKey;

                break;
            }
        }

        return sResult;
    }
    
}
