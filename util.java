package simple.project.utils;

import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxl.Workbook;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import simple.WebApplicationStarter;
import simple.base.utils.ConvertSimple;
import simple.base.utils.StringSimple;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 工具类
 * 
 * @author wm
 * @date 2016年8月9日
 */
public class Utils {
	
	protected static Logger logger=LoggerFactory.getLogger(WebApplicationStarter.class);

	/**
	 * 功能：将字符串转化成long类型list
	 * 
	 * @param str
	 *                需要处理的字符串
	 * @param splitStr
	 *                分割字符 例如：, |
	 * @return list
	 */
	public static List<Long> stringToLongList(String str, String splitStr) {
		List<Long> list = new ArrayList<Long>();
		if (StringSimple.isNullOrEmpty(str)) {
			return list;
		}
		String[] array = str.split(splitStr);
		for (String id : array) {
			list.add(ConvertSimple.toLong(id));
		}
		return list;
	}

	/**
	 * 功能：将字符串转化成String类型list
	 * 
	 * @param str
	 *                需要处理的字符串
	 * @param splitStr
	 *                分割字符 例如：, |
	 * @return list
	 */
	public static List<String> stringToStringList(String str, String splitStr) {
		List<String> list = new ArrayList<String>();
		if (StringSimple.isNullOrEmpty(str)) {
			return list;
		}
		String[] array = str.split(splitStr);
		for (String id : array) {
			list.add(id.trim());
		}
		return list;
	}

	/**
	 * 把15位身份证号转换成18位身份证号码 出生月份前加"19"(20世纪才使用的15位身份证号码),最后一位加校验码
	 * 
	 * @param custNo
	 * @return
	 */
	public static String transformIdFrom15To18(String custNo) {
		String idCardNo = null;
		if (custNo != null && custNo.trim().length() == 15) {
			custNo = custNo.trim();
			StringBuffer newIdCard = new StringBuffer(custNo);
			newIdCard.insert(6, "19");
			newIdCard.append(trasformLastNo(newIdCard.toString()));
			idCardNo = newIdCard.toString();
		}
		;
		if (custNo != null && custNo.trim().length() == 18) {
			idCardNo = custNo;
		}
		;
		return idCardNo;
	}

	/**
	 * 生成身份证最后一位效验码
	 * 
	 * @param id
	 * @return
	 */
	private static String trasformLastNo(String id) {
		char pszSrc[] = id.toCharArray();
		int iS = 0;
		int iW[] = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
		char szVerCode[] = new char[] { '1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2' };
		int i;
		for (i = 0; i < id.length(); i++) {
			iS += (pszSrc[i] - '0') * iW[i];
		}
		int iY = iS % 11;

		return String.valueOf(szVerCode[iY]);
	}

	/**
	 * 根据出生日期计算周岁
	 * 
	 * @param birthDate
	 * @return 周岁数
	 */
	public static int getAgeByBirthday(Date birthDate) {
		if (birthDate == null) {
			return -1;
		}
//		int age = 0;
//		Date now = new Date();
//		SimpleDateFormat format_y = new SimpleDateFormat("yyyy");
//		SimpleDateFormat format_M = new SimpleDateFormat("MM");
//		SimpleDateFormat format_D = new SimpleDateFormat("dd");
//		String birth_year = format_y.format(birthDate);
//		String this_year = format_y.format(now);
//		String birth_month = format_M.format(birthDate);
//		String this_month = format_M.format(now);
//		String birth_day = format_D.format(birthDate);
//		String this_day = format_D.format(now);
//		// 初步，估算
//		age = Integer.parseInt(this_year) - Integer.parseInt(birth_year);
//		// 如果未到出生月份和日期，则age - 1
//		if (!(this_month.compareTo(birth_month) > 0 || (this_month.equals(birth_month) && this_day.compareTo(birth_day) >= 0))) {
//			age -= 1;
//		}
//		if (age < 0)
//			age = 0;
//		return age;
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		
		Date d1 = null;
		try {
			d1 = sdf2.parse(sdf2.format(new Date()));
		} catch (ParseException e) {
			logger.error("context",e);
			return 0;
		}
		int y1 = Integer.parseInt(sdf1.format(d1));
		int y2 = Integer.parseInt(sdf1.format(birthDate));
		
		Calendar c1 = Calendar.getInstance();
		c1.setTime(birthDate);
		c1.add(Calendar.YEAR,y1-y2);
		if(c1.getTime().getTime() < d1.getTime()){
			return y1-y2;
		}else{
			return y1-y2-1;
		}
	}

