package com.longrise.table.view;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;

/**
 * 可以调用方法获得适配Android端和PC端的字符串，其他属性和传统EditText一样。
 * @author haiphon.huang
 */
public class EditTextTransFormAndroidPC extends EditText {

	public EditTextTransFormAndroidPC(Context context) {
		super(context);
	}

	/**
	 * @author haiphon.huang
	 * @description 获取字符串，并且把只使用与android的字符串转化成适用与PC端的。 
	 */
	public void setAndroidTextFromPC(String string) {
		// 和服务器协商好，第一段前面两个空格必须是"\u3000\u3000"，所以这里先把最前面的空格转为半角角再去掉最前面的空格。
		string = transFormEntireSpaceToHalfSpace(string, 0).trim();
		string = transStringFromPcToAndroid(string);
		setText("\u3000\u3000" + string);
	}
	
	/**
	 * @author haiphon.huang
	 * @description 获取字符串，并且把只使用与android的字符串转化成适用与PC端的。
	 * @return 转化后的字符串
	 */
	public String getAndroidTextToPC() {
		// 和服务器协商好，数据开头的空格默认是两个空格，但是提交时不用提交，他那边也不会用。
		String string = super.getText().toString().trim();
		string = transStringFromAndroidToPC(string);
		return string;
	}
	
	/**
	 * 转换字符串第i位和后面的全角字符为半角字符
	 * @param input 需要去全角的字符串
	 * @return i 只去除第几个全角  ，因为用了递归，所以需要传递一个值
	 * 假设i == 0；
	 * input = "　　234567",这里第0位和第1位时中文全角空格，则这里去掉第0位和第1位全角的空格；
	 * input = "　　　345678",这里第0位和第1位，第2位时中文全角空格，则这里去掉第0位和第1位及第2位全角的空格；
	 * 相当于转换第一个(一个或多个空格的正则表达式的空串)String regex = "\\s+";
	 */
	public static String transFormEntireSpaceToHalfSpace(String input, int i) 
	{
		if(input == null || input.equals("")) {
			return input;
		}
		// 是否继续循环
		boolean goNext = false;
		char[] c = input.toCharArray();
		// 全角空格是否在第一个空格
		if(i < 0 || i >= c.length) {
			return input;
		} else {
			for (; i < c.length; i++) 
			{
				if (c[i] == 12288) 
				{
					c[i] = (char) 32;
					if(i+1 < c.length && c[i+1] == 12288) {
						goNext = true;
					} else {
						goNext = false;
					}
					break;
				}
				if (c[i] > 65280 && c[i] < 65375)
					c[i] = (char) (c[i] - 65248);
			}
			if(goNext) {
				return transFormEntireSpaceToHalfSpace(new String(c), i + 1);
			} else {
				return new String(c);
			}
		}
	}
	
	
	/**
	 * 
	 * @author xiejun
	 * @description 
	 * @return 转换pc端的全角空格成Android端的中文空格+普通空格
	 * 服务器拿到的String数据中，如果是2个空格，那他在PC端会被当成2个全角空格，即2个汉字大小；而在Android它只当成2个半角空格，1个汉字大小，
	 * 即可以看成2个半角空格=1个全角空格，所以会小一半，所以我们要把数据源中的空格字符串转化，如果是4个空格，在pc端则是2个汉字，Android端也要显示成2个汉字大小
	 * Android端可以把"\u3000"当成一个全角空格，1个"\u3000"就是一个汉字大小。
	 * 当字符串中有6个空格，在pc端是3个汉字，在手机端则需要 3个"\u3000";
	 * 当字符串中有7个空格，在pc端是3个汉字+1个半角空个，在手机端则需要 3个"\u3000"+" ";
	 */
	public static String transStringFromPcToAndroid(String str) {
		if(TextUtils.isEmpty(str)) 
			return str;
		StringBuffer strResult = new StringBuffer();//输出结果
		int spaceNum = 0;//记录连续空格数量
		StringBuffer tempSpace = new StringBuffer(); //空格临时存储空间
		boolean isSpaceMode = false;//判断当前是否是连续空格
		for(int i = 0 ; i < str.length() ; i ++) {
			if(Character.isSpaceChar(str.charAt(i))) {//判断当前是否是空
				spaceNum++;
				isSpaceMode = true;
			}else {
				if(isSpaceMode) { //如果不为空，则判断前面是否出现过空格
					if(1 == spaceNum%2) {//判断空格数量
						if(1 != spaceNum) {
							for(int j = 0 ; j < (spaceNum - 1)/2 ; j ++) {
								tempSpace.append("\u3000");
							}
						}
						tempSpace.append(" ");
					}else {
						for(int j = 0 ; j < spaceNum/2 ; j ++) {
							tempSpace.append("\u3000");
						}
					}
					strResult.append(tempSpace);
					strResult.append(str.charAt(i));
					spaceNum = 0;
					isSpaceMode = false;
					tempSpace.setLength(0);
				}else {
					strResult.append(str.charAt(i));
				}
			}
		}
		return strResult.toString();
	}

	/**
	 * @author haiphon.huang
	 * @description 和transStringFromPcToAndroid()相反
	 * @return 将适应Android的含有中文空格和普通半角空格的字符串转成
	 */
	public static String transStringFromAndroidToPC(String str) {
		if(TextUtils.isEmpty(str)) 
			return str;
		StringBuffer strResult = new StringBuffer();
		for(int i = 0 ; i < str.length() ; i ++) {
			//判断当前是否是"\u3000"，12288代表全角空格
			if(str.charAt(i) == 12288) {
				strResult.append("  ");
			} else if(str.charAt(i) == 32){// 32代表半角空格
				if(i+1 < str.length() && str.charAt(i+1) == 32) {
					strResult.append(" ");
					i = i+1;
				} else {
					strResult.append(str.charAt(i));
				}
			} else {
				strResult.append(str.charAt(i));
			}
		}
		return strResult.toString();
	}
	
}