	/**
	 * 根据出生日期计算到选定时间的周岁
	 * 
	 * @param birthDate
	 * @param now
	 * @return 周岁数
	 */
	public static int getAgeByBirthdayAndTime(Date birthDate, Date now) {
		if (birthDate == null) {
			return -1;
		}
		int age = 0;
		SimpleDateFormat format_y = new SimpleDateFormat("yyyy");
		SimpleDateFormat format_M = new SimpleDateFormat("MM");
		SimpleDateFormat format_D = new SimpleDateFormat("dd");
		String birth_year = format_y.format(birthDate);
		String this_year = format_y.format(now);
		String birth_month = format_M.format(birthDate);
		String this_month = format_M.format(now);
		String birth_day = format_D.format(birthDate);
		String this_day = format_D.format(now);
		// 初步，估算
		age = Integer.parseInt(this_year) - Integer.parseInt(birth_year);
		// 如果未到出生月份和日期，则age - 1
		if (!(this_month.compareTo(birth_month) > 0 || (this_month.equals(birth_month) && this_day.compareTo(birth_day) >= 0))) {
			age -= 1;
		}
		if (age < 0)
			age = 0;
		return age;
	}

	/**
	 * 根据身份证获取信息(出生日期,年龄,性别)
	 * 
	 * @param idCard
	 * @return
	 */
	public static Map<String, Object> getInfoByIdCard(String idCard) {
		Map<String, Object> infoMap = new HashMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat resultSdf = new SimpleDateFormat("yyyy-MM-dd");
		String birth = "";
		String sexStr = "";
		int sex = 1;
		if (idCard.length() == 18) {
			birth = idCard.substring(6, 14);
			sexStr = idCard.substring(16, 17);
		} else if (idCard.length() == 15) {
			String shortBirth = idCard.substring(6, 12);
			birth = "19" + shortBirth;
			sexStr = idCard.substring(14, 15);
		}
		if (Integer.parseInt(sexStr) % 2 == 0) {
			sexStr = "女";
			sex = 2;
		} else {
			sexStr = "男";
			sex = 1;
		}
		try {
			Date birthDate = sdf.parse(birth);
			String resultDateStr = resultSdf.format(birthDate);
			int age = getAgeByBirthday(birthDate);
			infoMap.put("xbStr", sexStr);
			infoMap.put("xb", sex);
			infoMap.put("nl", age);
			infoMap.put("birthday", resultDateStr);
			infoMap.put("birthDate", birthDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return infoMap;
	}

	/**
	 * 获取参数Object数组
	 * 
	 * @param clist
	 * @return
	 * @author yc
	 * @date 2016年8月22日
	 */
	public static Object[] getSqlConditionObject(List<String> clist) {
		Object[] os = new Object[clist.size()];
		for (int i = 0; i < clist.size(); i++) {
			os[i] = clist.get(i);
		}
		return os;
	}

	/**
	 * 15位身份证转18位
	 * 
	 * @param sfzh_15
	 * @return
	 * @author wm
	 * @date 2015-10-09
	 */
	public static String getSfz_18(String sfzh_15) {
		sfzh_15 = nullToEmpty(sfzh_15);
		if (sfzh_15.length() == 18) {
			return sfzh_15;
		}
		String sfzh_17 = sfzh_15.substring(0, 6) + "19" + sfzh_15.substring(6, 15);
		String sfzh_18 = "";
		try {
			sfzh_18 = getVerifyCode(sfzh_17);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sfzh_18;
	}

	/**
	 * 获取校验码
	 * 
	 * @param idCardNumber
	 *                不带校验位的身份证号码（17位）
	 * @return 校验码
	 * @throws Exception
	 */
	private static String getVerifyCode(String idCardNumber) throws Exception {
		char[] Ai = idCardNumber.toCharArray();
		int[] Wi = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
		char[] verifyCode = { '1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2' };
		int S = 0;
		int Y;
		for (int i = 0; i < Wi.length; i++) {
			S += (Ai[i] - '0') * Wi[i];
		}
		Y = S % 11;
		return idCardNumber + verifyCode[Y];
	}

	/**
	 * 身份证号18位转15位
	 * @param idCardNo18
	 * @return
	 * @author yc
	 * @date 2016年5月17日
	 */
	public static String from18to15(String idCardNo18) {
		if (idCardNo18.length() != 18){
			return idCardNo18;
		}else {
			return idCardNo18.substring(0, 6) + idCardNo18.substring(8, 17);
		}
	}

	/**
	 * 判断给定的字符串是不是符合身份证号的要求
	 * 
	 * @param str
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isIdCardNo(String str) {

        String Ai = "";  
          
        if(null == str || str.trim().isEmpty())  
            return false;  
          
        // 判断号码的长度 15位或18位  
        if (str.length() != 15 && str.length() != 18) {  
            return false;  
        }  
        // 18位身份证前17位位数字，如果是15位的身份证则所有号码都为数字  
        if (str.length() == 18) {  
            Ai = str.substring(0, 17);  
        } else if (str.length() == 15) {  
            Ai = str.substring(0, 6) + "19" + str.substring(6, 15);  
        }  
        if (isNumeric(Ai) == false) {  
            return false;  
        }  
        // 判断出生年月是否有效  
        String strYear = Ai.substring(6, 10);// 年份  
        String strMonth = Ai.substring(10, 12);// 月份  
        String strDay = Ai.substring(12, 14);// 日期  
        if (isDate(strYear + "-" + strMonth + "-" + strDay) == false) {  
            return false;  
        }  
        GregorianCalendar gc = new GregorianCalendar();  
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");  
        try {  
            if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150  
                    || (gc.getTime().getTime() - s.parse(strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {  
                return false;  
            }  
        } catch (NumberFormatException e) {  
            e.printStackTrace();  
        } catch (java.text.ParseException e) {  
            e.printStackTrace();  
        }  
        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {  
            return false;  
        }  
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {  
            return false;  
        }  
        // 判断地区码是否有效  
        HashMap areacode = GetAreaCode();  
        // 如果身份证前两位的地区码不在Hashtable，则地区码有误  
        if (areacode.get(Ai.substring(0, 2)) == null) {  
            return false;  
        }  
        if (isVarifyCode(Ai, str) == false) {  
            return false;  
        }  
        return true;  
	}
	/**
	 * 判断字符串是否都是数字
	 * @param strnum
	 * @return
	 * @author hjj
	 * @date 2017年3月14日
	 */
	 private static boolean isNumeric(String strnum) {  
	        Pattern pattern = Pattern.compile("[0-9]*");  
	        Matcher isNum = pattern.matcher(strnum);  
	        if (isNum.matches()) {  
	            return true;  
	        } else {  
	            return false;  
	        }  
	    } 
	 
	    /* 
	     * 判断第18位校验码是否正确 第18位校验码的计算方式： 1. 对前17位数字本体码加权求和 公式为：S = Sum(Ai * Wi), i = 
	     * 0, ... , 16 其中Ai表示第i个位置上的身份证号码数字值，Wi表示第i位置上的加权因子，其各位对应的值依次为： 7 9 10 5 8 4 
	     * 2 1 6 3 7 9 10 5 8 4 2 2. 用11对计算结果取模 Y = mod(S, 11) 3. 根据模的值得到对应的校验码 
	     * 对应关系为： Y值： 0 1 2 3 4 5 6 7 8 9 10 校验码： 1 0 X 9 8 7 6 5 4 3 2 
	     */  
		private static boolean isVarifyCode(String Ai, String IDStr) {  
	        String[] VarifyCode = { "1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2" };  
	        String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2" };  
	        int sum = 0;  
	        for (int i = 0; i < 17; i++) {  
	            sum = sum + Integer.parseInt(String.valueOf(Ai.charAt(i))) * Integer.parseInt(Wi[i]);  
	        }  
	        int modValue = sum % 11;  
	        String strVerifyCode = VarifyCode[modValue];  
	        Ai = Ai + strVerifyCode;  
	        if (IDStr.length() == 18) {  
	            if (Ai.equals(IDStr) == false) {  
	                return false;  
	            }  
	        }  
	        return true;  
	    }  
	  
	    /** 
	     * 将所有地址编码保存在一个Hashtable中 
	     *  
	     * @return Hashtable 对象 
	     */  
	    @SuppressWarnings({ "rawtypes", "unchecked" })
		private static HashMap GetAreaCode() {  
	        HashMap hashtable = new HashMap();  
	        hashtable.put("11", "北京");  
	        hashtable.put("12", "天津");  
	        hashtable.put("13", "河北");  
	        hashtable.put("14", "山西");  
	        hashtable.put("15", "内蒙古");  
	        hashtable.put("21", "辽宁");  
	        hashtable.put("22", "吉林");  
	        hashtable.put("23", "黑龙江");  
	        hashtable.put("31", "上海");  
	        hashtable.put("32", "江苏");  
	        hashtable.put("33", "浙江");  
	        hashtable.put("34", "安徽");  
	        hashtable.put("35", "福建");  
	        hashtable.put("36", "江西");  
	        hashtable.put("37", "山东");  
	        hashtable.put("41", "河南");  
	        hashtable.put("42", "湖北");  
	        hashtable.put("43", "湖南");  
	        hashtable.put("44", "广东");  
	        hashtable.put("45", "广西");  
	        hashtable.put("46", "海南");  
	        hashtable.put("50", "重庆");  
	        hashtable.put("51", "四川");  
	        hashtable.put("52", "贵州");  
	        hashtable.put("53", "云南");  
	        hashtable.put("54", "西藏");  
	        hashtable.put("61", "陕西");  
	        hashtable.put("62", "甘肃");  
	        hashtable.put("63", "青海");  
	        hashtable.put("64", "宁夏");  
	        hashtable.put("65", "新疆");  
	        hashtable.put("71", "台湾");  
	        hashtable.put("81", "香港");  
	        hashtable.put("82", "澳门");  
	        hashtable.put("91", "国外");  
	        return hashtable;  
	 } 

	/**
	 * 导出excel头
	 * 
	 * @param sheetName
	 *                sheet名称
	 * @param headName
	 *                标题名称
	 * @param zbsj
	 *                制表时间
	 * @param zbr
	 *                制表人
	 * @return
	 * @author yc
	 * @date 2016年10月18日
	 */
	public static JSONObject buildExcelHead(String sheetName, String headName, String zbsj, String zbdw, String zbr) {
		JSONObject jo = new JSONObject();
		jo.put("sheetName", sheetName);
		jo.put("headName", headName);
		jo.put("zbsj", zbsj);
		jo.put("zbdw", zbdw);
		jo.put("zbr", zbr);
		return jo;
	}

	/**
	 * 创建excel单元格信息
	 * 
	 * @param x
	 * @param y
	 * @param mxs
	 * @param mys
	 * @param mxe
	 * @param mye
	 * @param text
	 * @return
	 * @author yc
	 * @date 2016年10月18日
	 */
	public static JSONObject buildExcelCell(int x, int y, int mxs, int mys, int mxe, int mye, String text) {
		JSONObject jo = new JSONObject();
		jo.put("x", x);
		jo.put("y", y);
		jo.put("mxs", mxs);
		jo.put("mys", mys);
		jo.put("mxe", mxe);
		jo.put("mye", mye);
		jo.put("text", text);
		return jo;
	}

	/**
	 * 导出公用方法
	 * 
	 * @param headMsg
	 *                头信息
	 * @param headWidth
	 *                头部宽度
	 * @param chineseStr
	 *                中文头字段
	 * @param englishStr
	 *                英文头字段名
	 * @param listmap
	 *                信息列表
	 * @param liststartY
	 *                信息列表从第几行开始
	 * @param endMsg
	 *                尾部信息
	 * @param response
	 * @throws Exception
	 * @author yc
	 * @date 2016年10月18日
	 */
	public static void excelexport(JSONObject headMsg, int headWidth, JSONArray chineseStr, List<String> englishStr, List<Map<String, Object>> listmap, int liststartY, JSONArray endMsg, HttpServletResponse response) throws Exception {
		// 取得输出流
		OutputStream os = response.getOutputStream();
		// 清空输出流
		response.reset();
		// 设定输出文件头
		response.setHeader("Content-disposition", "attachment; filename="+new String(headMsg.getString("headName").getBytes("gb2312"),"ISO8859-1")+".xls");
		// 定义输出类型
		response.setContentType("application/msexcel");
		// 建立excel文件
		WritableWorkbook wbook = Workbook.createWorkbook(os);
		// 写好头部
		WritableSheet wsheet = buildExcelHead(wbook, headMsg, headWidth);
		int endstartY = liststartY + listmap.size();
		if(endMsg != null){
			//一行合计
			if(endMsg.size()>7){
				endstartY += 1;
			}else{
				endstartY += endMsg.size();
			}
		}
		buildExcelEndUserMsg(wsheet, headMsg, endstartY, headWidth);
		WritableFont wf = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK); // 定义格式

		for (int i = 0; i < chineseStr.size(); i++) {
			JSONObject cellMsg = chineseStr.getJSONObject(i);
			wsheet.mergeCells(cellMsg.getIntValue("mxs"), cellMsg.getIntValue("mys"), cellMsg.getIntValue("mxe"), cellMsg.getIntValue("mye"));
			// 颜色
			WritableCellFormat wcf = new WritableCellFormat(wf); // 单元格定义
			wcf.setAlignment(jxl.format.Alignment.CENTRE); // 设置对齐方式
			wcf.setVerticalAlignment(VerticalAlignment.CENTRE);
			Label lable = new Label(cellMsg.getIntValue("x"), cellMsg.getIntValue("y"), cellMsg.getString("text"), wcf);
			wsheet.addCell(lable);
			lable = null;
		}

		// 写列表
		for (int i = 0; i < listmap.size(); i++) {
			Map<String, Object> t = listmap.get(i);
			for (int j = 0; j < englishStr.size(); j++) {
				Label l = new Label(j, i + liststartY, StringSimple.nullToEmpty(t.get(englishStr.get(j))));
				wsheet.addCell(l);
			}
		}

		if (endMsg != null) {
			buildExcelEnding(wsheet, endMsg, listmap.size() + liststartY);
		}

		// 写入文件
		wbook.write();
		wbook.close();
		// 关闭流
		os.close();
	}

	/**
	 * 写出excel头部
	 * 
	 * @param wbook
	 * @param jo
	 * @param width
	 * @return
	 * @author yc
	 * @date 2016年10月18日
	 */
	public static WritableSheet buildExcelHead(WritableWorkbook wbook, JSONObject jo, int width) {
		// sheetname
		WritableSheet wsheet = wbook.createSheet(jo.getString("sheetName"), 0);

		try {
			// 标题
			WritableFont headwf = new WritableFont(WritableFont.ARIAL, 24, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK); // 定义格式
			WritableCellFormat headwcf = new WritableCellFormat(headwf);
			// 设置对齐方式
			headwcf.setAlignment(jxl.format.Alignment.CENTRE);
			// 合并首行标题单元格
			wsheet.mergeCells(0, 0, width, 0);
			Label titlelabel = new Label(0, 0, jo.getString("headName"), headwcf);
			wsheet.addCell(titlelabel);

			// 制表信息
			WritableFont wf = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK); // 定义格式
			// 颜色
			WritableCellFormat zbsjwcf = new WritableCellFormat(wf); // 单元格定义
			zbsjwcf.setAlignment(jxl.format.Alignment.LEFT); // 设置对齐方式
			wsheet.mergeCells(0, 1, 2, 1);
			Label lablezbsj = new Label(0, 1, "制表单位：" + jo.getString("zbdw"), zbsjwcf);
			wsheet.addCell(lablezbsj);

			WritableCellFormat zbrwcf = new WritableCellFormat(wf); // 单元格定义
			zbrwcf.setAlignment(jxl.format.Alignment.RIGHT); // 设置对齐方式
			wsheet.mergeCells(width - 2, 1, width, 1);
			Label lablezbr = new Label(width - 2, 1, "制表日期：" + jo.getString("zbsj"), zbrwcf);
			if(width - 3 >=3){
				wsheet.mergeCells(3, 1, width - 3, 1);
			}
			wsheet.addCell(lablezbr);
		} catch (WriteException e) {
			e.printStackTrace();
		}

		return wsheet;
	}

	/**
	 * 写表格结尾的用户信息
	 * 
	 * @param wsheet
	 * @param jo
	 * @param height
	 * @param width
	 * @author yc
	 * @date 2016年10月19日
	 */
	public static void buildExcelEndUserMsg(WritableSheet wsheet, JSONObject jo, int height, int width) {
		WritableFont wf = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK); // 定义格式
		WritableCellFormat zbrwcf = new WritableCellFormat(wf); // 单元格定义
		try {
			zbrwcf.setAlignment(jxl.format.Alignment.LEFT);

			Label lablezbr = new Label(0, height, "制表人：" + jo.getString("zbr"), zbrwcf);
			wsheet.mergeCells(0, height, 1, height);
			wsheet.addCell(lablezbr);

			Label lableshr = new Label(width - 1, height, "审核人：", zbrwcf);
			wsheet.mergeCells(width - 1, height, width, height);
			wsheet.addCell(lableshr);
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成结尾信息
	 * @param x
	 * @param y
	 * @param text
	 * @return
	 * @author yc
	 * @date 2016年10月22日
	 */
	public static JSONObject buildEndJo(int x,int y,String text){
		JSONObject jo = new JSONObject();
		jo.put("x", x);
		jo.put("y", y);
		jo.put("text", text);
		return jo;
	}

	/**
	 * 写出excel底部信息
	 * 
	 * @param wsheet
	 * @param ja
	 * @param listendingY
	 * @author yc
	 * @date 2016年10月18日
	 */
	public static void buildExcelEnding(WritableSheet wsheet, JSONArray ja, int listendingY) {
		// 制表信息
		WritableFont wf = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK); // 定义格式
		// 颜色
		WritableCellFormat wcf = new WritableCellFormat(wf); // 单元格定义
		// 设置对齐方式
		try {
			wcf.setAlignment(jxl.format.Alignment.LEFT);
			wcf.setVerticalAlignment(VerticalAlignment.CENTRE);
		} catch (WriteException e1) {
			e1.printStackTrace();
		}
		
		for (int i = 0; i < ja.size(); i++) {
			JSONObject cellMsg = ja.getJSONObject(i);
			try {
				Label lable = new Label(cellMsg.getIntValue("x"), listendingY + cellMsg.getIntValue("y"), cellMsg.getString("text"), wcf);
				wsheet.addCell(lable);
			} catch (WriteException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * 格式化字符串将null值转为空字符串
	 * 
	 * @param obj
	 * @return
	 * @author wm
	 * @date 2016-02-17
	 */
	public static String nullToEmpty(Object obj) {
		if (obj == null || "null".equals(obj)) {
			return "";
		} else {
			return obj.toString().trim();
		}
	}

	/**
	 * 功能：将字符串转化成list
	 * 
	 * @param str
	 *                需要处理的字符串
	 * @param splitStr
	 *                分割字符 例如：, |
	 * @return list
	 */
	public static List<String> stringToList(String str, String splitStr) {
		List<String> list = new ArrayList<String>();
		if ("".equals(nullToEmpty(str))) {
			return list;
		}
		String[] array = str.split(splitStr);
		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
		return list;
	}

	/**
	 * 此方法用来返回字符串中前面不为0的子串
	 * 
	 * @param str
	 * @return
	 */
	public static String getBeforeZero(String str) {

		int i = 0;
		int j = 0;
		int temp = str.length();
		for (i = 0; i < str.length(); i++) {
			j = i;
			if (!str.substring(j, j + 1).equals("0")) {
				continue;
			} else {
				if (j == str.length() - 1) {
					return str.substring(0, i);
				}
				j = j + 1;
				while (str.substring(j, j + 1).equals("0")) {
					if (j == str.length() - 1) {
						temp = i;
						break;
					}
					j = j + 1;
				}
				i = j - 1;
				if (j == str.length() - 1) {
					break;
				}

			}
		}
		return str.substring(0, temp);
	}

	/**
	 * 根据结构获得实际最左边的非零元
	 * 
	 * @return
	 */
	public static String getRealLeaf(String left, String structure) {
		if ("".equals(left)) {
			return "";
		}
		String str = left;
		int length = left.length();
		String[] strus = structure.split(",");
		int[] strusum = new int[strus.length];
		int sum = 0;
		for (int i = 0; i < strus.length; i++) {
			sum += Integer.parseInt(strus[i]);
			strusum[i] = sum;
		}
		int cha = 0;
		for (int i = 0; i < strusum.length; i++) {
			if (length <= strusum[0]) {
				cha = strusum[0] - length;
				break;
			} else if (length > strusum[strusum.length - 2]) {
				cha = strusum[strusum.length - 1] - length;
				break;
			} else if (length > strusum[i] && length <= strusum[i + 1]) {
				cha = strusum[i + 1] - length;
				break;
			}

		}
		for (int i = 0; i < cha; i++) {
			str += "0";
		}
		return str;
	}

	/**
	 * 根据年月获取一个月的天数
	 * 
	 * @param year
	 * @param month
	 * @return
	 * @author wm
	 * @date 2016年9月13日
	 */
	public static int getDaysByYearMonth(int year, int month) {
		Calendar a = Calendar.getInstance();
		a.set(Calendar.YEAR, year);
		a.set(Calendar.MONTH, month - 1);
		a.set(Calendar.DATE, 1);
		a.roll(Calendar.DATE, -1);
		int maxDate = a.get(Calendar.DATE);
		return maxDate;
	}

	/**
	 * 验证是否为日期格式
	 * 
	 * @param strDate
	 * @return
	 * @author wm
	 * @date 2016年5月18日
	 */
	public static boolean isDate(String strDate) {
		if (strDate.length() < 10) {
			return false;
		}
		Pattern pattern = Pattern.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
		Matcher m = pattern.matcher(strDate);
		if (m.matches()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 手机号验证
	 * @param str
	 * @return 验证通过返回true
	 */
	public static boolean isMobile(String str) {   
        Pattern p = null;  
        Matcher m = null;  
        boolean b = false;   
        p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号  
        m = p.matcher(str);  
        b = m.matches();   
        return b;  
    }  
    /** 
     * 电话号码验证 
     *  
     * @param  str 
     * @return 验证通过返回true 
     */  
    public static boolean isPhone(String str) {   
        Pattern p1 = null,p2 = null;  
        Matcher m = null;  
        boolean b = false;    
        p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");  // 验证带区号的  
        p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");         // 验证没有区号的  
        if(str.length() > 8)  
        {   m = p1.matcher(str);  
            b = m.matches();    
        }else{  
            m = p2.matcher(str);  
            b = m.matches();   
        }    
        return b;  
    }  
    
    /**
     * 日期加上天数
     * @param date
     * @param day
     * @return
     * @author wm
     * @date 2017年6月8日
     */
    public static Date getPlusDate(Date date,int day){
    	Calendar c = Calendar.getInstance();
    	c.setTime(date);
    	c.add(Calendar.DAY_OF_MONTH, +day);
    	date = c.getTime();
    	return date;
    }
}
